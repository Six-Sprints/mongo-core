package com.sixsprints.core.generic.create;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.generic.delete.AbstractDeleteService;

public abstract class AbstractCreateService<T extends AbstractMongoEntity> extends AbstractDeleteService<T>
  implements GenericCreateService<T> {

  @Override
  public T save(T entity) {
    generateSlugIfRequired(entity);
    preSave(entity);
    entity = repository().save(entity);
    postSave(entity);
    return entity;
  }

  @Override
  public List<T> saveAll(List<T> entities) {
    generateSlugIfRequired(entities);
    return repository().saveAll(entities);
  }

  @Override
  public List<T> saveAllWithHooks(List<T> entities) {
    List<T> list = new ArrayList<>();
    for (T entity : entities) {
      list.add(save(entity));
    }
    return list;
  }

  @Override
  public T create(T domain) throws EntityAlreadyExistsException, EntityInvalidException {
    preCreate(domain);
    List<String> errors = checkValidity(domain);
    if (!CollectionUtils.isEmpty(errors)) {
      throw validationException(errors);
    }
    T fromDB = findDuplicate(domain);
    if (fromDB != null) {
      if (fromDB.getActive()) {
        throw alreadyExistsException(fromDB, domain);
      }
      delete(fromDB);
    }
    domain = save(domain);
    postCreate(domain);
    return domain;
  }

}
