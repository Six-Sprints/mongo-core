# Delete Operations

## Overview

Provides methods to permanently remove entities from the database. All delete operations automatically handle:

- **Hard Deletion**: Permanently removes entities from the database
- **Bulk Operations**: Efficient deletion of multiple entities
- **Multiple Strategies**: Delete by ID, slug, or custom criteria
- **Exception Handling**: Thrown exceptions are automatically converted to the correct HTTP error responses

**Warning**: All methods perform hard deletes that permanently remove entities. Use with caution and ensure proper backup strategies are in place.

---

## Methods

### `deleteOneById(String id)`

Permanently deletes a single entity by its unique identifier.

- **Parameters**: `id` (String) - The unique identifier (must not be null)
- **Returns**: `long` - Number of entities deleted (0 or 1)
- **Throws**: None (completes silently if entity doesn't exist)

**Example**:

```java
String userId = "507f1f77bcf86cd799439011";
long deletedCount = userCrudService.deleteOneById(userId);

if (deletedCount > 0) {
    System.out.println("User deleted successfully");
} else {
    System.out.println("User not found");
}
```

### `deleteOneBySlug(String slug)`

Permanently deletes a single entity by its slug field.

- **Parameters**: `slug` (String) - The slug value (must not be null)
- **Returns**: `long` - Number of entities deleted (0 or 1)
- **Throws**: None (completes silently if entity doesn't exist)

**Example**:

```java
String userSlug = "john-doe";
long deletedCount = userCrudService.deleteOneBySlug(userSlug);

if (deletedCount > 0) {
    System.out.println("User with slug '" + userSlug + "' deleted successfully");
} else {
    System.out.println("User with slug '" + userSlug + "' not found");
}
```

### `deleteOneByCriteria(Criteria criteria)`

Permanently deletes the first entity matching the specified criteria.

- **Parameters**: `criteria` (Criteria) - MongoDB query criteria (must not be null)
- **Returns**: `long` - Number of entities deleted (0 or 1)
- **Throws**: None (completes silently if no entities match)

**Example**:

```java
Criteria criteria = Criteria.where(User.Fields.department).is("Legacy")
    .and(User.Fields.active).is(false);

long deletedCount = userCrudService.deleteOneByCriteria(criteria);

if (deletedCount > 0) {
    System.out.println("First legacy user deleted successfully");
} else {
    System.out.println("No legacy users found to delete");
}
```

### `bulkDeleteById(List<String> ids)`

Permanently deletes multiple entities by their unique identifiers.

- **Parameters**: `ids` (List<String>) - List of unique identifiers (must not be null)
- **Returns**: `long` - Number of entities deleted (0 to size of input list)
- **Throws**: None (ignores non-existent IDs)

**Example**:

```java
List<String> userIdsToDelete = Arrays.asList(
    "507f1f77bcf86cd799439011",
    "507f1f77bcf86cd799439012",
    "507f1f77bcf86cd799439013"
);

long deletedCount = userCrudService.bulkDeleteById(userIdsToDelete);
System.out.println("Deleted " + deletedCount + " out of " + userIdsToDelete.size() + " users");
```

### `bulkDeleteBySlug(List<String> slugs)`

Permanently deletes multiple entities by their slug fields.

- **Parameters**: `slugs` (List<String>) - List of slug values (must not be null)
- **Returns**: `long` - Number of entities deleted (0 to size of input list)
- **Throws**: None (ignores non-existent slugs)

**Example**:

```java
List<String> userSlugsToDelete = Arrays.asList(
    "john-doe",
    "jane-smith",
    "bob-johnson"
);

long deletedCount = userCrudService.bulkDeleteBySlug(userSlugsToDelete);
System.out.println("Deleted " + deletedCount + " out of " + userSlugsToDelete.size() + " users by slug");
```

### `bulkDeleteByCriteria(Criteria criteria)`

Permanently deletes all entities matching the specified criteria.

- **Parameters**: `criteria` (Criteria) - MongoDB query criteria (must not be null)
- **Returns**: `long` - Number of entities deleted (0 or more)
- **Throws**: None (completes silently if no entities match)

**Example**:

```java
Criteria criteria = Criteria.where(User.Fields.active).is(false)
    .and(User.Fields.lastLogin).lt(LocalDateTime.now().minusYears(2));

long deletedCount = userCrudService.bulkDeleteByCriteria(criteria);
System.out.println("Deleted " + deletedCount + " inactive users who haven't logged in for 2+ years");
```

---

## Common Delete Patterns

### User Account Deletion

```java
public ResponseEntity<String> deleteUserAccount(String userId) {
    // Check if user exists first
    Optional<User> userOpt = userCrudService.findOneById(userId);
    if (userOpt.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    // Check for dependencies
    if (hasActiveSessions(userId) || hasDependentData(userId)) {
        return ResponseEntity.badRequest().body("Cannot delete user with active sessions or dependent data");
    }

    // Perform deletion
    long deletedCount = userCrudService.deleteOneById(userId);

    if (deletedCount > 0) {
        auditService.logUserDeletion(userId, "Account deleted by admin");
        return ResponseEntity.ok("User account deleted successfully");
    } else {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Failed to delete user account");
    }
}
```

### Bulk Cleanup Operations

```java
public void performDataCleanup() {
    logger.info("Starting data cleanup process");

    // Delete old inactive users
    Criteria oldInactiveCriteria = Criteria.where(User.Fields.active).is(false)
        .and(User.Fields.lastLogin).lt(LocalDateTime.now().minusYears(5));

    long deletedCount = userCrudService.bulkDeleteByCriteria(oldInactiveCriteria);
    logger.info("Deleted " + deletedCount + " old inactive users");

    // Delete test users
    Criteria testUserCriteria = Criteria.where(User.Fields.email).regex("test@.*");
    deletedCount = userCrudService.bulkDeleteByCriteria(testUserCriteria);
    logger.info("Deleted " + deletedCount + " test users");
}
```

### Department Cleanup

```java
public void deleteUsersByDepartment(String department, boolean forceDelete) {
    // Find users in the department
    Criteria deptCriteria = Criteria.where(User.Fields.department).is(department);
    Page<User> usersInDept = userCrudService.filterByCriteria(deptCriteria);

    if (usersInDept.isEmpty()) {
        logger.info("No users found in department: " + department);
        return;
    }

    logger.info("Found " + usersInDept.getTotalElements() + " users in department: " + department);

    if (forceDelete) {
        // Hard delete all users in department
        long deletedCount = userCrudService.bulkDeleteByCriteria(deptCriteria);
        logger.info("Hard deleted " + deletedCount + " users in department: " + department);
    } else {
        // Delete by IDs for more control
        List<String> userIds = usersInDept.getContent().stream()
            .map(User::getId)
            .collect(Collectors.toList());

        long deletedCount = userCrudService.bulkDeleteById(userIds);
        logger.info("Deleted " + deletedCount + " users in department: " + department);
    }
}
```

---

## Best Practices

1. **Verify Before Delete**: Check if entities exist before attempting deletion
2. **Check Dependencies**: Ensure no other entities depend on the data being deleted
3. **Use Bulk Operations**: Use bulk methods for multiple entities to improve performance
4. **Log Deletions**: Log deletion operations for audit and debugging purposes
5. **Handle Results**: Check return values to verify successful deletion
6. **Index Fields**: Ensure fields used in delete criteria have proper database indexes

## Performance Considerations

1. **Bulk Operations**: Use `bulkDelete*` methods for multiple entities
2. **Indexed Fields**: Ensure frequently used delete criteria fields are indexed
3. **Batch Processing**: Process large deletions in batches to avoid timeouts
4. **Criteria Optimization**: Use efficient criteria to minimize database load
5. **Transaction Support**: Consider using transactions for critical delete operations
