package com.sixsprints.core.generic;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.domain.CustomSequence;
import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;

public abstract class GenericAbstractService<T extends AbstractMongoEntity> extends ServiceHook<T> {

  @Autowired
  protected MongoOperations mongo;

  protected abstract GenericRepository<T> repository();

  protected abstract MetaData metaData(T entity);

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
      MetaData metaData = metaData(entity);
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
    MetaData metaData = metaData(entities.get(0));
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

  protected boolean isInvalid(T domain) {
    return false;
  }

  protected EntityInvalidException invalidException(T domain) {
    return EntityInvalidException.childBuilder().build();
  }

  protected boolean isNew(T entity) {
    return StringUtils.isEmpty(entity.getId());
  }

  private String slug(int nextSequence, MetaData metaData) {
    return new StringBuffer(metaData.getPrefix()).append(nextSequence).toString();
  }

}
