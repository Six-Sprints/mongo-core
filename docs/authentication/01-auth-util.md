# AuthUtil - JWT Token Management

## Overview

The `AuthUtil` class provides secure JWT (JSON Web Token) creation, validation, and decoding functionality for authentication purposes. It handles token lifecycle management with built-in security features including:

- **Token Creation**: Generates signed JWT tokens with configurable expiration
- **Token Validation**: Verifies token signature, expiration, and integrity
- **Token Decoding**: Safely extracts subject information from valid tokens
- **Security Features**: Uses XOR encryption for subject obfuscation and HMAC-SHA256 for signing
- **Exception Handling**: Provides clear error messages for authentication failures

---

## Configuration

The utility uses environment variables for configuration with sensible defaults:

| Environment Variable | Default Value             | Description                                     |
| -------------------- | ------------------------- | ----------------------------------------------- |
| `JWT_TOKEN_SECRET`   | `xxxxxxxxxx`              | Secret key for XOR encryption of token subjects |
| `JWT_ISSUER`         | `https://www.website.com` | Token issuer identifier                         |
| `JWT_SHARED_SECRET`  | `xxxxxxxxxx`              | HMAC signing key (must be at least 256 bits)    |
| `JWT_TOKEN_EXPIRY`   | `30`                      | Default token expiration in days                |

---

## Methods

### `createToken(String subject)`

Creates a JWT token with the default expiration period.

- **Parameters**: `subject` (String) - The subject to encode in the token (**must be the database ID, not slug**)
- **Returns**: `String` - The signed JWT token
- **Throws**: No exceptions (errors are logged)

**Example**:

```java
String userId = "user123";
String token = AuthUtil.createToken(userId);
System.out.println("Generated token: " + token);
```

### `createToken(String subject, int expiryDays)`

Creates a JWT token with a custom expiration period.

- **Parameters**:
  - `subject` (String) - The subject to encode in the token (**must be the database ID, not slug**)
  - `expiryDays` (int) - Number of days until token expiration
- **Returns**: `String` - The signed JWT token
- **Throws**: No exceptions (errors are logged)

**Example**:

```java
String userId = "user123";
String shortLivedToken = AuthUtil.createToken(userId, 1); // Expires in 1 day
String longLivedToken = AuthUtil.createToken(userId, 90); // Expires in 90 days
```

### `decodeToken(String authHeader)`

Decodes and validates a JWT token, returning the original subject.

- **Parameters**: `authHeader` (String) - The JWT token string
- **Returns**: `String` - The decoded subject from the token
- **Throws**:
  - `NotAuthorizedException`: If token is invalid, expired, or cannot be verified (returns **HTTP 401 Unauthorized**)

**Example**:

```java
try {
    String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
    String userId = AuthUtil.decodeToken(token);
    System.out.println("Authenticated user: " + userId);
} catch (NotAuthorizedException e) {
    System.out.println("Authentication failed: " + e.getMessage());
}
```

---

## ⚠️ Important: Token Subject Requirements

**CRITICAL**: When generating tokens, always use the **database ID** of the entity as the subject, never use the slug or any other identifier. The token resolution mechanism expects the entity ID to properly resolve back the payload and perform database lookups.

**Correct Usage**:

```java
// ✅ Use database ID
String userId = user.getId(); // Database ID like "507f1f77bcf86cd799439011"
String token = AuthUtil.createToken(userId);
```

**Incorrect Usage**:

```java
// ❌ Don't use slug or other identifiers
String userSlug = user.getSlug(); // Slug like "john-doe"
String token = AuthUtil.createToken(userSlug); // This will cause resolution issues
```

---

## Security Features

### Token Structure

Each JWT token contains:

- **Header**: Uses HMAC-SHA256 algorithm (`HS256`)
- **Payload**: Contains subject (XOR encrypted), issuer, issue time, and expiration time
- **Signature**: HMAC-SHA256 signature using the shared secret

### XOR Encryption

The subject is encrypted using XOR with a rotating secret key to prevent:

- Direct reading of sensitive information
- Token tampering
- Information leakage in logs

### Token Validation

The `decodeToken` method performs comprehensive validation:

1. **Signature Verification**: Ensures token hasn't been tampered with
2. **Expiration Check**: Validates token hasn't expired
3. **Format Validation**: Confirms token structure is valid
4. **Subject Decryption**: Safely extracts the original subject

---

## Error Handling

The utility throws `NotAuthorizedException` with specific error constants:

| Error Constant           | Description                          | HTTP Status      |
| ------------------------ | ------------------------------------ | ---------------- |
| `UNABLE_TO_VERIFY_TOKEN` | Token signature verification failed  | 401 Unauthorized |
| `INVALID_TOKEN`          | Token format is invalid or corrupted | 401 Unauthorized |
| `TOKEN_EXPIRED`          | Token has passed its expiration time | 401 Unauthorized |

---
