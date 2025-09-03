# Read Operations - Basic CRUD

## Overview

Provides methods to retrieve entities from the database. All read operations automatically handle:

- **Pagination**: Efficient handling of large datasets
- **Sorting**: Flexible ordering of results
- **Filtering**: Complex query capabilities using MongoDB Criteria
- **Exception Handling**: Thrown exceptions are automatically converted to the correct HTTP error responses

---

## Methods

### `findAllList()`

Retrieves all entities as a list without pagination.

- **Parameters**: None
- **Returns**: `List<T>` - List of all entities
- **Use Case**: Small datasets or when you need all entities in memory

**Example**:

```java
List<User> allUsers = userCrudService.findAllList();
System.out.println("Total users: " + allUsers.size());
```

### `findAll(Pageable pageable)`

Retrieves entities with pagination and sorting.

- **Parameters**: `pageable` (Pageable) - Pagination and sorting configuration
- **Returns**: `Page<T>` - Paginated result with metadata
- **Use Case**: Displaying entities in paginated lists/tables

**Example**:

```java
Pageable pageable = PageRequest.of(0, 20, Sort.by(User.Fields.name).ascending());
Page<User> userPage = userCrudService.findAll(pageable);

System.out.println("Total users: " + userPage.getTotalElements());
System.out.println("Current page: " + userPage.getNumber());
System.out.println("Users on this page: " + userPage.getContent().size());
```

### `findOneById(String id)`

Finds a single entity by its unique identifier.

- **Parameters**: `id` (String) - The unique identifier (may be null)
- **Returns**: `Optional<T>` - Optional containing the entity if found
- **Use Case**: Retrieving specific entities by ID

**Example**:

```java
String userId = "507f1f77bcf86cd799439011";
Optional<User> userOpt = userCrudService.findOneById(userId);

if (userOpt.isPresent()) {
    User user = userOpt.get();
    System.out.println("Found user: " + user.getName());
} else {
    System.out.println("User not found");
}

// Alternative: Throw EntityNotFoundException if not found
User user = userCrudService.findOneById(userId)
    .orElseThrow(() -> EntityNotFoundException.childBuilder()
        .error("User not found with ID: " + userId)
        .build());
```

### `findOneBySlug(String slug)`

Finds a single entity by its slug identifier.

- **Parameters**: `slug` (String) - The slug identifier (may be null)
- **Returns**: `Optional<T>` - Optional containing the entity if found
- **Use Case**: Retrieving entities by human-readable slug identifiers

**Example**:

```java
String userSlug = "john-doe";
Optional<User> userOpt = userCrudService.findOneBySlug(userSlug);

userOpt.ifPresent(user -> {
    System.out.println("Found user by slug: " + user.getName());
});

// Alternative: Throw EntityNotFoundException if not found
User user = userCrudService.findOneBySlug(userSlug)
    .orElseThrow(() -> EntityNotFoundException.childBuilder()
        .error("User not found with slug: " + userSlug)
        .build());
```

### `findOneByCriteria(Criteria criteria)`

Finds a single entity matching the specified criteria.

- **Parameters**: `criteria` (Criteria) - MongoDB query criteria
- **Returns**: `Optional<T>` - Optional containing the first matching entity
- **Use Case**: Complex queries that cannot be expressed through simple ID or slug lookups

**Example**:

```java
Criteria criteria = Criteria.where(User.Fields.department).is("Engineering")
    .and(User.Fields.age).gte(25);

Optional<User> userOpt = userCrudService.findOneByCriteria(criteria);

userOpt.ifPresent(user -> {
    System.out.println("Found engineer: " + user.getName());
});

// Alternative: Throw EntityNotFoundException if not found
User user = userCrudService.findOneByCriteria(criteria)
    .orElseThrow(() -> EntityNotFoundException.childBuilder()
        .error("No engineer found matching criteria")
        .build());
```

### `filterByCriteria(Criteria criteria)`

Filters entities based on criteria with default pagination.

- **Parameters**: `criteria` (Criteria) - MongoDB query criteria
- **Returns**: `Page<T>` - Paginated result of filtered entities
- **Use Case**: Filtered retrieval with default pagination settings

**Example**:

```java
Criteria criteria = Criteria.where(User.Fields.active).is(true);
Page<User> activeUsers = userCrudService.filterByCriteria(criteria);

System.out.println("Found " + activeUsers.getTotalElements() + " active users");
```

### `filterByCriteria(Criteria criteria, Sort sort)`

Filters entities with custom sorting.

- **Parameters**:
  - `criteria` (Criteria) - MongoDB query criteria
  - `sort` (Sort) - Sorting configuration
- **Returns**: `Page<T>` - Paginated and sorted result
- **Use Case**: Filtered retrieval with custom sorting

**Example**:

```java
Criteria criteria = Criteria.where(User.Fields.department).is("Engineering");
Sort sort = Sort.by(User.Fields.name).ascending();

Page<User> engineers = userCrudService.filterByCriteria(criteria, sort);
System.out.println("Engineers sorted by name: " + engineers.getContent().size());
```

### `filterByCriteria(Criteria criteria, Pageable pageable)`

Filters entities with custom pagination and sorting.

- **Parameters**:
  - `criteria` (Criteria) - MongoDB query criteria
  - `pageable` (Pageable) - Pagination and sorting configuration
- **Returns**: `Page<T>` - Paginated and sorted result
- **Use Case**: Complex queries with specific pagination and sorting requirements

**Example**:

```java
Criteria criteria = Criteria.where(User.Fields.age).gte(25).lte(50);
Pageable pageable = PageRequest.of(0, 10, Sort.by(User.Fields.age).descending());

Page<User> users = userCrudService.filterByCriteria(criteria, pageable);
System.out.println("Found " + users.getTotalElements() + " users aged 25-50");
```

---

## Best Practices

1. **Use Pagination**: Always use paginated methods for large datasets to avoid memory issues
2. **Use Optional**: Methods return `Optional<T>` for safe handling of potentially missing entities
3. **Index Your Fields**: Ensure frequently queried fields have proper database indexes
4. **Criteria Queries**: Use `findOneByCriteria()` and `filterByCriteria()` for complex queries
5. **Performance**: Consider caching frequently accessed entities by ID or slug

## Common Query Patterns

### Finding by Multiple Conditions

```java
Criteria criteria = Criteria.where(User.Fields.department).is("Engineering")
    .and(User.Fields.active).is(true)
    .and(User.Fields.age).gte(25);

Optional<User> userOpt = userCrudService.findOneByCriteria(criteria);
```

### Range Queries

```java
Criteria criteria = Criteria.where(User.Fields.salary).gte(50000).lte(100000);
Page<User> users = userCrudService.filterByCriteria(criteria);
```

### Pattern Matching

```java
Criteria criteria = Criteria.where(User.Fields.email).regex(".*@company\\.com");
Page<User> users = userCrudService.filterByCriteria(criteria);
```

### Complex Sorting

```java
Sort sort = Sort.by(User.Fields.department).ascending()
    .and(Sort.by(User.Fields.name).ascending());

Pageable pageable = PageRequest.of(0, 20, sort);
Page<User> users = userCrudService.findAll(pageable);
```
