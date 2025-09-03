package com.sixsprints.core.generic.create;

import java.util.List;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import jakarta.annotation.Nonnull;

/**
 * Generic service interface for creating new entities in the database.
 * 
 * <p>This interface provides methods for inserting new entities with full validation,
 * duplicate checking, and proper lifecycle hooks. All methods ensure data integrity
 * by validating entities before insertion and checking for duplicates.</p>
 * 
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *   <li>Full entity validation using Bean Validation annotations</li>
 *   <li>Duplicate detection and prevention</li>
 *   <li>Automatic slug generation for entities that require it</li>
 *   <li>Lifecycle hooks (preCreate, postCreate) for custom business logic</li>
 *   <li>Consistent exception handling for validation and duplicate errors</li>
 * </ul>
 * 
 * <p><strong>Usage Guidelines:</strong></p>
 * <ul>
 *   <li>Use {@code insertOne()} for single entity creation with full validation</li>
 *   <li>Use {@code bulkInsert()} for multiple entities - processes each entity individually
 *       to ensure consistent validation and error handling</li>
 *   <li>All methods will throw exceptions if validation fails or duplicates are found</li>
 *   <li>Entities must not already exist in the database (use update/upsert services for existing entities)</li>
 * </ul>
 * 
 * @param <T> the type of entity extending AbstractMongoEntity
 * 
 * @author SixSprints Core Framework
 * @since 3.5.500
 */
public interface GenericCreateService<T extends AbstractMongoEntity> {

  /**
   * Inserts a single new entity into the database with full validation and duplicate checking.
   * 
   * <p>This method performs the following operations:</p>
   * <ol>
   *   <li>Executes preCreate lifecycle hook</li>
   *   <li>Validates the entity using Bean Validation annotations</li>
   *   <li>Checks for duplicate entities using the configured duplicate detection logic</li>
   *   <li>Generates slug if required by the entity</li>
   *   <li>Inserts the entity into the database</li>
   *   <li>Executes postCreate lifecycle hook</li>
   * </ol>
   * 
   * <p><strong>Validation:</strong> The entity is validated using all Bean Validation
   * annotations. If validation fails, an EntityInvalidException is thrown with
   * detailed error messages.</p>
   * 
   * <p><strong>Duplicate Detection:</strong> The method checks for existing entities
   * using the findDuplicate() implementation. If a duplicate is found, an
   * EntityAlreadyExistsException is thrown.</p>
   * 
   * <p><strong>Slug Generation:</strong> If the entity requires a slug and doesn't have one,
   * a unique slug will be automatically generated based on the entity's slug formatter.</p>
   * 
   * @param entity the entity to insert (must not be null)
   * @return the inserted entity with generated fields (ID, slug, timestamps, etc.)
   * @throws EntityInvalidException if the entity fails validation
   * @throws EntityAlreadyExistsException if a duplicate entity already exists
   * @throws IllegalArgumentException if entity is null
   * 
   * @see #bulkInsert(List) for inserting multiple entities
   */
  T insertOne(@Nonnull T entity) throws EntityAlreadyExistsException, EntityInvalidException;

  /**
   * Inserts multiple new entities into the database with full validation and duplicate checking.
   * 
   * <p>This method processes each entity individually by calling {@link #insertOne(Object)}
   * for each entity in the list. This ensures consistent validation, duplicate checking,
   * and lifecycle hooks for each entity.</p>
   * 
   * <p><strong>Processing Behavior:</strong></p>
   * <ul>
   *   <li>Entities are processed in the order they appear in the list</li>
   *   <li>Each entity goes through the same validation and duplicate checking as {@code insertOne()}</li>
   *   <li>If any entity fails validation or is a duplicate, the entire operation fails</li>
   *   <li>No partial insertions occur - either all entities are inserted or none are</li>
   * </ul>
   * 
   * <p><strong>Performance Considerations:</strong></p>
   * <ul>
   *   <li>This method prioritizes data integrity over performance</li>
   *   <li>For large batches, consider using bulk operations in the update service</li>
   *   <li>Each entity is processed individually to ensure proper validation and hooks</li>
   * </ul>
   * 
   * <p><strong>Error Handling:</strong> If any entity in the list fails validation or
   * is a duplicate, the method will throw the appropriate exception and no entities
   * will be inserted. This ensures data consistency.</p>
   * 
   * @param entities the list of entities to insert (must not be null or contain null elements)
   * @return the list of inserted entities with generated fields
   * @throws EntityInvalidException if any entity fails validation
   * @throws EntityAlreadyExistsException if any entity is a duplicate
   * @throws IllegalArgumentException if entities list is null or contains null elements
   * 
   * @see #insertOne(Object) for inserting a single entity
   */
  List<T> bulkInsert(@Nonnull List<T> entities)
      throws EntityAlreadyExistsException, EntityInvalidException;

}
