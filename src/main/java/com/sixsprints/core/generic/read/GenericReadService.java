package com.sixsprints.core.generic.read;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.FilterRequestDto;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Generic read service interface providing common read operations for MongoDB entities.
 * 
 * <p>This interface provides methods for retrieving entities from the database with various
 * query strategies, filtering options, and pagination support. All methods are optimized
 * for performance and provide consistent error handling.</p>
 * 
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *   <li>Multiple query strategies (by ID, slug, or custom criteria)</li>
 *   <li>Flexible filtering with MongoDB Criteria support</li>
 *   <li>Built-in pagination and sorting capabilities</li>
 *   <li>High-level filtering through FilterRequestDto</li>
 *   <li>Performance-optimized queries with proper indexing support</li>
 *   <li>Consistent return types (Optional for single entities, Page for collections)</li>
 * </ul>
 * 
 * <p><strong>Query Strategies:</strong></p>
 * <ul>
 *   <li><strong>Direct Lookup:</strong> Find by ID or slug for exact matches</li>
 *   <li><strong>Criteria-based:</strong> Complex queries using MongoDB Criteria</li>
 *   <li><strong>Filter-based:</strong> High-level filtering through FilterRequestDto</li>
 * </ul>
 * 
 * <p><strong>Usage Guidelines:</strong></p>
 * <ul>
 *   <li>Use direct lookup methods (findOneById, findOneBySlug) for exact matches</li>
 *   <li>Use criteria-based methods for complex queries and filtering</li>
 *   <li>Use FilterRequestDto methods for user-facing search functionality</li>
 *   <li>Always use pagination for large datasets to avoid performance issues</li>
 *   <li>Consider database indexing for frequently queried fields</li>
 * </ul>
 * 
 * @param <T> the type of entity extending AbstractMongoEntity
 * 
 * @author SixSprints Core Framework
 * @since 3.5.500
 */
public interface GenericReadService<T extends AbstractMongoEntity> {

  /**
   * Retrieves all entities from the database as a list.
   * 
   * <p><strong>Warning:</strong> This method loads all entities into memory at once.
   * Only use this method for small datasets or when you specifically need all entities
   * in memory. For large datasets, use paginated methods to avoid performance issues
   * and potential OutOfMemoryError.</p>
   * 
   * <p><strong>Performance Considerations:</strong></p>
   * <ul>
   *   <li>Loads all entities into memory simultaneously</li>
   *   <li>May cause performance issues with large datasets</li>
   *   <li>No pagination or result limiting</li>
   *   <li>Consider using {@link #findAll(Pageable)} for better performance</li>
   * </ul>
   * 
   * @return a list containing all entities in the database
   * 
   * @see #findAll(Pageable) for paginated retrieval
   * @see #filterByCriteria(Criteria) for filtered retrieval
   */
  List<T> findAllList();

  /**
   * Retrieves all entities with pagination and sorting settings.
   * 
   * <p>This method provides efficient retrieval of entities with built-in pagination
   * and sorting capabilities. It's the recommended approach for retrieving large
   * datasets as it avoids loading all entities into memory at once.</p>
   * 
   * <p><strong>Features:</strong></p>
   * <ul>
   *   <li>Pagination support with configurable page size</li>
   *   <li>Sorting support for multiple fields</li>
   *   <li>Memory-efficient processing</li>
   *   <li>Consistent page metadata (total elements, total pages, etc.)</li>
   * </ul>
   * 
   * @param pageable the pagination and sorting information (must not be null)
   * @return a page containing entities based on the provided pageable settings
   * 
   * @see #filterByCriteria(Criteria, Pageable) for filtered paginated retrieval
   */
  Page<T> findAll(@Nonnull Pageable pageable);

  /**
   * Finds a single entity by its unique identifier.
   * 
   * <p>This method performs a direct lookup by the entity's unique identifier.
   * It's the most efficient way to retrieve a specific entity when you know its ID.</p>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>Returns the entity if found with the specified ID</li>
   *   <li>Returns empty Optional if no entity exists with the ID</li>
   *   <li>Returns empty Optional if the ID is null</li>
   *   <li>Uses database index for optimal performance</li>
   * </ul>
   * 
   * @param id the unique identifier of the entity (may be null)
   * @return an Optional containing the entity if found, empty otherwise
   * 
   * @see #findOneBySlug(String) for lookup by slug
   * @see #findOneByCriteria(Criteria) for complex queries
   */
  Optional<T> findOneById(@Nullable String id);

  /**
   * Finds a single entity by its slug field.
   * 
   * <p>This method performs a direct lookup by the entity's slug field.
   * Slugs are typically human-readable identifiers used in URLs and are usually
   * unique within the entity type.</p>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>Returns the entity if found with the specified slug</li>
   *   <li>Returns empty Optional if no entity exists with the slug</li>
   *   <li>Returns empty Optional if the slug is null</li>
   *   <li>Uses database index for optimal performance (if slug is indexed)</li>
   * </ul>
   * 
   * @param slug the slug value to search for (may be null)
   * @return an Optional containing the entity if found, empty otherwise
   * 
   * @see #findOneById(String) for lookup by ID
   * @see #findOneByCriteria(Criteria) for complex queries
   */
  Optional<T> findOneBySlug(@Nullable String slug);

  /**
   * Finds a single entity matching the specified criteria.
   * 
   * <p>This method provides flexible querying capabilities using MongoDB Criteria.
   * It's useful when you need to find entities based on complex conditions that
   * cannot be expressed through simple ID or slug lookups.</p>
   * 
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>Returns the first entity that matches the criteria</li>
   *   <li>Returns empty Optional if no entities match the criteria</li>
   *   <li>The order of results is not guaranteed unless explicitly sorted</li>
   *   <li>Performance depends on the complexity of the criteria and available indexes</li>
   * </ul>
   * 
   * <p><strong>Use Cases:</strong></p>
   * <ul>
   *   <li>Finding entities by multiple field conditions</li>
   *   <li>Range queries (date ranges, numeric ranges)</li>
   *   <li>Pattern matching and text searches</li>
   *   <li>Complex logical conditions (AND, OR, NOT)</li>
   * </ul>
   * 
   * @param criteria the MongoDB criteria to match against (must not be null)
   * @return an Optional containing the first matching entity, empty if none found
   * 
   * @see #filterByCriteria(Criteria) for retrieving multiple matching entities
   * @see #findOneById(String) for simple ID-based lookup
   */
  Optional<T> findOneByCriteria(@Nonnull Criteria criteria);

  /**
   * Filters entities based on the specified criteria with default pagination.
   * 
   * <p>This method provides filtered retrieval with built-in pagination using default
   * page size settings. It's useful when you need to retrieve multiple entities
   * matching specific criteria without loading all results into memory.</p>
   * 
   * <p><strong>Features:</strong></p>
   * <ul>
   *   <li>Applies MongoDB criteria for filtering</li>
   *   <li>Uses default pagination settings</li>
   *   <li>Returns paginated results with metadata</li>
   *   <li>Memory-efficient for large result sets</li>
   * </ul>
   * 
   * @param criteria the MongoDB criteria to filter entities (must not be null)
   * @return a page containing filtered entities with default page size
   * 
   * @see #filterByCriteria(Criteria, Sort) for filtered retrieval with custom sorting
   * @see #filterByCriteria(Criteria, Pageable) for filtered retrieval with custom pagination
   */
  Page<T> filterByCriteria(@Nonnull Criteria criteria);

  /**
   * Filters entities based on the specified criteria with custom sorting.
   * 
   * <p>This method provides filtered retrieval with custom sorting capabilities.
   * It allows you to specify how the filtered results should be ordered while
   * using default pagination settings.</p>
   * 
   * <p><strong>Features:</strong></p>
   * <ul>
   *   <li>Applies MongoDB criteria for filtering</li>
   *   <li>Custom sorting on multiple fields</li>
   *   <li>Supports ascending and descending order</li>
   *   <li>Uses default pagination settings</li>
   *   <li>Memory-efficient for large result sets</li>
   * </ul>
   * 
   * @param criteria the MongoDB criteria to filter entities (must not be null)
   * @param sort the sorting configuration (must not be null)
   * @return a page containing filtered and sorted entities with default page size
   * 
   * @see #filterByCriteria(Criteria) for filtered retrieval without custom sorting
   * @see #filterByCriteria(Criteria, Pageable) for filtered retrieval with custom pagination and sorting
   */
  Page<T> filterByCriteria(@Nonnull Criteria criteria, @Nonnull Sort sort);

  /**
   * Filters entities based on the specified criteria with custom pagination and sorting.
   * 
   * <p>This method provides the most flexible filtering capabilities, allowing you to
   * specify both custom pagination settings and sorting criteria. It's the recommended
   * approach for complex queries that require specific result ordering and pagination.</p>
   * 
   * <p><strong>Features:</strong></p>
   * <ul>
   *   <li>Applies MongoDB criteria for filtering</li>
   *   <li>Custom pagination with configurable page size</li>
   *   <li>Custom sorting on multiple fields</li>
   *   <li>Supports ascending and descending order</li>
   *   <li>Memory-efficient for large result sets</li>
   *   <li>Complete control over result presentation</li>
   * </ul>
   * 
   * @param criteria the MongoDB criteria to filter entities (must not be null)
   * @param pageable the pagination and sorting information (must not be null)
   * @return a page containing filtered entities based on the provided pageable settings
   * 
   * @see #filterByCriteria(Criteria) for filtered retrieval with default settings
   * @see #filterByCriteria(Criteria, Sort) for filtered retrieval with custom sorting only
   */
  Page<T> filterByCriteria(@Nonnull Criteria criteria, @Nonnull Pageable pageable);

  /**
   * Filters entities based on the provided FilterRequestDto with pagination.
   * 
   * <p>This method provides a high-level filtering interface that abstracts away the
   * complexity of MongoDB Criteria. It's designed for user-facing search functionality
   * and provides a more intuitive way to build complex queries.</p>
   * 
   * <p><strong>Features:</strong></p>
   * <ul>
   *   <li>High-level query building through FilterRequestDto</li>
   *   <li>Built-in pagination and sorting support</li>
   *   <li>User-friendly search interface</li>
   *   <li>Automatic query optimization</li>
   *   <li>Consistent API for different entity types</li>
   * </ul>
   * 
   * <p><strong>Use Cases:</strong></p>
   * <ul>
   *   <li>User-facing search functionality</li>
   *   <li>API endpoints that accept search parameters</li>
   *   <li>Dynamic filtering based on user input</li>
   *   <li>Complex search forms with multiple criteria</li>
   * </ul>
   * 
   * @param filterRequestDto the filter request containing search criteria, pagination, and sorting (must not be null)
   * @return a page containing filtered entities based on the filter request
   * 
   * @see #filterByCriteria(Criteria, Pageable) for low-level criteria-based filtering
   */
  Page<T> filterByFilterRequestDto(@Nonnull FilterRequestDto filterRequestDto);

}
