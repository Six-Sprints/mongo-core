package com.sixsprints.core.generic;

import jakarta.annotation.Nonnull;
import com.sixsprints.core.domain.AbstractMongoEntity;

/**
 * Abstract hook class that provides lifecycle methods for entity operations.
 * Subclasses can override these methods to add custom logic during entity creation,
 * updates, and other operations.
 *
 * @param <T> the type of entity that extends AbstractMongoEntity
 */
public class ServiceHook<T extends AbstractMongoEntity> {

  /**
   * Enhances an entity with additional data or modifications before any operation.
   * This method is called to enrich the entity with computed fields, default values,
   * or other enhancements that should be applied consistently.
   *
   * @param entity the entity to enhance, must not be null
   */
  protected void enhanceEntity(@Nonnull T entity) {}

  /**
   * Called before an update operation is performed on an entity.
   * This method receives both the current state of the entity and the new state
   * that will be persisted, allowing for validation or modification of the update.
   *
   * @param now the current state of the entity before update, must not be null
   * @param toBe the new state of the entity that will be persisted, must not be null
   */
  protected void preUpdate(@Nonnull T now, @Nonnull T toBe) {}

  /**
   * Called after an update operation has been successfully completed.
   * This method can be used to perform post-update actions such as logging,
   * notifications, or cleanup operations.
   *
   * @param entity the entity that was updated, must not be null
   */
  protected void postUpdate(@Nonnull T entity) {}

  /**
   * Called before an insert operation is performed on an entity.
   * This method can be used to perform pre-insert validation, set default values,
   * or apply any necessary transformations before the entity is persisted.
   *
   * @param entity the entity to be inserted, must not be null
   */
  protected void preInsert(@Nonnull T entity) {}

  /**
   * Called after an insert operation has been successfully completed.
   * This method can be used to perform post-insert actions such as logging,
   * notifications, or any operations that depend on the entity having a valid ID.
   *
   * @param entity the entity that was inserted, must not be null
   */
  protected void postInsert(@Nonnull T entity) {}

}
