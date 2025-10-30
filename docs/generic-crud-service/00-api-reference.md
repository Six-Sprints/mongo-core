# GenericCrudService API Reference and Domain-Specific CRUD Services

## Overview

This document provides a quick reference to all methods available in the `GenericCrudService`. For detailed documentation including parameters, return types, exceptions, examples, and best practices, refer to the linked detailed documentation.

## Domain-Specific CRUD Services

Each domain in the system has its own CRUD service that extends `GenericCrudService` (e.g., `UserCrudService`, `ProductCrudService`, `OrderCrudService`). These domain-specific services:

- **Inherit all methods** listed in this API reference
- **Are available in the system** but not exposed via documentation tools (they are locked, read-only files)
- **Provide type-specific implementations** for their respective domain entities
- **Can be used directly** with all the operations documented here

**Example**: `UserCrudService` extends `GenericCrudService<User>` and provides all these API methods working with `User` entities. You can call `userCrudService.insertOne(user)`, `userCrudService.findOneById(id)`, etc.

---

## Create Operations

For complete details, see [01-create-operations.md](01-create-operations.md)

### `T insertOne(T entity) throws EntityInvalidException, EntityAlreadyExistsException`

Inserts a single new entity with validation and duplicate checking.

### `List<T> bulkInsert(List<T> entities) throws EntityInvalidException, EntityAlreadyExistsException`

Inserts multiple entities in a transactional batch operation.

---

## Read Operations

For complete details, see [02-read-operations-basic.md](02-read-operations-basic.md)

### `List<T> findAllList()`

Retrieves all entities as a list without pagination.

### `Page<T> findAll(Pageable pageable)`

Retrieves entities with pagination and sorting support.

### `Optional<T> findOneById(String id)`

Finds a single entity by its unique identifier.

### `Optional<T> findOneBySlug(String slug)`

Finds a single entity by its slug identifier.

### `Optional<T> findOneByCriteria(Criteria criteria)`

Finds the first entity matching the specified criteria.

### `Page<T> filterByCriteria(Criteria criteria)`

Filters entities based on criteria with default pagination.

### `Page<T> filterByCriteria(Criteria criteria, Sort sort)`

Filters entities with custom sorting.

### `Page<T> filterByCriteria(Criteria criteria, Pageable pageable)`

Filters entities with custom pagination and sorting.

---

## Update Operations

For complete details, see [03-update-operations.md](03-update-operations.md)

### `T updateOneById(String id, T entity) throws EntityNotFoundException, EntityInvalidException`

Updates an existing entity by ID with full validation.

### `T updateOneBySlug(String slug, T entity) throws EntityNotFoundException, EntityInvalidException`

Updates an existing entity by slug with full validation.

### `T updateOneByCriteria(Criteria criteria, T entity) throws EntityNotFoundException, EntityInvalidException`

Updates the first entity matching criteria with full validation.

### `T patchUpdateOneById(String id, T entity, String propChanged) throws EntityNotFoundException, EntityInvalidException`

Partially updates a single property of an entity by ID.

### `T patchUpdateOneById(String id, T entity, List<String> propsChanged) throws EntityNotFoundException, EntityInvalidException`

Partially updates multiple properties of an entity by ID.

### `T patchUpdateOneBySlug(String slug, T entity, String propChanged) throws EntityNotFoundException, EntityInvalidException`

Partially updates a single property of an entity by slug.

### `T patchUpdateOneBySlug(String slug, T entity, List<String> propsChanged) throws EntityNotFoundException, EntityInvalidException`

Partially updates multiple properties of an entity by slug.

### `T patchUpdateOneByCriteria(Criteria criteria, T entity, String propChanged) throws EntityNotFoundException, EntityInvalidException`

Partially updates a single property of the first entity matching criteria.

### `T patchUpdateOneByCriteria(Criteria criteria, T entity, List<String> propsChanged) throws EntityNotFoundException, EntityInvalidException`

Partially updates multiple properties of the first entity matching criteria.

### `long bulkPatchUpdateByCriteria(Criteria criteria, T entity, String propChanged)`

Updates a single property for all entities matching criteria.

### `long bulkPatchUpdateByCriteria(Criteria criteria, T entity, List<String> propsChanged)`

Updates multiple properties for all entities matching criteria.

### `T upsertOne(T entity) throws EntityInvalidException`

Inserts or updates an entity (insert if not exists, update if exists).

### `List<T> bulkUpsert(List<T> entities) throws EntityInvalidException`

Inserts or updates multiple entities in a transactional batch.

---

## Delete Operations

For complete details, see [04-delete-operations.md](04-delete-operations.md)

### `long deleteOneById(String id)`

Permanently deletes a single entity by its unique identifier.

### `long deleteOneBySlug(String slug)`

Permanently deletes a single entity by its slug field.

### `long deleteOneByCriteria(Criteria criteria)`

Permanently deletes the first entity matching the specified criteria.

### `long bulkDeleteById(List<String> ids)`

Permanently deletes multiple entities by their unique identifiers.

### `long bulkDeleteBySlug(List<String> slugs)`

Permanently deletes multiple entities by their slug fields.

### `long bulkDeleteByCriteria(Criteria criteria)`

Permanently deletes all entities matching the specified criteria.
