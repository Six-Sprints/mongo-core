package com.sixsprints.core.generic.hooks;

import com.sixsprints.core.domain.AbstractMongoEntity;

public interface PostUpdateHook<T extends AbstractMongoEntity> {

  /**
   * Called after an update operation has been successfully completed.
   * This method can be used to perform post-update actions such as logging,
   * notifications, or cleanup operations.
   *
   * @param entity the entity that was updated, must not be null
   */
  void postUpdate(T entity);

}
