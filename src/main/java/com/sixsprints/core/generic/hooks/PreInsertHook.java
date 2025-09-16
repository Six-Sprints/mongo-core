package com.sixsprints.core.generic.hooks;

import com.sixsprints.core.domain.AbstractMongoEntity;

public interface PreInsertHook<T extends AbstractMongoEntity> {

  /**
   * Called before an insert operation is performed on an entity.
   * This method can be used to perform pre-insert validation, set default values,
   * or apply any necessary transformations before the entity is persisted.
   *
   * @param entity the entity to be inserted, must not be null
   */
  void preInsert(T entity);

}
