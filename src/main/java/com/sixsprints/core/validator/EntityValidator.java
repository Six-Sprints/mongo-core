package com.sixsprints.core.validator;

import java.util.List;

/**
 * A strategy for validating an entity before it's persisted.
 * @param <T> The type of the entity to validate.
 */
public interface EntityValidator<T> {

  /**
   * Checks if this validator supports the given entity class.
   * @param clazz The class of the entity.
   * @return true if this validator should be applied, false otherwise.
   */
  boolean supports(Class<?> clazz);

  /**
   * Validates the entity.
   * @param entity The entity instance to validate.
   * @return A list of validation errors.
   */
  List<String> validate(T entity);
}
