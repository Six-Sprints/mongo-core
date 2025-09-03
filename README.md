# Mongo Core

A comprehensive Spring Boot framework for building MongoDB-based applications with built-in CRUD operations, authentication, and exception handling.

## 🎯 Philosophy

Mongo Core was built on top of Spring Boot with a clear philosophy: **provide opinionated, lightweight solutions without the complexity of heavy frameworks**.

### Design Principles

- **Lightweight Authentication**: Built-in JWT-based authentication without the overhead of Spring Security
- **Opinionated Services**: Pre-built service layers that follow best practices out of the box
- **Simple Controllers**: Abstract base controllers that handle common patterns automatically
- **Minimal Configuration**: Sensible defaults that work for most use cases
- **Type Safety**: Compile-time validation through Lombok's `@FieldNameConstants`
- **Developer Experience**: Focus on productivity with less boilerplate code

### Why Not Spring Security?

While Spring Security is powerful, it can be overkill for many applications. Mongo Core provides:

- **Simpler Setup**: No complex security configuration
- **Faster Development**: Opinionated defaults that work immediately
- **Less Dependencies**: Fewer transitive dependencies to manage
- **Easier Testing**: Simpler authentication mocking in tests
- **Better Performance**: Lighter weight authentication layer

## 🚀 Features

### Core Components

- **Generic CRUD Operations**: Complete Create, Read, Update, Delete operations with validation
- **MongoDB Integration**: Optimized Spring Data MongoDB configuration with custom converters
- **Authentication & Authorization**: JWT-based authentication with role-based access control
- **Exception Handling**: Comprehensive exception handling with automatic HTTP response mapping
- **Audit Support**: Built-in audit fields and tracking
- **Type Safety**: Lombok `@FieldNameConstants` for compile-time field validation

### Key Capabilities

- **Bulk Operations**: Efficient bulk insert, update, and delete operations
- **Pagination & Sorting**: Built-in pagination and sorting support
- **Criteria Queries**: Advanced MongoDB criteria-based querying
- **Upsert Operations**: Insert-or-update functionality
- **Soft Delete**: Logical deletion with audit trails
- **CORS Support**: Built-in CORS configuration
- **Validation**: Jakarta Bean Validation integration

## 📋 Requirements

- Java 17+
- Spring Boot 3.5.5+
- MongoDB 4.4+
- Maven 3.6+

## 🛠️ Installation

### Maven Dependency

```xml
<dependency>
    <groupId>com.sixsprints</groupId>
    <artifactId>mongo-core</artifactId>
    <version>3.5.500</version>
</dependency>
```

### Gradle Dependency

```gradle
implementation 'com.sixsprints:mongo-core:3.5.500'
```

## ⚙️ Configuration

### Basic Setup

1. **Extend the base entity**:

```java
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User extends AbstractMongoEntity {
    private String name;
    private String email;
    private String department;
    // ... other fields
}
```

2. **Create a service**:

```java
@Service
public class UserService extends AbstractCrudService<User> {
    public UserService(GenericCrudRepository<User> repository) {
        super(repository);
    }
}
```

3. **Create a controller**:

```java
@RestController
@RequestMapping("/api/users")
public class UserController extends AbstractCrudController<User> {
    public UserController(GenericCrudService<User> service) {
        super(service);
    }
}
```

### MongoDB Configuration

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/your-database
      database: your-database
```

### Authentication Configuration

```yaml
# JWT Configuration
token:
  expiry-in-days: 30
  shared-secret: your-secret-key
  issuer: your-app-name
```

## 📚 Documentation

### Generic CRUD Operations

Comprehensive documentation for all CRUD operations:

- **[Create Operations](docs/generic-crud-service/01-create-operations.md)** - Insert and bulk insert operations
- **[Read Operations](docs/generic-crud-service/02-read-operations-basic.md)** - Query, filter, and pagination
- **[Update Operations](docs/generic-crud-service/03-update-operations.md)** - Update, patch, and bulk update operations
- **[Delete Operations](docs/generic-crud-service/04-delete-operations.md)** - Hard delete and bulk delete operations
- **[Exception Handling](docs/generic-crud-service/10-exceptions.md)** - Error handling and custom exceptions

### Quick Start Examples

#### Creating Entities

```java
User newUser = User.builder()
    .name("John Doe")
    .email("john@example.com")
    .department("Engineering")
    .build();

User createdUser = userService.insertOne(newUser);
```

#### Reading Entities

```java
// Find by ID
Optional<User> user = userService.findOneById("507f1f77bcf86cd799439011");

// Find with criteria
Criteria criteria = Criteria.where(User.Fields.department).is("Engineering");
Page<User> engineers = userService.filterByCriteria(criteria, PageRequest.of(0, 10));
```

#### Updating Entities

```java
// Patch update
User updateData = User.builder()
    .department("IT")
    .build();

User updatedUser = userService.patchUpdateOneById(userId, updateData, User.Fields.department);
```

#### Deleting Entities

```java
// Single delete
long deletedCount = userService.deleteOneById(userId);

// Bulk delete
Criteria criteria = Criteria.where(User.Fields.active).is(false);
long deletedCount = userService.bulkDeleteByCriteria(criteria);
```

## 🔐 Authentication & Authorization

### Basic Authentication

```java
@RestController
public class UserController {

    @BasicAuth(module = BasicModuleEnum.USER, permission = BasicPermissionEnum.READ)
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile() {
        User currentUser = ApplicationContext.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }
}
```

### Custom Authentication

```java
@Component
public class AuthInterceptor extends AbstractAuthenticationInterceptor<User> {

    public AuthInterceptor(UserService userService) {
        super(userService);
    }

    @Override
    protected String authTokenKey() {
        return "X-AUTH-TOKEN";
    }

    @Override
    protected void checkUserPermissions(User user, ModuleDefinition module,
                                      PermissionDefinition permission, boolean required) {
        // Custom permission logic
    }
}
```

## 🎯 Key Features

### Type-Safe Field References

```java
// Using Lombok @FieldNameConstants
Criteria criteria = Criteria.where(User.Fields.department).is("Engineering");
Sort sort = Sort.by(User.Fields.name).ascending();
List<String> fields = Arrays.asList(User.Fields.name, User.Fields.email);
```

### Automatic Audit Fields

All entities automatically include:

- `id` - Unique identifier
- `slug` - Human-readable identifier
- `sequence` - Auto-incrementing sequence
- `dateCreated` - Creation timestamp
- `dateModified` - Last modification timestamp
- `createdBy` - Creator user ID
- `lastModifiedBy` - Last modifier user ID

### Exception Handling

```java
// Exceptions are automatically converted to HTTP responses
try {
    User user = userService.findOneById(userId)
        .orElseThrow(() -> EntityNotFoundException.childBuilder()
            .error("User not found with ID: " + userId)
            .build());
} catch (EntityNotFoundException e) {
    // Automatically returns HTTP 404 with proper error message
}
```

## 🧪 Testing

The framework includes comprehensive test utilities:

```java
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest extends BaseControllerTest {

    @Test
    public void testCreateUser() {
        User user = User.builder()
            .name("Test User")
            .email("test@example.com")
            .build();

        User created = userService.insertOne(user);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getSlug()).isNotNull();
    }
}
```

## 🔧 Advanced Configuration

### Custom Converters

```java
@Configuration
public class MongoConfig extends ParentMongoConfig {

    @Override
    protected List<Converter<?, ?>> converters() {
        List<Converter<?, ?>> converters = super.converters();
        converters.add(new CustomConverter());
        return converters;
    }
}
```

### Custom Exception Handler

```java
@ControllerAdvice
public class CustomExceptionHandler extends RestExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException ex) {
        return RestUtil.errorResponse(ex.getData(), ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
```

## 📖 API Reference

### GenericCrudService Methods

#### Create Operations

- `insertOne(T entity)` - Insert single entity with validation
- `bulkInsert(List<T> entities)` - Bulk insert with validation

#### Read Operations

- `findAllList()` - Get all entities as list
- `findAll(Pageable pageable)` - Get paginated entities
- `findOneById(String id)` - Find by ID (returns Optional)
- `findOneBySlug(String slug)` - Find by slug (returns Optional)
- `findOneByCriteria(Criteria criteria)` - Find by criteria (returns Optional)
- `filterByCriteria(Criteria criteria)` - Filter with default pagination
- `filterByCriteria(Criteria criteria, Sort sort)` - Filter with sorting
- `filterByCriteria(Criteria criteria, Pageable pageable)` - Filter with pagination

#### Update Operations

- `updateOneById(String id, T entity)` - Full update by ID
- `updateOneBySlug(String slug, T entity)` - Full update by slug
- `updateOneByCriteria(Criteria criteria, T entity)` - Full update by criteria
- `patchUpdateOneById(String id, T entity, String propChanged)` - Partial update by ID
- `patchUpdateOneById(String id, T entity, List<String> propsChanged)` - Partial update by ID (multiple fields)
- `bulkPatchUpdateByCriteria(Criteria criteria, T entity, String propChanged)` - Bulk partial update
- `bulkPatchUpdateByCriteria(Criteria criteria, T entity, List<String> propsChanged)` - Bulk partial update (multiple fields)
- `upsertOne(T entity)` - Insert or update
- `bulkUpsert(List<T> entities)` - Bulk insert or update

#### Delete Operations

- `deleteOneById(String id)` - Delete by ID
- `deleteOneBySlug(String slug)` - Delete by slug
- `deleteOneByCriteria(Criteria criteria)` - Delete by criteria
- `bulkDeleteById(List<String> ids)` - Bulk delete by IDs
- `bulkDeleteBySlug(List<String> slugs)` - Bulk delete by slugs
- `bulkDeleteByCriteria(Criteria criteria)` - Bulk delete by criteria

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For questions and support:

- Check the [documentation](docs/)
- Open an issue on GitHub
- Contact the development team

## 🔄 Version History

- **3.5.500** - Current version with comprehensive CRUD operations
- **3.5.0** - Initial release with basic CRUD functionality

---

**Built with ❤️ by the SixSprints team**
