package com.sixsprints.core.generic;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class GenericAbstractService<T extends AbstractMongoEntity> extends ServiceHook<T> {

  private static final String SEQ = "seq";

  private static final String _ID = "_id";

  @Autowired
  protected MongoOperations mongo;

  @Autowired
  protected Validator validator;

  @Value("${slug.padding.character:0}")
  private String slugPaddingCharacter;

  @Value("${slug.padding.length:8}")
  private int slugPaddingLength;

  protected abstract GenericRepository<T> repository();

  protected abstract MetaData<T> metaData();

  protected SlugFormatter slugFromatter(T entity) {
    String className = entity.getClass().getSimpleName().toLowerCase();
    String prefix = className.charAt(0) + className.substring(1).replaceAll("[aeiou]", "");
    return SlugFormatter.builder()
      .collection(className)
      .prefix(prefix.substring(0, Math.min(3, prefix.length())).toUpperCase())
      .build();
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
        entity.setSlug(slug(entity, nextSequence, slugFromatter));
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
        entity.setSlug(slug(entity, nextSequence, slugFromatter(entity)));
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

  protected List<String> checkValidity(T domain) {
    return new ArrayList<>();
  }

  protected EntityInvalidException invalidException(T domain) {
    return EntityInvalidException.childBuilder().build();
  }

  protected EntityInvalidException validationException(List<String> errors) {
    return EntityInvalidException.childBuilder().data(errors)
      .error("Entity is invalid. Please check the error(s) and rectify.").build();
  }

  protected boolean isNew(T entity) {
    return !StringUtils.hasText(entity.getId());
  }

  protected void validatePageAndSize(Integer pageNumber, Integer pageSize) throws BaseRuntimeException {
    if ((pageNumber == null) || (pageSize == null) || (pageNumber < 0) || (pageSize < 0)) {
      throw BaseRuntimeException.builder().httpStatus(HttpStatus.BAD_REQUEST)
        .error("Page number or page size is not valid")
        .build();
    }
  }

  protected String slug(T entity, Long nextSequence, SlugFormatter slugFromatter) {
    StringBuffer buffer = new StringBuffer(slugFromatter.getPrefix());
    if (slugFromatter.getMinimumSequenceNumber() != null) {
      nextSequence += slugFromatter.getMinimumSequenceNumber();
    }
    return buffer
      .append(org.apache.commons.lang3.StringUtils.leftPad(nextSequence.toString(), slugPaddingLength(),
        slugPaddingCharacter()))
      .toString();
  }

  protected String slugPaddingCharacter() {
    return slugPaddingCharacter;
  }

  protected int slugPaddingLength() {
    return slugPaddingLength;
  }

  private boolean shouldOverwriteSlug(T entity) {
    return isNew(entity) && !StringUtils.hasText(entity.getSlug());
  }

  protected String entityName() {
    MetaData<T> metaData = metaData();
    if (StringUtils.hasText(metaData.getEntityName())) {
      return metaData.getEntityName();
    }
    log.warn(
      "Entity name is not set for this class. Defaulting to classname. Please consider providing the entityName in the metaData() for this class.");
    return metaData.getClassType().getSimpleName();
  }

}
