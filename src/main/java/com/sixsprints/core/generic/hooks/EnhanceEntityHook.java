package com.sixsprints.core.generic.hooks;

import com.sixsprints.core.domain.AbstractMongoEntity;

public interface EnhanceEntityHook<T extends AbstractMongoEntity> {

  /**
  * Enhances an entity with additional data or modifications before any operation.
  * This method is called to enrich the entity with computed fields, default values,
  * or other enhancements that should be applied consistently.
  *
  * @param entity the entity to enhance, must not be null
  */
  void enhanceEntity(T entity);

}
