# Update Operations

## Overview

Provides methods to modify existing entities in the database. All update operations automatically handle:

- **Validation**: Ensures updated data is valid (for validated methods)
- **Duplicate Checking**: Prevents creating duplicate records
- **Audit Fields**: Automatically updates `dateModified` and `lastModifiedBy` fields
- **Exception Handling**: Thrown exceptions are automatically converted to the correct HTTP error responses

**Note**: You don't need to manually set `dateModified` or `lastModifiedBy` fields in your update operations - these are automatically managed by the framework.

---

## Methods

### `updateOneById(String id, T entity)`

Updates an existing entity by ID with full validation.

- **Parameters**:
  - `id` (String) - The unique identifier of the entity to update
  - `entity` (T) - The entity containing new values
- **Returns**: `T` - The updated entity
- **Throws**:
  - `EntityNotFoundException`: If no entity exists with the given ID (returns **HTTP 404 Not Found**)
  - `EntityInvalidException`: If the entity fails validation (returns **HTTP 400 Bad Request**)

**Example**:

```java
String userId = "507f1f77bcf86cd799439011";

User updatedUser = User.builder()
    .name("John Smith")
    .email("john.smith@example.com")
    .department("Engineering")
    .role("SENIOR_DEVELOPER")
    .build();

User result = userCrudService.updateOneById(userId, updatedUser);
System.out.println("User updated successfully: " + result.getName());
```

### `updateOneBySlug(String slug, T entity)`

Updates an existing entity by slug with full validation.

- **Parameters**:
  - `slug` (String) - The slug identifier of the entity to update
  - `entity` (T) - The entity containing new values
- **Returns**: `T` - The updated entity
- **Throws**:
  - `EntityNotFoundException`: If no entity exists with the given slug (returns **HTTP 404 Not Found**)
  - `EntityInvalidException`: If the entity fails validation (returns **HTTP 400 Bad Request**)

**Example**:

```java
String userSlug = "john-doe";

User updatedUser = User.builder()
    .name("John Smith")
    .email("john.smith@example.com")
    .build();

User result = userCrudService.updateOneBySlug(userSlug, updatedUser);
System.out.println("User updated successfully: " + result.getName());
```

### `updateOneByCriteria(Criteria criteria, T entity)`

Updates the first entity matching criteria with full validation.

- **Parameters**:
  - `criteria` (Criteria) - MongoDB query criteria to match entities
  - `entity` (T) - The entity containing new values
- **Returns**: `T` - The updated entity
- **Throws**:
  - `EntityNotFoundException`: If no entity matches the criteria (returns **HTTP 404 Not Found**)
  - `EntityInvalidException`: If the entity fails validation (returns **HTTP 400 Bad Request**)

**Example**:

```java
Criteria criteria = Criteria.where("department").is("Engineering")
    .and("age").gte(25);

User updatedUser = User.builder()
    .role("TEAM_LEAD")
    .salary(75000)
    .build();

User result = userCrudService.updateOneByCriteria(criteria, updatedUser);
System.out.println("Updated engineer: " + result.getName());
```

### `patchUpdateOneById(String id, T entity, String propChanged)`

Partially updates a single property of an entity by ID.

- **Parameters**:
  - `id` (String) - The unique identifier of the entity to update
  - `entity` (T) - The entity containing the new value for the property
  - `propChanged` (String) - The name of the property to update
- **Returns**: `T` - The updated entity
- **Throws**:
  - `EntityNotFoundException`: If no entity exists with the given ID (returns **HTTP 404 Not Found**)
  - `EntityInvalidException`: If the entity fails validation (returns **HTTP 400 Bad Request**)

**Example**:

```java
String userId = "507f1f77bcf86cd799439011";

User userUpdate = User.builder()
    .department("IT")
    .build();

User updatedUser = userCrudService.patchUpdateOneById(userId, userUpdate, User.Fields.department);
System.out.println("User department updated to: " + updatedUser.getDepartment());
```

### `patchUpdateOneById(String id, T entity, List<String> propsChanged)`

Partially updates multiple properties of an entity by ID.

- **Parameters**:
  - `id` (String) - The unique identifier of the entity to update
  - `entity` (T) - The entity containing the new values for the properties
  - `propsChanged` (List<String>) - List of property names to update
- **Returns**: `T` - The updated entity
- **Throws**:
  - `EntityNotFoundException`: If no entity exists with the given ID (returns **HTTP 404 Not Found**)
  - `EntityInvalidException`: If the entity fails validation (returns **HTTP 400 Bad Request**)

**Example**:

```java
String userId = "507f1f77bcf86cd799439011";

User userUpdate = User.builder()
    .department("Engineering")
    .role("TEAM_LEAD")
    .salary(75000)
    .build();

List<String> changedProps = Arrays.asList(User.Fields.department, User.Fields.role, User.Fields.salary);
User updatedUser = userCrudService.patchUpdateOneById(userId, userUpdate, changedProps);

System.out.println("User updated:");
System.out.println("- Department: " + updatedUser.getDepartment());
System.out.println("- Role: " + updatedUser.getRole());
System.out.println("- Salary: " + updatedUser.getSalary());
```

### `patchUpdateOneBySlug(String slug, T entity, String propChanged)`

Partially updates a single property of an entity by slug.

- **Parameters**:
  - `slug` (String) - The slug identifier of the entity to update
  - `entity` (T) - The entity containing the new value for the property
  - `propChanged` (String) - The name of the property to update
- **Returns**: `T` - The updated entity
- **Throws**:
  - `EntityNotFoundException`: If no entity exists with the given slug (returns **HTTP 404 Not Found**)
  - `EntityInvalidException`: If the entity fails validation (returns **HTTP 400 Bad Request**)

**Example**:

```java
String userSlug = "john-doe";

User userUpdate = User.builder()
    .status("ONLINE")
    .build();

User updatedUser = userCrudService.patchUpdateOneBySlug(userSlug, userUpdate, User.Fields.status);
System.out.println("User status updated to: " + updatedUser.getStatus());
```

### `patchUpdateOneBySlug(String slug, T entity, List<String> propsChanged)`

Partially updates multiple properties of an entity by slug.

- **Parameters**:
  - `slug` (String) - The slug identifier of the entity to update
  - `entity` (T) - The entity containing the new values for the properties
  - `propsChanged` (List<String>) - List of property names to update
- **Returns**: `T` - The updated entity
- **Throws**:
  - `EntityNotFoundException`: If no entity exists with the given slug (returns **HTTP 404 Not Found**)
  - `EntityInvalidException`: If the entity fails validation (returns **HTTP 400 Bad Request**)

**Example**:

```java
String userSlug = "john-doe";

User userUpdate = User.builder()
    .lastLogin(LocalDateTime.now())
    .loginCount(15)
    .status("ONLINE")
    .build();

List<String> propsToUpdate = Arrays.asList(User.Fields.lastLogin, User.Fields.loginCount, User.Fields.status);
User updatedUser = userCrudService.patchUpdateOneBySlug(userSlug, userUpdate, propsToUpdate);
System.out.println("User activity updated successfully");
```

### `patchUpdateOneByCriteria(Criteria criteria, T entity, String propChanged)`

Partially updates a single property of the first entity matching criteria.

- **Parameters**:
  - `criteria` (Criteria) - MongoDB query criteria to match entities
  - `entity` (T) - The entity containing the new value for the property
  - `propChanged` (String) - The name of the property to update
- **Returns**: `T` - The updated entity
- **Throws**:
  - `EntityNotFoundException`: If no entity matches the criteria (returns **HTTP 404 Not Found**)
  - `EntityInvalidException`: If the entity fails validation (returns **HTTP 400 Bad Request**)

**Example**:

```java
Criteria criteria = Criteria.where("department").is("Engineering")
    .and("role").is("JUNIOR");

User userUpdate = User.builder()
    .role("MID")
    .build();

User updatedUser = userCrudService.patchUpdateOneByCriteria(criteria, userUpdate, User.Fields.role);
System.out.println("Promoted engineer: " + updatedUser.getName());
```

### `patchUpdateOneByCriteria(Criteria criteria, T entity, List<String> propsChanged)`

Partially updates multiple properties of the first entity matching criteria.

- **Parameters**:
  - `criteria` (Criteria) - MongoDB query criteria to match entities
  - `entity` (T) - The entity containing the new values for the properties
  - `propsChanged` (List<String>) - List of property names to update
- **Returns**: `T` - The updated entity
- **Throws**:
  - `EntityNotFoundException`: If no entity matches the criteria (returns **HTTP 404 Not Found**)
  - `EntityInvalidException`: If the entity fails validation (returns **HTTP 400 Bad Request**)

**Example**:

```java
Criteria criteria = Criteria.where("department").is("Engineering")
    .and("level").is("JUNIOR");

User userUpdate = User.builder()
    .level("MID")
    .salary(60000)
    .updatedAt(LocalDateTime.now())
    .build();

List<String> propsToUpdate = Arrays.asList(User.Fields.level, User.Fields.salary, User.Fields.updatedAt);
User updatedUser = userCrudService.patchUpdateOneByCriteria(criteria, userUpdate, propsToUpdate);
System.out.println("Engineer promoted: " + updatedUser.getName());
```

### `bulkPatchUpdateByCriteria(Criteria criteria, T entity, String propChanged)`

Updates a single property for all entities matching criteria.

- **Parameters**:
  - `criteria` (Criteria) - MongoDB query criteria to match entities
  - `entity` (T) - The entity containing the new value for the property
  - `propChanged` (String) - The name of the property to update
- **Returns**: `long` - The number of entities that were updated
- **Throws**: None

**Key Behavior**: This operation updates **all entities** matching the criteria in a single efficient bulk operation.

**Example**:

```java
Criteria criteria = Criteria.where("department").is("Engineering")
    .and("level").is("JUNIOR");

User updateData = User.builder()
    .level("MID")
    .build();

long updatedCount = userCrudService.bulkPatchUpdateByCriteria(criteria, updateData, User.Fields.level);
System.out.println("Promoted " + updatedCount + " junior engineers to mid-level");
```

### `bulkPatchUpdateByCriteria(Criteria criteria, T entity, List<String> propsChanged)`

Updates multiple properties for all entities matching criteria.

- **Parameters**:
  - `criteria` (Criteria) - MongoDB query criteria to match entities
  - `entity` (T) - The entity containing the new values for the properties
  - `propsChanged` (List<String>) - List of property names to update
- **Returns**: `long` - The number of entities that were updated
- **Throws**: None

**Key Behavior**: This operation updates **all entities** matching the criteria in a single efficient bulk operation.

**Example**:

```java
Criteria criteria = Criteria.where("department").is("Legacy")
    .and("active").is(true);

User updateData = User.builder()
    .department("Maintenance")
    .level("SENIOR")
    .updatedAt(LocalDateTime.now())
    .build();

List<String> propsToUpdate = Arrays.asList(User.Fields.department, User.Fields.level, User.Fields.updatedAt);
long updatedCount = userCrudService.bulkPatchUpdateByCriteria(criteria, updateData, propsToUpdate);
System.out.println("Migrated " + updatedCount + " users from Legacy to Maintenance department");
```

### `upsertOne(T entity)`

Inserts or updates an entity (insert if not exists, update if exists).

- **Parameters**: `entity` (T) - The entity to upsert
- **Returns**: `T` - The upserted entity
- **Throws**:
  - `EntityInvalidException`: If the entity fails validation (returns **HTTP 400 Bad Request**)

**Example**:

```java
User user = User.builder()
    .name("John Doe")
    .email("john@example.com")
    .department("Engineering")
    .build();

User result = userCrudService.upsertOne(user);
System.out.println("User upserted with ID: " + result.getId());
```

### `bulkUpsert(List<T> entities)`

Inserts or updates multiple entities.

- **Parameters**: `entities` (List<T>) - The list of entities to upsert
- **Returns**: `List<T>` - The list of upserted entities
- **Throws**:
  - `EntityInvalidException`: If any entity fails validation (returns **HTTP 400 Bad Request**)

**Key Behavior**: This operation is **transactional**. If any entity fails validation, the entire batch is rolled back.

**Example**:

```java
List<User> users = Arrays.asList(
    User.builder()
        .name("Alice")
        .email("alice@example.com")
        .department("Engineering")
        .build(),
    User.builder()
        .name("Bob")
        .email("bob@example.com")
        .department("Marketing")
        .build()
);

List<User> upsertedUsers = userCrudService.bulkUpsert(users);
System.out.println("Successfully upserted " + upsertedUsers.size() + " users");
```

---

## Best Practices

1. **Use `updateOneById()` for user input**: When updating entities from user input or external sources
2. **Use `patchUpdateOneById()` for partial updates**: When updating only specific properties
3. **Use `upsertOne()` for uncertain existence**: When you're unsure if the entity exists
4. **Use `bulkPatchUpdateByCriteria()` for bulk updates**: When updating multiple entities matching criteria efficiently
5. **Use `bulkUpsert()` for batch operations**: When processing multiple entities that may or may not exist
6. **Handle exceptions appropriately**: Always catch and handle update exceptions

## Common Update Patterns

### User Profile Update

```java
public ResponseEntity<User> updateUserProfile(String userId, UserProfileUpdateDto profileUpdate) {
    User userUpdate = User.builder()
        .name(profileUpdate.getName())
        .email(profileUpdate.getEmail())
        .phone(profileUpdate.getPhone())
        .build();

    List<String> changedProps = Arrays.asList(User.Fields.name, User.Fields.email, User.Fields.phone);
User updatedUser = userCrudService.patchUpdateOneById(userId, userUpdate, changedProps);

    return ResponseEntity.ok(updatedUser);
}
```

### Bulk Department Migration

```java
public void migrateDepartment(String fromDept, String toDept) {
    Criteria criteria = Criteria.where("department").is(fromDept)
        .and("active").is(true);

    User updateData = User.builder()
        .department(toDept)
        .build();

    List<String> propsToUpdate = Arrays.asList(User.Fields.department);

    // Efficiently update all users in the department
    long updatedCount = userCrudService.bulkPatchUpdateByCriteria(criteria, updateData, propsToUpdate);
    System.out.println("Migrated " + updatedCount + " users from " + fromDept + " to " + toDept);
}
```

### Bulk Status Updates

```java
public void updateUserStatuses(String department, String newStatus) {
    Criteria criteria = Criteria.where("department").is(department)
        .and("status").ne(newStatus);

    User statusUpdate = User.builder()
        .status(newStatus)
        .build();

    // Update status for all users in department who don't already have the new status
    long updatedCount = userCrudService.bulkPatchUpdateByCriteria(criteria, statusUpdate, User.Fields.status);
    System.out.println("Updated status to " + newStatus + " for " + updatedCount + " users in " + department);
}
```
