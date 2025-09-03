package com.sixsprints.core.generic;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.*;
import static org.springframework.data.mongodb.core.query.Criteria.*;
import static org.springframework.data.mongodb.core.query.Query.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import com.sixsprints.core.constants.ExceptionConstants;
import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.domain.CustomSequence;
import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.dto.SlugFormatter;
import com.sixsprints.core.exception.BaseRuntimeException;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.repository.GenericCrudRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
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

  @Autowired
  private MessageSource messageSource;

  protected abstract GenericCrudRepository<T> repository();

  protected abstract MetaData<T> metaData();

  protected SlugFormatter slugFromatter(T entity) {
    String className = entity.getClass().getSimpleName().toLowerCase();
    String prefix = className.charAt(0) + className.substring(1).replaceAll("[aeiou]", "");
    return SlugFormatter.builder().collection(className)
        .prefix(prefix.substring(0, Math.min(3, prefix.length())).toUpperCase()).build();
  }

  protected abstract T findDuplicate(T entity);

  protected Long getNextSequence(String seqName, int size) {
    CustomSequence counter = mongo.findAndModify(query(where(_ID).is(seqName)),
        new Update().inc(SEQ, size), options().returnNew(true).upsert(true), CustomSequence.class);
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

  protected EntityAlreadyExistsException alreadyExistsException(T existingEntity) {
    return EntityAlreadyExistsException.childBuilder()
        .error(ExceptionConstants.ENTITY_ALREADY_EXISTS)
        .arg(metaData().getClassType().getSimpleName()).arg(existingEntity.getSlug()).build();
  }

  protected EntityAlreadyExistsException alreadyExistsExceptionWithField(String fieldName,
      Object fieldValue) {
    return EntityAlreadyExistsException.childBuilder()
        .error(ExceptionConstants.ENTITY_ALREADY_EXISTS_WITH_FIELD)
        .arg(metaData().getClassType().getSimpleName()).arg(fieldName).arg(fieldValue).build();
  }

  protected EntityNotFoundException notFoundException(String id) {
    return EntityNotFoundException.childBuilder().error(ExceptionConstants.ENTITY_NOT_FOUND_WITH_ID)
        .arg(metaData().getClassType().getSimpleName()).arg(id).build();
  }

  protected EntityNotFoundException notFoundExceptionCriteria() {
    return EntityNotFoundException.childBuilder()
        .error(ExceptionConstants.ENTITY_NOT_FOUND_CRITERIA)
        .arg(metaData().getClassType().getSimpleName()).build();
  }

  protected List<String> checkValidity(T domain) {

    Set<ConstraintViolation<T>> violations = validator.validate(domain);
    return toHumanReadableErrors(violations);
  }

  protected List<String> toHumanReadableErrors(Set<ConstraintViolation<T>> violations) {
    if (violations == null || violations.isEmpty()) {
      return List.of();
    }
    return violations.stream().map(violation -> {
      String propertyPath = violation.getPropertyPath().toString();
      String message = violation.getMessage();
      Object invalidValue = violation.getInvalidValue();
      String valuePart =
          (invalidValue != null) ? " (was: '" + String.valueOf(invalidValue) + "')" : "";
      if (propertyPath.isEmpty()) {
        return message + valuePart;
      } else {
        return createViolationError(propertyPath, message, valuePart);
      }
    }).collect(Collectors.toList());
  }

  protected String createViolationError(String propertyPath, String message, String valuePart) {
    return propertyPath + ": " + message + valuePart;
  }

  protected BaseRuntimeException requestInvalidException(String field, Object value) {
    return BaseRuntimeException.builder().error(ExceptionConstants.REQUEST_PARAMETER_ANOMALY)
        .argument(field).httpStatus(HttpStatus.BAD_REQUEST).argument(value).build();
  }

  protected EntityInvalidException invalidException(T domain, List<String> errors) {
    return validationException(errors);
  }

  protected EntityInvalidException validationException(List<String> errors) {
    List<String> resolvedErrors =
        errors.stream().map(err -> localisedMessage(err, null)).collect(Collectors.toList());
    return EntityInvalidException.childBuilder().data(resolvedErrors)
        .error(resolvedErrors.size() > 1 ? errors.toString() : resolvedErrors.get(0)).build();
  }

  protected boolean isNew(T entity) {
    return !StringUtils.hasText(entity.getId());
  }

  protected String slug(T entity, Long nextSequence, SlugFormatter slugFromatter) {
    StringBuffer buffer = new StringBuffer(slugFromatter.getPrefix());
    if (slugFromatter.getMinimumSequenceNumber() != null) {
      nextSequence += slugFromatter.getMinimumSequenceNumber();
    }
    return buffer.append(org.apache.commons.lang3.StringUtils.leftPad(nextSequence.toString(),
        slugPaddingLength(), slugPaddingCharacter())).toString();
  }

  protected String slugPaddingCharacter() {
    return slugPaddingCharacter;
  }

  protected int slugPaddingLength() {
    return slugPaddingLength;
  }

  protected String localisedMessage(String messageKey, List<Object> args) {
    if (args == null) {
      args = List.of();
    }
    return messageSource.getMessage(messageKey, args.toArray(), LocaleContextHolder.getLocale());
  }

  protected void assertValid(Boolean expression, String field, Object value) {
    if (!expression) {
      throw requestInvalidException(field, value);
    }
  }

  private boolean shouldOverwriteSlug(T entity) {
    return isNew(entity) && !StringUtils.hasText(entity.getSlug());
  }

}
