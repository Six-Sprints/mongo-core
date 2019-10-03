package com.sixsprints.core.generic.create;

import java.util.List;

import com.google.common.collect.Lists;
import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.ChangeDto;
import com.sixsprints.core.enums.AuditLogAction;
import com.sixsprints.core.enums.AuditLogSource;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.generic.delete.DeleteAbstractService;

public abstract class AbstractCreateService<T extends AbstractMongoEntity> extends DeleteAbstractService<T>
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
    List<T> list = Lists.newArrayList();
    for (T entity : entities) {
      list.add(save(entity));
    }
    return list;
  }

  public T create(T domain) throws EntityAlreadyExistsException, EntityInvalidException {
    if (isInvalid(domain)) {
      throw invalidException(domain);
    }
    T fromDB = findDuplicate(domain);
    if (fromDB != null) {
      if (fromDB.getActive()) {
        throw alreadyExistsException(fromDB);
      }
      delete(fromDB);
    }
    preCreate(domain);
    domain = save(domain);
    if (domain.getActive()) {
      ChangeDto change = ChangeDto.builder().action(AuditLogAction.CREATE)
        .source(AuditLogSource.SCREEN).build();
      postCreate(domain, change);
    }
    return domain;
  }

}
