# Entity Hooks

This document explains how to use the various hook interfaces provided by the mongo-core library to modify data before and after entity operations.

## Overview

The mongo-core library provides a comprehensive hook system that allows you to intercept and modify entity operations at different stages of their lifecycle. These hooks are automatically executed by the `GenericAbstractService` and provide a clean way to add custom business logic without modifying the core service implementation.

## Available Hook Interfaces

### 1. PreInsertHook

**Purpose**: Executed before an entity is inserted into the database.

**Full Qualified Name**: `com.sixsprints.core.generic.hooks.PreInsertHook`

**Interface**:

```java
public interface PreInsertHook<T extends AbstractMongoEntity> {
    void preInsert(T entity);
}
```

**Example Implementation**:

```java
package com.example.user.hooks;

import org.springframework.stereotype.Component;
import com.sixsprints.core.generic.hooks.PreInsertHook;
import com.example.user.User;

@Component
public class UserPreInsertHook implements PreInsertHook<User> {

    @Override
    public void preInsert(User user) {
      // write any desired implementation
    }
}
```

### 2. PreUpdateHook

**Purpose**: Executed before an entity is updated in the database.

**Full Qualified Name**: `com.sixsprints.core.generic.hooks.PreUpdateHook`

**Interface**:

```java
public interface PreUpdateHook<T extends AbstractMongoEntity> {
    void preUpdate(T now, T toBe);
}
```

**Example Implementation**:

```java
package com.example.user.hooks;

import org.springframework.stereotype.Component;
import com.sixsprints.core.generic.hooks.PreUpdateHook;
import com.example.user.User;

@Component
public class UserPreUpdateHook implements PreUpdateHook<User> {

    @Override
    public void preUpdate(User now, User toBe) {
        // write any desired implementation
    }
}
```

### 3. PostInsertHook

**Purpose**: Executed after an entity has been successfully inserted into the database.

**Full Qualified Name**: `com.sixsprints.core.generic.hooks.PostInsertHook`

**Interface**:

```java
public interface PostInsertHook<T extends AbstractMongoEntity> {
    void postInsert(T entity);
}
```

**Example Implementation**:

```java
package com.example.user.hooks;

import org.springframework.stereotype.Component;
import com.sixsprints.core.generic.hooks.PostInsertHook;
import com.example.user.User;

@Component
public class UserPostInsertHook implements PostInsertHook<User> {

    @Override
    public void postInsert(User user) {
        // write any desired implementation
    }
}
```

### 4. PostUpdateHook

**Purpose**: Executed after an entity has been successfully updated in the database.

**Full Qualified Name**: `com.sixsprints.core.generic.hooks.PostUpdateHook`

**Interface**:

```java
public interface PostUpdateHook<T extends AbstractMongoEntity> {
    void postUpdate(T entity);
}
```

**Example Implementation**:

```java
package com.example.user.hooks;

import org.springframework.stereotype.Component;
import com.sixsprints.core.generic.hooks.PostUpdateHook;
import com.example.user.User;

@Component
public class UserPostUpdateHook implements PostUpdateHook<User> {

    @Override
    public void postUpdate(User user) {
        // write any desired implementation
    }
}
```

### 5. EnhanceEntityHook

**Purpose**: Enhances an entity with additional data or modifications before any operation.

**Use Cases**:

- Add computed fields
- Apply consistent transformations
- Set derived values
- Enrich entity data

**Full Qualified Name**: `com.sixsprints.core.generic.hooks.EnhanceEntityHook`

**Interface**:

```java
public interface EnhanceEntityHook<T extends AbstractMongoEntity> {
    void enhanceEntity(T entity);
}
```

**Example Implementation**:

```java
package com.example.user.hooks;

import org.springframework.stereotype.Component;
import com.sixsprints.core.generic.hooks.EnhanceEntityHook;
import com.example.user.User;

@Component
public class UserEnhanceEntityHook implements EnhanceEntityHook<User> {

    @Override
    public void enhanceEntity(User user) {
        // write any desired implementation
    }
}
```

## Hook Execution Order

The hooks are executed in the following order during entity operations:

### For Insert Operations:

1. `EnhanceEntityHook` - Enhance the entity
2. `PreInsertHook` - Pre-insert validation and modifications
3. **Database Insert** - Actual database operation
4. `PostInsertHook` - Post-insert actions

### For Update Operations:

1. `EnhanceEntityHook` - Enhance the entity
2. `PreUpdateHook` - Pre-update validation and modifications
3. **Database Update** - Actual database operation
4. `PostUpdateHook` - Post-update actions

## Configuration

### Package Structure

It is recommended to create your hook implementations in the following package structure:

```
<base-package>.<entity-name>.hooks
```

For example, if your base package is `com.example` and you have a `User` entity, create your hooks in:

```
com.example.user.hooks
```

This keeps your hooks organized and makes them easy to find and maintain.

**Note**: It's a best practice to create multiple hooks (even of the same type) for different use cases to maintain single responsibility. For example, you might have separate `UserSetFullNamePreInsertHook`, `UserNotificationPreInsertHook`, and `UserAuditPreInsertHook` classes, each handling a specific aspect of the pre-insert logic.

### Automatic Discovery

Hooks are automatically discovered and registered by Spring's component scanning. Simply annotate your hook implementations with `@Component` or `@Service`:

```java
@Component
public class MyCustomHook implements PreInsertHook<MyEntity> {
    // Implementation
}

```
