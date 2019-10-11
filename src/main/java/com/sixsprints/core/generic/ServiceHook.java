package com.sixsprints.core.generic;

import com.sixsprints.core.domain.AbstractMongoEntity;

public class ServiceHook<T extends AbstractMongoEntity> {

  protected void preSave(T entity) {
  }

  protected void postSave(T entity) {
  }

  protected void preUpdate(T entity) {
  }

  protected void postUpdate(T entity) {
  }

  protected void preCreate(T entity) {
  }

  protected void postCreate(T entity) {
  }

}
