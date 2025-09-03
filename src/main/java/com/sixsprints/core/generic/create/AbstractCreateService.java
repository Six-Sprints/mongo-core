package com.sixsprints.core.generic.create;

import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.generic.delete.AbstractDeleteService;

public abstract class AbstractCreateService<T extends AbstractMongoEntity>
    extends AbstractDeleteService<T> implements GenericCreateService<T> {

  @Override
  public T insertOne(T entity) throws EntityAlreadyExistsException, EntityInvalidException {
    assertValid(entity != null, "entity", entity);
    enhanceEntity(entity);
    preInsert(entity);
    List<String> errors = checkValidity(entity);
    if (!CollectionUtils.isEmpty(errors)) {
      throw validationException(errors);
    }
    T fromDB = findDuplicate(entity);
    if (fromDB != null) {
      throw alreadyExistsException(fromDB);
    }
    generateSlugIfRequired(entity);
    entity = repository().insert(entity);
    postInsert(entity);
    return entity;
  }

  @Override
  @Transactional
  @SuppressWarnings("null")
  public List<T> bulkInsert(List<T> entities)
      throws EntityAlreadyExistsException, EntityInvalidException {
    assertValid(entities != null, "entities", entities);
    List<T> list = new ArrayList<>();
    for (T entity : entities) {
      list.add(insertOne(entity));
    }
    return list;
  }

}
