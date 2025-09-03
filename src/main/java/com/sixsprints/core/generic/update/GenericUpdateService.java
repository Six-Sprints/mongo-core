package com.sixsprints.core.generic.update;

import java.util.List;
import org.springframework.data.mongodb.core.query.Criteria;
import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import jakarta.annotation.Nonnull;

/**
 * Generic service interface for updating entities in the database.
 * 
 * <p>This interface provides methods for updating existing entities with various strategies,
 * including full updates, partial updates (patch), and upsert operations. All methods ensure
 * proper validation and maintain data integrity throughout the update process.</p>
 * 
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *   <li>Full entity updates that replace all fields</li>
 *   <li>Partial updates (patch) that modify only specified fields</li>
 *   <li>Upsert operations that insert or update as needed</li>
 *   <li>Multiple update strategies (by ID, slug, or custom criteria)</li>
 *   <li>Bulk operations for efficient processing of multiple entities</li>
 *   <li>Comprehensive validation and error handling</li>
 * </ul>
 * 
 * <p><strong>Update Strategies:</strong></p>
 * <ul>
 *   <li><strong>Full Update:</strong> Replaces all fields of the entity</li>
 *   <li><strong>Patch Update:</strong> Modifies only specified fields, preserving others</li>
 *   <li><strong>Upsert:</strong> Inserts if entity doesn't exist, updates if it does</li>
 * </ul>
 * 
 * <p><strong>Usage Guidelines:</strong></p>
 * <ul>
 *   <li>Use full update methods when you want to replace all entity fields</li>
 *   <li>Use patch update methods when you want to modify only specific fields</li>
 *   <li>Use upsert methods when you're unsure if the entity exists</li>
 *   <li>All methods validate entities before updating</li>
 *   <li>Update operations maintain referential integrity and audit trails</li>
 * </ul>
 * 
 * @param <T> the type of entity extending AbstractMongoEntity
 * 
 * @author SixSprints Core Framework
 * @since 3.5.500
 */
public interface GenericUpdateService<T extends AbstractMongoEntity> {

  /**
   * Updates a single entity by its unique identifier with a full entity replacement.
   * 
   * <p>This method performs a complete update of the entity, replacing all fields
   * with the values from the provided entity object. The entity must already exist
   * in the database.</p>
   * 
   * <p><strong>Update Process:</strong></p>
   * <ol>
   *   <li>Validates the provided entity using Bean Validation annotations</li>
   *   <li>Checks if an entity with the specified ID exists</li>
   *   <li>Replaces all fields of the existing entity with the new values</li>
   *   <li>Updates audit fields (lastModifiedDate, etc.)</li>
   *   <li>Returns the updated entity</li>
   * </ol>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>If the entity exists, all its fields will be replaced with the new values</li>
   *   <li>If the entity doesn't exist, an EntityNotFoundException is thrown</li>
   *   <li>The entity ID cannot be changed through this operation</li>
   *   <li>All validation rules are applied to the updated entity</li>
   * </ul>
   * 
   * @param id the unique identifier of the entity to update (must not be null)
   * @param entity the entity containing the new values (must not be null)
   * @return the updated entity with all fields replaced
   * @throws EntityNotFoundException if no entity exists with the specified ID
   * @throws EntityInvalidException if the entity fails validation
   * @throws IllegalArgumentException if id or entity is null
   * 
   * @see #patchUpdateOneById(String, Object, String) for partial updates
   * @see #upsertOne(Object) for insert-or-update operations
   */
  T updateOneById(@Nonnull String id, @Nonnull T entity)
      throws EntityNotFoundException, EntityInvalidException;

  /**
   * Updates a single entity by its slug field with a full entity replacement.
   * 
   * <p>This method performs a complete update of the entity, replacing all fields
   * with the values from the provided entity object. The entity must already exist
   * in the database and have the specified slug.</p>
   * 
   * <p><strong>Update Process:</strong></p>
   * <ol>
   *   <li>Validates the provided entity using Bean Validation annotations</li>
   *   <li>Checks if an entity with the specified slug exists</li>
   *   <li>Replaces all fields of the existing entity with the new values</li>
   *   <li>Updates audit fields (lastModifiedDate, etc.)</li>
   *   <li>Returns the updated entity</li>
   * </ol>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>If an entity with the slug exists, all its fields will be replaced</li>
   *   <li>If no entity with the slug exists, an EntityNotFoundException is thrown</li>
   *   <li>The entity slug cannot be changed through this operation</li>
   *   <li>All validation rules are applied to the updated entity</li>
   * </ul>
   * 
   * @param slug the slug value of the entity to update (must not be null)
   * @param entity the entity containing the new values (must not be null)
   * @return the updated entity with all fields replaced
   * @throws EntityNotFoundException if no entity exists with the specified slug
   * @throws EntityInvalidException if the entity fails validation
   * @throws IllegalArgumentException if slug or entity is null
   * 
   * @see #patchUpdateOneBySlug(String, Object, String) for partial updates
   * @see #upsertOne(Object) for insert-or-update operations
   */
  T updateOneBySlug(@Nonnull String slug, @Nonnull T entity)
      throws EntityNotFoundException, EntityInvalidException;

  /**
   * Updates a single entity matching the specified criteria with a full entity replacement.
   * 
   * <p>This method performs a complete update of the first entity that matches the provided
   * criteria, replacing all fields with the values from the provided entity object.</p>
   * 
   * <p><strong>Update Process:</strong></p>
   * <ol>
   *   <li>Validates the provided entity using Bean Validation annotations</li>
   *   <li>Finds the first entity matching the specified criteria</li>
   *   <li>Replaces all fields of the matching entity with the new values</li>
   *   <li>Updates audit fields (lastModifiedDate, etc.)</li>
   *   <li>Returns the updated entity</li>
   * </ol>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>If entities match the criteria, the first matching entity will be updated</li>
   *   <li>If no entities match the criteria, an EntityNotFoundException is thrown</li>
   *   <li>Only the first matching entity is updated (use bulk operations for multiple updates)</li>
   *   <li>All validation rules are applied to the updated entity</li>
   * </ul>
   * 
   * <p><strong>Note:</strong> This method only updates the first matching entity. If multiple
   * entities match the criteria and you need to update all of them, consider using
   * bulk update operations or refining your criteria.</p>
   * 
   * @param criteria the MongoDB criteria to match against (must not be null)
   * @param entity the entity containing the new values (must not be null)
   * @return the updated entity with all fields replaced
   * @throws EntityNotFoundException if no entity matches the specified criteria
   * @throws EntityInvalidException if the entity fails validation
   * @throws IllegalArgumentException if criteria or entity is null
   * 
   * @see #patchUpdateOneByCriteria(Criteria, Object, String) for partial updates
   * @see #upsertOne(Object) for insert-or-update operations
   */
  T updateOneByCriteria(@Nonnull Criteria criteria, @Nonnull T entity)
      throws EntityNotFoundException, EntityInvalidException;

  /**
   * Performs a partial update (patch) on a single entity by its unique identifier.
   * 
   * <p>This method updates only the specified property of the entity, leaving all other
   * fields unchanged. This is useful when you want to modify only specific fields
   * without affecting the rest of the entity.</p>
   * 
   * <p><strong>Patch Update Process:</strong></p>
   * <ol>
   *   <li>Validates the provided entity using Bean Validation annotations</li>
   *   <li>Checks if an entity with the specified ID exists</li>
   *   <li>Updates only the specified property with the new value</li>
   *   <li>Preserves all other fields of the existing entity</li>
   *   <li>Updates audit fields (lastModifiedDate, etc.)</li>
   *   <li>Returns the updated entity</li>
   * </ol>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>Only the specified property is updated, all other fields remain unchanged</li>
   *   <li>If the entity doesn't exist, an EntityNotFoundException is thrown</li>
   *   <li>The property name must be valid and accessible</li>
   *   <li>Validation is applied only to the updated property</li>
   * </ul>
   * 
   * @param id the unique identifier of the entity to update (must not be null)
   * @param entity the entity containing the new value for the specified property (must not be null)
   * @param propChanged the name of the property to update (must not be null)
   * @return the updated entity with only the specified property changed
   * @throws EntityNotFoundException if no entity exists with the specified ID
   * @throws EntityInvalidException if the entity fails validation
   * @throws IllegalArgumentException if id, entity, or propChanged is null
   * 
   * @see #patchUpdateOneById(String, Object, List) for updating multiple properties
   * @see #updateOneById(String, Object) for full entity updates
   */
  T patchUpdateOneById(@Nonnull String id, @Nonnull T entity, @Nonnull String propChanged)
      throws EntityNotFoundException, EntityInvalidException;

  /**
   * Performs a partial update (patch) on a single entity by its unique identifier for multiple properties.
   * 
   * <p>This method updates only the specified properties of the entity, leaving all other
   * fields unchanged. This is useful when you want to modify multiple specific fields
   * without affecting the rest of the entity.</p>
   * 
   * <p><strong>Patch Update Process:</strong></p>
   * <ol>
   *   <li>Validates the provided entity using Bean Validation annotations</li>
   *   <li>Checks if an entity with the specified ID exists</li>
   *   <li>Updates only the specified properties with the new values</li>
   *   <li>Preserves all other fields of the existing entity</li>
   *   <li>Updates audit fields (lastModifiedDate, etc.)</li>
   *   <li>Returns the updated entity</li>
   * </ol>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>Only the specified properties are updated, all other fields remain unchanged</li>
   *   <li>If the entity doesn't exist, an EntityNotFoundException is thrown</li>
   *   <li>All property names must be valid and accessible</li>
   *   <li>Validation is applied to all updated properties</li>
   * </ul>
   * 
   * @param id the unique identifier of the entity to update (must not be null)
   * @param entity the entity containing the new values for the specified properties (must not be null)
   * @param propsChanged the list of property names to update (must not be null or empty)
   * @return the updated entity with only the specified properties changed
   * @throws EntityNotFoundException if no entity exists with the specified ID
   * @throws EntityInvalidException if the entity fails validation
   * @throws IllegalArgumentException if id, entity, or propsChanged is null/empty
   * 
   * @see #patchUpdateOneById(String, Object, String) for updating a single property
   * @see #updateOneById(String, Object) for full entity updates
   */
  T patchUpdateOneById(@Nonnull String id, @Nonnull T entity, @Nonnull List<String> propsChanged)
      throws EntityNotFoundException, EntityInvalidException;

  /**
   * Performs a partial update (patch) on a single entity by its slug field.
   * 
   * <p>This method updates only the specified property of the entity, leaving all other
   * fields unchanged. This is useful when you want to modify only specific fields
   * without affecting the rest of the entity.</p>
   * 
   * <p><strong>Patch Update Process:</strong></p>
   * <ol>
   *   <li>Validates the provided entity using Bean Validation annotations</li>
   *   <li>Checks if an entity with the specified slug exists</li>
   *   <li>Updates only the specified property with the new value</li>
   *   <li>Preserves all other fields of the existing entity</li>
   *   <li>Updates audit fields (lastModifiedDate, etc.)</li>
   *   <li>Returns the updated entity</li>
   * </ol>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>Only the specified property is updated, all other fields remain unchanged</li>
   *   <li>If no entity with the slug exists, an EntityNotFoundException is thrown</li>
   *   <li>The property name must be valid and accessible</li>
   *   <li>Validation is applied only to the updated property</li>
   * </ul>
   * 
   * @param slug the slug value of the entity to update (must not be null)
   * @param entity the entity containing the new value for the specified property (must not be null)
   * @param propChanged the name of the property to update (must not be null)
   * @return the updated entity with only the specified property changed
   * @throws EntityNotFoundException if no entity exists with the specified slug
   * @throws EntityInvalidException if the entity fails validation
   * @throws IllegalArgumentException if slug, entity, or propChanged is null
   * 
   * @see #patchUpdateOneBySlug(String, Object, List) for updating multiple properties
   * @see #updateOneBySlug(String, Object) for full entity updates
   */
  T patchUpdateOneBySlug(@Nonnull String slug, @Nonnull T entity, @Nonnull String propChanged)
      throws EntityNotFoundException, EntityInvalidException;

  /**
   * Performs a partial update (patch) on a single entity by its slug field for multiple properties.
   * 
   * <p>This method updates only the specified properties of the entity, leaving all other
   * fields unchanged. This is useful when you want to modify multiple specific fields
   * without affecting the rest of the entity.</p>
   * 
   * <p><strong>Patch Update Process:</strong></p>
   * <ol>
   *   <li>Validates the provided entity using Bean Validation annotations</li>
   *   <li>Checks if an entity with the specified slug exists</li>
   *   <li>Updates only the specified properties with the new values</li>
   *   <li>Preserves all other fields of the existing entity</li>
   *   <li>Updates audit fields (lastModifiedDate, etc.)</li>
   *   <li>Returns the updated entity</li>
   * </ol>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>Only the specified properties are updated, all other fields remain unchanged</li>
   *   <li>If no entity with the slug exists, an EntityNotFoundException is thrown</li>
   *   <li>All property names must be valid and accessible</li>
   *   <li>Validation is applied to all updated properties</li>
   * </ul>
   * 
   * @param slug the slug value of the entity to update (must not be null)
   * @param entity the entity containing the new values for the specified properties (must not be null)
   * @param propsChanged the list of property names to update (must not be null or empty)
   * @return the updated entity with only the specified properties changed
   * @throws EntityNotFoundException if no entity exists with the specified slug
   * @throws EntityInvalidException if the entity fails validation
   * @throws IllegalArgumentException if slug, entity, or propsChanged is null/empty
   * 
   * @see #patchUpdateOneBySlug(String, Object, String) for updating a single property
   * @see #updateOneBySlug(String, Object) for full entity updates
   */
  T patchUpdateOneBySlug(@Nonnull String slug, @Nonnull T entity,
      @Nonnull List<String> propsChanged) throws EntityNotFoundException, EntityInvalidException;

  /**
   * Performs a partial update (patch) on a single entity matching the specified criteria.
   * 
   * <p>This method updates only the specified property of the first entity that matches
   * the provided criteria, leaving all other fields unchanged.</p>
   * 
   * <p><strong>Patch Update Process:</strong></p>
   * <ol>
   *   <li>Validates the provided entity using Bean Validation annotations</li>
   *   <li>Finds the first entity matching the specified criteria</li>
   *   <li>Updates only the specified property with the new value</li>
   *   <li>Preserves all other fields of the existing entity</li>
   *   <li>Updates audit fields (lastModifiedDate, etc.)</li>
   *   <li>Returns the updated entity</li>
   * </ol>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>Only the specified property is updated, all other fields remain unchanged</li>
   *   <li>If no entities match the criteria, an EntityNotFoundException is thrown</li>
   *   <li>Only the first matching entity is updated</li>
   *   <li>Validation is applied only to the updated property</li>
   * </ul>
   * 
   * <p><strong>Note:</strong> This method only updates the first matching entity. If multiple
   * entities match the criteria and you need to update all of them, consider using
   * bulk update operations or refining your criteria.</p>
   * 
   * @param criteria the MongoDB criteria to match against (must not be null)
   * @param entity the entity containing the new value for the specified property (must not be null)
   * @param propChanged the name of the property to update (must not be null)
   * @return the updated entity with only the specified property changed
   * @throws EntityNotFoundException if no entity matches the specified criteria
   * @throws EntityInvalidException if the entity fails validation
   * @throws IllegalArgumentException if criteria, entity, or propChanged is null
   * 
   * @see #patchUpdateOneByCriteria(Criteria, Object, List) for updating multiple properties
   * @see #updateOneByCriteria(Criteria, Object) for full entity updates
   */
  T patchUpdateOneByCriteria(@Nonnull Criteria criteria, @Nonnull T entity,
      @Nonnull String propChanged) throws EntityNotFoundException, EntityInvalidException;

  /**
   * Performs a partial update (patch) on a single entity matching the specified criteria for multiple properties.
   * 
   * <p>This method updates only the specified properties of the first entity that matches
   * the provided criteria, leaving all other fields unchanged.</p>
   * 
   * <p><strong>Patch Update Process:</strong></p>
   * <ol>
   *   <li>Validates the provided entity using Bean Validation annotations</li>
   *   <li>Finds the first entity matching the specified criteria</li>
   *   <li>Updates only the specified properties with the new values</li>
   *   <li>Preserves all other fields of the existing entity</li>
   *   <li>Updates audit fields (lastModifiedDate, etc.)</li>
   *   <li>Returns the updated entity</li>
   * </ol>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>Only the specified properties are updated, all other fields remain unchanged</li>
   *   <li>If no entities match the criteria, an EntityNotFoundException is thrown</li>
   *   <li>Only the first matching entity is updated</li>
   *   <li>Validation is applied to all updated properties</li>
   * </ul>
   * 
   * <p><strong>Note:</strong> This method only updates the first matching entity. If multiple
   * entities match the criteria and you need to update all of them, consider using
   * bulk update operations or refining your criteria.</p>
   * 
   * @param criteria the MongoDB criteria to match against (must not be null)
   * @param entity the entity containing the new values for the specified properties (must not be null)
   * @param propsChanged the list of property names to update (must not be null or empty)
   * @return the updated entity with only the specified properties changed
   * @throws EntityNotFoundException if no entity matches the specified criteria
   * @throws EntityInvalidException if the entity fails validation
   * @throws IllegalArgumentException if criteria, entity, or propsChanged is null/empty
   * 
   * @see #patchUpdateOneByCriteria(Criteria, Object, String) for updating a single property
   * @see #updateOneByCriteria(Criteria, Object) for full entity updates
   */
  T patchUpdateOneByCriteria(@Nonnull Criteria criteria, @Nonnull T entity,
      @Nonnull List<String> propsChanged) throws EntityNotFoundException, EntityInvalidException;

  /**
   * Performs a partial update (patch) on multiple entities matching the specified criteria.
   * 
   * <p>This method updates only the specified property of each entity that matches
   * the provided criteria, leaving all other fields unchanged. This is useful when
   * you need to update a specific field across multiple entities efficiently.</p>
   * 
   * <p><strong>Bulk Patch Update Process:</strong></p>
   * <ol>
   *   <li>Validates the provided entity using Bean Validation annotations</li>
   *   <li>Finds all entities matching the specified criteria</li>
   *   <li>Updates only the specified property with the new value for each matching entity</li>
   *   <li>Preserves all other fields of each existing entity</li>
   *   <li>Updates audit fields (lastModifiedDate, etc.) for all updated entities</li>
   *   <li>Returns the count of entities that were updated</li>
   * </ol>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>Only the specified property is updated for each entity, all other fields remain unchanged</li>
   *   <li>All entities matching the criteria are updated in a single operation</li>
   *   <li>If no entities match the criteria, the operation completes successfully with 0 updates</li>
   *   <li>Validation is applied only to the updated property</li>
   *   <li>Uses efficient bulk update operations for better performance</li>
   * </ul>
   * 
   * <p><strong>Performance Considerations:</strong></p>
   * <ul>
   *   <li>This method uses efficient bulk operations for updating multiple entities</li>
   *   <li>Much faster than calling individual update methods in a loop</li>
   *   <li>Reduces database round trips and improves performance</li>
   * </ul>
   * 
   * <p><strong>Use Cases:</strong></p>
   * <ul>
   *   <li>Bulk status updates across multiple entities</li>
   *   <li>Mass field updates based on criteria</li>
   *   <li>Batch property modifications</li>
   *   <li>Administrative operations affecting multiple records</li>
   * </ul>
   * 
   * <p><strong>Note:</strong> This method updates all matching entities. If you need to update
   * only the first matching entity, consider using {@link #patchUpdateOneByCriteria(Criteria, Object, String)}
   * instead.</p>
   * 
   * @param criteria the MongoDB criteria to match against (must not be null)
   * @param entity the entity containing the new value for the specified property (must not be null)
   * @param propChanged the name of the property to update (must not be null)
   * @return the number of entities that were updated (0 or more)
   * @throws IllegalArgumentException if criteria, entity, or propChanged is null
   * 
   * @see #patchUpdateOneByCriteria(Criteria, Object, String) for updating only the first matching entity
   * @see #bulkPatchUpdateByCriteria(Criteria, Object, List) for updating multiple properties
   */
  long bulkPatchUpdateByCriteria(@Nonnull Criteria criteria, @Nonnull T entity,
      @Nonnull String propChanged);

  /**
   * Performs a partial update (patch) on multiple entities matching the specified criteria for multiple properties.
   * 
   * <p>This method updates only the specified properties of each entity that matches
   * the provided criteria, leaving all other fields unchanged. This is useful when
   * you need to update multiple specific fields across multiple entities efficiently.</p>
   * 
   * <p><strong>Bulk Patch Update Process:</strong></p>
   * <ol>
   *   <li>Validates the provided entity using Bean Validation annotations</li>
   *   <li>Finds all entities matching the specified criteria</li>
   *   <li>Updates only the specified properties with the new values for each matching entity</li>
   *   <li>Preserves all other fields of each existing entity</li>
   *   <li>Updates audit fields (lastModifiedDate, etc.) for all updated entities</li>
   *   <li>Returns the count of entities that were updated</li>
   * </ol>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>Only the specified properties are updated for each entity, all other fields remain unchanged</li>
   *   <li>All entities matching the criteria are updated in a single operation</li>
   *   <li>If no entities match the criteria, the operation completes successfully with 0 updates</li>
   *   <li>Validation is applied to all updated properties</li>
   *   <li>Uses efficient bulk update operations for better performance</li>
   * </ul>
   * 
   * <p><strong>Performance Considerations:</strong></p>
   * <ul>
   *   <li>This method uses efficient bulk operations for updating multiple entities</li>
   *   <li>Much faster than calling individual update methods in a loop</li>
   *   <li>Reduces database round trips and improves performance</li>
   *   <li>Optimized for updating multiple properties across multiple entities</li>
   * </ul>
   * 
   * <p><strong>Use Cases:</strong></p>
   * <ul>
   *   <li>Bulk updates of multiple fields across multiple entities</li>
   *   <li>Mass property updates based on criteria</li>
   *   <li>Batch multi-field modifications</li>
   *   <li>Administrative operations affecting multiple properties and records</li>
   *   <li>Data migration and cleanup operations</li>
   * </ul>
   * 
   * <p><strong>Note:</strong> This method updates all matching entities. If you need to update
   * only the first matching entity, consider using {@link #patchUpdateOneByCriteria(Criteria, Object, List)}
   * instead.</p>
   * 
   * @param criteria the MongoDB criteria to match against (must not be null)
   * @param entity the entity containing the new values for the specified properties (must not be null)
   * @param propsChanged the list of property names to update (must not be null or empty)
   * @return the number of entities that were updated (0 or more)
   * @throws IllegalArgumentException if criteria, entity, or propsChanged is null/empty
   * 
   * @see #patchUpdateOneByCriteria(Criteria, Object, List) for updating only the first matching entity
   * @see #bulkPatchUpdateByCriteria(Criteria, Object, String) for updating a single property
   */
  long bulkPatchUpdateByCriteria(@Nonnull Criteria criteria, @Nonnull T entity,
      @Nonnull List<String> propsChanged);

  /**
   * Performs an upsert operation on a single entity (insert if not exists, update if exists).
   * 
   * <p>This method provides a convenient way to handle entities that may or may not exist
   * in the database. It will insert the entity if it doesn't exist, or update it if it does.
   * The decision is typically based on the entity's unique constraints (ID, slug, etc.).</p>
   * 
   * <p><strong>Upsert Process:</strong></p>
   * <ol>
   *   <li>Validates the provided entity using Bean Validation annotations</li>
   *   <li>Checks if an entity with matching unique constraints already exists</li>
   *   <li>If entity exists: updates it with the new values</li>
   *   <li>If entity doesn't exist: inserts it as a new entity</li>
   *   <li>Updates audit fields (createdDate, lastModifiedDate, etc.)</li>
   *   <li>Returns the upserted entity</li>
   * </ol>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>If an entity with matching unique constraints exists, it will be updated</li>
   *   <li>If no matching entity exists, a new entity will be inserted</li>
   *   <li>All validation rules are applied to the entity</li>
   *   <li>Duplicate checking is performed based on the entity's unique constraints</li>
   * </ul>
   * 
   * <p><strong>Use Cases:</strong></p>
   * <ul>
   *   <li>Importing data where entities may or may not already exist</li>
   *   <li>API endpoints that handle both create and update scenarios</li>
   *   <li>Data synchronization processes</li>
   *   <li>Bulk operations where existence is uncertain</li>
   * </ul>
   * 
   * @param entity the entity to upsert (must not be null)
   * @return the upserted entity (either newly inserted or updated)
   * @throws EntityInvalidException if the entity fails validation
   * @throws IllegalArgumentException if entity is null
   * 
   * @see #bulkUpsert(List) for upserting multiple entities
   * @see #updateOneById(String, Object) for updating existing entities only
   */
  T upsertOne(@Nonnull T entity) throws EntityInvalidException;

  /**
   * Performs bulk upsert operations on multiple entities.
   * 
   * <p>This method provides an efficient way to handle multiple entities that may or may not
   * exist in the database. It will insert entities that don't exist, or update entities that do.
   * The decision is typically based on each entity's unique constraints (ID, slug, etc.).</p>
   * 
   * <p><strong>Bulk Upsert Process:</strong></p>
   * <ol>
   *   <li>Validates all provided entities using Bean Validation annotations</li>
   *   <li>For each entity, checks if an entity with matching unique constraints already exists</li>
   *   <li>If entity exists: updates it with the new values</li>
   *   <li>If entity doesn't exist: inserts it as a new entity</li>
   *   <li>Updates audit fields for all entities</li>
   *   <li>Returns the list of upserted entities</li>
   * </ol>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>Each entity is processed individually for upsert operations</li>
   *   <li>If any entity fails validation, the entire operation fails</li>
   *   <li>No partial upserts occur - either all entities are processed or none are</li>
   *   <li>All validation rules are applied to each entity</li>
   *   <li>Duplicate checking is performed based on each entity's unique constraints</li>
   * </ul>
   * 
   * <p><strong>Performance Considerations:</strong></p>
   * <ul>
   *   <li>This method prioritizes data integrity over performance</li>
   *   <li>Each entity is processed individually to ensure proper validation and hooks</li>
   *   <li>For very large batches, consider using database-specific bulk operations</li>
   * </ul>
   * 
   * <p><strong>Use Cases:</strong></p>
   * <ul>
   *   <li>Bulk data import where entities may or may not already exist</li>
   *   <li>Data synchronization processes</li>
   *   <li>API endpoints that handle batch create/update scenarios</li>
   *   <li>Migration scripts that need to handle existing data</li>
   * </ul>
   * 
   * @param list the list of entities to upsert (must not be null or contain null elements)
   * @return the list of upserted entities (mix of newly inserted and updated entities)
   * @throws EntityInvalidException if any entity fails validation
   * @throws IllegalArgumentException if list is null or contains null elements
   * 
   * @see #upsertOne(Object) for upserting a single entity
   * @see #updateOneById(String, Object) for updating existing entities only
   */
  List<T> bulkUpsert(@Nonnull List<T> list) throws EntityInvalidException;

}
