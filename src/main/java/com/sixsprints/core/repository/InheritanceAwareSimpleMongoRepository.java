package com.sixsprints.core.repository;

import java.io.Serializable;
import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import com.sixsprints.core.utils.InheritanceMongoUtil;

public class InheritanceAwareSimpleMongoRepository<T, ID extends Serializable> extends SimpleMongoRepository<T, ID> {

  private final MongoOperations mongoOperations;
  private final MongoEntityInformation<T, ID> entityInformation;

  private final Document classCriteriaDocument;
  private final Criteria inheritanceCriteria;

  public InheritanceAwareSimpleMongoRepository(MongoEntityInformation<T, ID> metadata,
    MongoOperations mongoOperations) {
    super(metadata, mongoOperations);
    this.mongoOperations = mongoOperations;
    this.entityInformation = metadata;

    inheritanceCriteria = InheritanceMongoUtil.generate(entityInformation.getJavaType());
    classCriteriaDocument = inheritanceCriteria == null ? null : inheritanceCriteria.getCriteriaObject();
  }

  @Override
  public List<T> findAll() {
    return inheritanceCriteria != null ? mongoOperations.find(new Query().addCriteria(inheritanceCriteria),
      entityInformation.getJavaType(),
      entityInformation.getCollectionName())
      : super.findAll();
  }

  @Override
  public long count() {
    return inheritanceCriteria != null ? mongoOperations.getCollection(
      entityInformation.getCollectionName()).countDocuments(
        classCriteriaDocument)
      : super.count();
  }

}
