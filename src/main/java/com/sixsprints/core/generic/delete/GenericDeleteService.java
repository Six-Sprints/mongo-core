package com.sixsprints.core.generic.delete;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;

import com.sixsprints.core.domain.AbstractMongoEntity;
import jakarta.annotation.Nonnull;

/**
 * Generic service interface for deleting entities from the database.
 * 
 * <p>This interface provides methods for removing entities with various deletion strategies,
 * including hard delete operations that permanently remove entities from the database.
 * All methods ensure proper cleanup and maintain referential integrity.</p>
 * 
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *   <li>Hard delete operations that permanently remove entities</li>
 *   <li>Support for single and bulk deletion operations</li>
 *   <li>Multiple deletion strategies (by ID, slug, or custom criteria)</li>
 *   <li>Consistent error handling for non-existent entities</li>
 *   <li>Efficient bulk operations for multiple entities</li>
 * </ul>
 * 
 * <p><strong>Usage Guidelines:</strong></p>
 * <ul>
 *   <li>Use single delete methods for individual entity removal</li>
 *   <li>Use bulk delete methods for removing multiple entities efficiently</li>
 *   <li>All delete operations are permanent - entities cannot be recovered</li>
 *   <li>Consider using soft delete operations if data recovery is needed</li>
 *   <li>Ensure proper authorization before performing delete operations</li>
 * </ul>
 * 
 * <p><strong>Warning:</strong> All methods in this interface perform hard deletes,
 * which permanently remove entities from the database. Use with caution and ensure
 * proper backup strategies are in place.</p>
 * 
 * @param <T> the type of entity extending AbstractMongoEntity
 * 
 * @author SixSprints Core Framework
 * @since 3.5.500
 */
public interface GenericDeleteService<T extends AbstractMongoEntity> {

  /**
   * Permanently deletes a single entity by its unique identifier.
   * 
   * <p>This method performs a hard delete operation, permanently removing the entity
   * from the database. The entity cannot be recovered after this operation.</p>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>If the entity exists, it will be permanently removed</li>
   *   <li>If the entity does not exist, the operation completes silently</li>
   *   <li>No exception is thrown for non-existent entities</li>
   * </ul>
   * 
   * @param id the unique identifier of the entity to delete (must not be null)
   * @return the number of entities deleted (0 or 1)
   * 
   * @see #bulkDeleteById(List) for deleting multiple entities by ID
   */
  long deleteOneById(@Nonnull String id);

  /**
   * Permanently deletes a single entity by its slug field.
   * 
   * <p>This method performs a hard delete operation, permanently removing the entity
   * with the specified slug from the database. The entity cannot be recovered after this operation.</p>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>If an entity with the slug exists, it will be permanently removed</li>
   *   <li>If no entity with the slug exists, the operation completes silently</li>
   *   <li>No exception is thrown for non-existent entities</li>
   * </ul>
   * 
   * @param slug the slug value of the entity to delete (must not be null)
   * @return the number of entities deleted (0 or 1)
   * 
   * @see #bulkDeleteBySlug(List) for deleting multiple entities by slug
   */
  long deleteOneBySlug(@Nonnull String slug);

  /**
   * Permanently deletes a single entity matching the specified criteria.
   * 
   * <p>This method performs a hard delete operation, permanently removing the first entity
   * that matches the provided criteria from the database. The entity cannot be recovered after this operation.</p>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>If entities match the criteria, the first matching entity will be permanently removed</li>
   *   <li>If no entities match the criteria, the operation completes silently</li>
   *   <li>No exception is thrown when no matching entities are found</li>
   * </ul>
   * 
   * <p><strong>Note:</strong> This method only deletes the first matching entity. If multiple
   * entities match the criteria, use {@link #bulkDeleteByCriteria(Criteria)} to delete all matches.</p>
   * 
   * @param criteria the MongoDB criteria to match against (must not be null)
   * @return the number of entities deleted (0 or 1)
   * 
   * @see #bulkDeleteByCriteria(Criteria) for deleting all entities matching criteria
   */
  long deleteOneByCriteria(@Nonnull Criteria criteria);

  /**
   * Permanently deletes multiple entities by their unique identifiers.
   * 
   * <p>This method performs a hard delete operation, permanently removing all entities
   * with the specified IDs from the database. The entities cannot be recovered after this operation.</p>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>All entities with matching IDs will be permanently removed</li>
   *   <li>Non-existent IDs are ignored (no exception thrown)</li>
   *   <li>The operation is atomic - either all entities are deleted or none are</li>
   * </ul>
   * 
   * <p><strong>Performance:</strong> This method uses an efficient bulk operation
   * that deletes multiple entities in a single database operation.</p>
   * 
   * @param ids the list of unique identifiers of entities to delete (must not be null)
   * @return the number of entities deleted (0 to the size of the input list)
   * 
   * @see #deleteOneById(String) for deleting a single entity by ID
   */
  long bulkDeleteById(@Nonnull List<String> ids);

  /**
   * Permanently deletes all entities matching the specified criteria.
   * 
   * <p>This method performs a hard delete operation, permanently removing all entities
   * that match the provided criteria from the database. The entities cannot be recovered after this operation.</p>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>All entities matching the criteria will be permanently removed</li>
   *   <li>If no entities match the criteria, the operation completes silently</li>
   *   <li>No exception is thrown when no matching entities are found</li>
   * </ul>
   * 
   * <p><strong>Performance:</strong> This method uses an efficient bulk operation
   * that deletes all matching entities in a single database operation.</p>
   * 
   * <p><strong>Warning:</strong> Use this method with extreme caution as it can delete
   * a large number of entities. Always test your criteria thoroughly before execution.</p>
   * 
   * @param criteria the MongoDB criteria to match against (must not be null)
   * @return the number of entities deleted (0 or more)
   * 
   * @see #deleteOneByCriteria(Criteria) for deleting only the first matching entity
   */
  long bulkDeleteByCriteria(@Nonnull Criteria criteria);

  /**
   * Permanently deletes multiple entities by their slug fields.
   * 
   * <p>This method performs a hard delete operation, permanently removing all entities
   * with the specified slugs from the database. The entities cannot be recovered after this operation.</p>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>All entities with matching slugs will be permanently removed</li>
   *   <li>Non-existent slugs are ignored (no exception thrown)</li>
   *   <li>The operation is atomic - either all entities are deleted or none are</li>
   * </ul>
   * 
   * <p><strong>Performance:</strong> This method uses an efficient bulk operation
   * that deletes multiple entities in a single database operation.</p>
   * 
   * @param slugs the list of slug values of entities to delete (must not be null)
   * @return the number of entities deleted (0 to the size of the input list)
   * 
   * @see #deleteOneBySlug(String) for deleting a single entity by slug
   */
  long bulkDeleteBySlug(@Nonnull List<String> slugs);

}
