package com.sixsprints.core.generic;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.domain.CustomSequence;
import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.exception.BaseRuntimeException;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;

public abstract class GenericAbstractService<T extends AbstractMongoEntity> extends ServiceHook<T> {

  @Autowired
  protected MongoOperations mongo;

  protected abstract GenericRepository<T> repository();

  protected abstract MetaData<T> metaData(T entity);

  protected MetaData<T> metaData() {
    return metaData(null);
  }

  protected abstract T findDuplicate(T entity);

  protected int getNextSequence(String seqName, int size) {
    CustomSequence counter = mongo.findAndModify(query(where("_id").is(seqName)), new Update().inc("seq", size),
      options().returnNew(true).upsert(true), CustomSequence.class);
    return counter.getSeq();
  }

  protected int getNextSequence(String seqName) {
    return getNextSequence(seqName, 1);
  }

  protected void generateSlugIfRequired(T entity) {
    if (isNew(entity) && StringUtils.isEmpty(entity.getSlug())) {
      MetaData<T> metaData = metaData(entity);
      if (metaData != null && metaData.getCollection() != null) {
        int nextSequence = getNextSequence(metaData.getCollection());
        entity.setSlug(slug(nextSequence, metaData));
      }
    }
  }

  protected void generateSlugIfRequired(List<T> entities) {
    if (CollectionUtils.isEmpty(entities)) {
      return;
    }
    MetaData<T> metaData = metaData(entities.get(0));
    if (metaData == null) {
      return;
    }
    int size = entities.size();
    int sequence = getNextSequence(metaData.getCollection(), size);
    int i = 1;
    for (T entity : entities) {
      entity.setSlug(slug(sequence - size + i++, metaData));
    }

  }

  protected EntityAlreadyExistsException alreadyExistsException(T domain) {
    return EntityAlreadyExistsException.childBuilder().build();
  }

  protected EntityNotFoundException notFoundException(String string) {
    return EntityNotFoundException.childBuilder().build();
  }

  protected boolean isInvalid(T domain) {
    return false;
  }

  protected EntityInvalidException invalidException(T domain) {
    return EntityInvalidException.childBuilder().build();
  }

  protected boolean isNew(T entity) {
    return StringUtils.isEmpty(entity.getId());
  }

  protected void validatePageAndSize(Integer pageNumber, Integer pageSize) throws BaseRuntimeException {
    if ((pageNumber == null) || (pageSize == null) || (pageNumber < 0) || (pageSize < 0)) {
      throw BaseRuntimeException.builder().httpStatus(HttpStatus.BAD_REQUEST)
        .error("Page number or Page size is not valid")
        .build();
    }
  }

  private String slug(int nextSequence, MetaData<T> metaData) {
    return new StringBuffer(metaData.getPrefix()).append(nextSequence).toString();
  }

}
