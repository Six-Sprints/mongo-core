package com.sixsprints.core.generic;

import com.sixsprints.core.domain.AbstractMongoEntity;

public class ServiceHook<T extends AbstractMongoEntity> {

  protected void enhanceEntity(T entity) {}

  protected void preUpdate(T now, T toBe) {}

  protected void postUpdate(T entity) {}

  protected void preInsert(T entity) {}

  protected void postInsert(T entity) {}

}
