# Create Operations

## Overview

Provides methods to safely insert new entities into the database. All create operations automatically perform:

- **Bean Validation**: Ensures the entity's data is valid.
- **Duplicate Checking**: Prevents inserting duplicate records.
- **Slug Generation**: Automatically creates a slug if required.
- **Lifecycle Hooks**: Executes `preInsert` and `postInsert` logic.
- **Exception Handling**: Thrown exceptions are automatically converted to the correct HTTP error responses.

---

## Methods

### `insertOne(T entity)`

Inserts a single new entity after performing all validation and duplicate checks.

- **Parameters**: `entity` (T) - The entity to insert (must not be `null`).
- **Returns**: `T` - The inserted entity with generated fields (e.g., ID, slug, timestamps).
- **Throws**:
  - `EntityInvalidException`: If the entity fails validation (returns **HTTP 400 Bad Request**).
  - `EntityAlreadyExistsException`: If a duplicate entity is found (returns **HTTP 409 Conflict**).

**Example**:

```java
User newUser = User.builder()
    .name("John Doe")
    .email("john@example.com")
    .age(30)
    .build();

User createdUser = userCrudService.insertOne(newUser);
System.out.println("User created with ID: " + createdUser.getId());
```

### `bulkInsert(List<T> entities)`

Inserts a list of new entities. Each entity undergoes the same validation and duplicate checks as `insertOne`.

- **Parameters**: `entities` (List\<T\>) - The list of entities to insert.
- **Returns**: `List<T>` - The list of inserted entities with generated fields.
- **Throws**:
  - `EntityInvalidException`: If any entity fails validation (returns **HTTP 400 Bad Request**).
  - `EntityAlreadyExistsException`: If any duplicate entity is found (returns **HTTP 409 Conflict**).

**Key Behavior**: This operation is **transactional**. If any entity in the list fails validation or is a duplicate, the entire batch is rolled back, and no entities are inserted.

**Example**:

```java
List<User> newUsers = Arrays.asList(
    User.builder()
        .name("Alice")
        .email("alice@example.com")
        .age(25)
        .build(),
    User.builder()
        .name("Bob")
        .email("bob@example.com")
        .age(30)
        .build()
);

List<User> createdUsers = userCrudService.bulkInsert(newUsers);
System.out.println("Successfully created " + createdUsers.size() + " users.");
```

---

## Best Practices

1.  **Use for New Entities Only**: These methods are for creating new records. To modify existing ones, use an update or upsert service.
2.  **User Input**: Use `insertOne()` when creating an entity from direct user input to ensure immediate and clear feedback.
3.  **Batch Integrity**: Use `bulkInsert()` when you need to ensure a batch of entities is created together, with all items validated.
4.  **Performance**: `bulkInsert` prioritizes data integrity over raw speed because it processes each item individually. For very large, pre-validated datasets, consider using a dedicated high-performance bulk operation if available.
