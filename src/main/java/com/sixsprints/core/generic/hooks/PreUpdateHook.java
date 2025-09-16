package com.sixsprints.core.generic.hooks;

import com.sixsprints.core.domain.AbstractMongoEntity;

public interface PreUpdateHook<T extends AbstractMongoEntity> {

  /**
   * Called before an update operation is performed on an entity.
   * This method receives both the current state of the entity and the new state
   * that will be persisted, allowing for validation or modification of the update.
   *
   * @param now the current state of the entity before update, must not be null
   * @param toBe the new state of the entity that will be persisted, must not be null
   */
  void preUpdate(T now, T toBe);

}
