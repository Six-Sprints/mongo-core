package com.sixsprints.core.utils.csv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCursor;

public class ParseMasterDataValue extends CellProcessorAdaptor
  implements StringCellProcessor {

  private static final String MESSAGE = "Master Data is invalid!";

  private String collectionName;

  private String column;

  private MongoOperations mongo;

  @Autowired
  public ParseMasterDataValue(String collectionName, String column, MongoOperations mongo) {
    super();
    this.collectionName = collectionName;
    this.column = column;
    this.mongo = mongo;
  }

  @Autowired
  public ParseMasterDataValue(CellProcessor next, String collectionName, String column, MongoOperations mongo) {
    super(next);
    this.collectionName = collectionName;
    this.column = column;
    this.mongo = mongo;
  }

  public String validateData(String data) {
    DistinctIterable<String> iterable = mongo.getCollection(collectionName).distinct(column, String.class);
    MongoCursor<String> cursor = iterable.iterator();
    while (cursor.hasNext()) {
      String next = cursor.next();
      if (data.equalsIgnoreCase(next)) {
        return next;
      }
    }
    return null;
  }

  @Override
  public <X> X execute(Object value, CsvContext context) {
    if (value != null) {
      String strVal = value.toString();
      String data = validateData(strVal);
      if (data != null) {
        return next.execute(data, context);
      }
      throw new SuperCsvCellProcessorException(MESSAGE, context, this);
    }
    return next.execute(null, context);
  }
}