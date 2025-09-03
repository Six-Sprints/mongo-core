# Exceptions

This document provides an overview of the custom exceptions used in this library. These exceptions are designed to provide a consistent and structured way of handling errors.

## `BaseException`

This is a custom checked exception that serves as the base for all other checked exceptions in the library. It includes the following properties:

- **`httpStatus`**: The HTTP status code to be returned to the client.
- **`error`**: The error message.
- **`data`**: Any additional data to be sent with the error response.
- **`arguments`**: A list of arguments to be used for message formatting.

### Note: The `BaseException` and all it's children exceptions are checked and must be thrown from the methods where they are being used so that the code can compile witout issues.

## `BaseRuntimeException`

This is a custom unchecked exception that serves as the base for all other unchecked exceptions in the library. It has the same properties as `BaseException`.

## Specific Exceptions

The following exceptions extend `BaseException` and provide more specific error handling for common scenarios:

- **`EntityAlreadyExistsException`**: Thrown when an attempt is made to create an entity that already exists. (HTTP Status: `409 CONFLICT`)
- **`EntityInvalidException`**: Thrown when an entity fails validation. (HTTP Status: `406 NOT_ACCEPTABLE`)
- **`EntityNotFoundException`**: Thrown when an entity cannot be found in the database. (HTTP Status: `404 NOT_FOUND`)
- **`NotAuthenticatedException`**: Thrown when a user is not authenticated to access a resource. (HTTP Status: `403 FORBIDDEN`)
- **`NotAuthorizedException`**: Thrown when a user is not authorized to perform a specific action. (HTTP Status: `401 UNAUTHORIZED`)

### All these specific exceptions have the default HHTP status already set and they provide a `childBuilder()` to set a custom error if needed.

## How to Use

You can throw these exceptions from your service layer, and the `RestExceptionHandler` will automatically handle them and generate a consistent JSON error response.

**Example:**

```java
@Service
@RequiredArgsConstructor
public class ProductInventoryService {

  private final ProductCrudService productCrudService;

  public Product addInventory(String slug, Integer quantity) throws EntityNotFoundException {
    Optional<Product> product = productCrudService.findBySlugOptional(slug);
    if (product.isEmpty()) {
      throw EntityNotFoundException.childBuilder().error("Product not found with slug: " + slug).build();
    }
    // ... rest of the code
  }
}
```
