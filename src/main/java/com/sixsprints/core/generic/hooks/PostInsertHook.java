package com.sixsprints.core.generic.hooks;

import com.sixsprints.core.domain.AbstractMongoEntity;

public interface PostInsertHook<T extends AbstractMongoEntity> {

  /**
   * Called after an insert operation has been successfully completed.
   * This method can be used to perform post-insert actions such as logging,
   * notifications, or any operations that depend on the entity having a valid ID.
   *
   * @param entity the entity that was inserted, must not be null
   */
  void postInsert(T entity);

}
