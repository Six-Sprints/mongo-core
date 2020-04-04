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
import com.sixsprints.core.dto.SlugFormatter;
import com.sixsprints.core.exception.BaseRuntimeException;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.repository.GenericRepository;

public abstract class GenericAbstractService<T extends AbstractMongoEntity> extends ServiceHook<T> {

  private static final String SEQ = "seq";

  private static final String _ID = "_id";

  @Autowired
  protected MongoOperations mongo;

  protected abstract GenericRepository<T> repository();

  protected abstract MetaData<T> metaData();

  protected SlugFormatter slugFromatter(T entity) {
    String className = entity.getClass().getSimpleName().toLowerCase();
    String prefix = className.replaceAll("[aeiou]", "");
    return SlugFormatter.builder().collection(className)
      .prefix(prefix.substring(0, Math.min(3, prefix.length())).toUpperCase()).build();
  }

  protected abstract T findDuplicate(T entity);

  protected Long getNextSequence(String seqName, int size) {
    CustomSequence counter = mongo.findAndModify(query(where(_ID).is(seqName)), new Update().inc(SEQ, size),
      options().returnNew(true).upsert(true), CustomSequence.class);
    return counter.getSeq();
  }

  protected Long getNextSequence(String seqName) {
    return getNextSequence(seqName, 1);
  }

  protected void generateSlugIfRequired(T entity) {
    if (shouldOverwriteSlug(entity)) {
      SlugFormatter slugFromatter = slugFromatter(entity);
      if (slugFromatter != null && slugFromatter.getCollection() != null) {
        Long nextSequence = getNextSequence(slugFromatter.getCollection());
        entity.setSlug(slug(nextSequence, slugFromatter));
        entity.setSequence(nextSequence);
      }
    }
  }

  protected void generateSlugIfRequired(List<T> entities) {
    if (CollectionUtils.isEmpty(entities)) {
      return;
    }
    SlugFormatter slugFromatter = slugFromatter(entities.get(0));
    if (slugFromatter == null) {
      return;
    }
    int size = entities.size();
    Long sequence = getNextSequence(slugFromatter.getCollection(), size);
    int i = 1;
    for (T entity : entities) {
      if (shouldOverwriteSlug(entity)) {
        Long nextSequence = sequence - size + i++;
        entity.setSlug(slug(nextSequence, slugFromatter(entity)));
        entity.setSequence(nextSequence);
      }
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

  private String slug(Long nextSequence, SlugFormatter slugFromatter) {
    StringBuffer buffer = new StringBuffer(slugFromatter.getPrefix());
    if (slugFromatter.getMinimumSequenceNumber() != null) {
      nextSequence += slugFromatter.getMinimumSequenceNumber();
    }
    return buffer.append(nextSequence).toString();
  }

  private boolean shouldOverwriteSlug(T entity) {
    return isNew(entity) && StringUtils.isEmpty(entity.getSlug());
  }

}
