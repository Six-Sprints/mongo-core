# Notification Operations

## Overview

The `NotificationService` interface is located in the `com.sixsprints.notification.service` package and provides methods to send notifications asynchronously. The notification service automatically handles:

- **Asynchronous Processing**: All notifications are sent asynchronously to prevent blocking the main application thread.
- **Multiple Channel Support**: Supports various notification channels (email, SMS, push notifications, etc.).
- **Template Processing**: Handles template-based notifications with dynamic content substitution.
- **Attachment Support**: Allows sending files and attachments with notifications.
- **Future-based Results**: Returns `Future<String>` for tracking notification status and results.

## Dependency Injection

To use the `NotificationService` in your application, inject it using Spring's dependency injection:

**Constructor Injection** (Recommended):

```java
import com.sixsprints.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final NotificationService notificationService;

    // Use notificationService in your methods
}
```

**Alternative Injection Methods**:

- **Field Injection**:

```java
import com.sixsprints.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserService {

    @Autowired
    private NotificationService notificationService;

    // Use notificationService in your methods
}
```

---

## Methods

### `sendMessage(MessageDto messageDto)`

Sends a notification message asynchronously through the configured notification channels.

- **Parameters**: `messageDto` (MessageDto) - The message details containing recipient, content, and configuration (must not be `null`).
- **Returns**: `Future<String>` - A Future object that will contain the notification result (typically a message ID).
- **Throws**:
  - `IllegalArgumentException`: If the messageDto is null or contains invalid data.
  - `NotificationException`: If the notification fails to send (e.g., invalid recipient, service unavailable).

**MessageDto Properties**:

- `to` (String) - Recipient address (email, phone number, etc.)
- `subject` (String) - Message subject/title
- `content` (String) - Message body content (supports plain text and HTML for email notifications)
- `attachment` (AttachmentDto) - Optional file attachment
- `templateId` (String) - Optional template identifier for dynamic content (supports Handlebars templates located in classpath)
- `templateValues` (Object) - Optional template variables for content substitution (can be Map or DTO for Handlebars context)

**AttachmentDto Properties**:

- `attachmentUrl` (String) - URL pointing to the attachment file
- `name` (String) - Display name of the attachment
- `description` (String) - Optional description of the attachment

**Example**:

```java
MessageDto message = MessageDto.builder()
    .to("user@example.com")
    .subject("Welcome to Our Service")
    .content("Thank you for joining us!<br/>Best Regards.")
    .build();

Future<String> result = notificationService.sendMessage(message);

// Optionally wait for completion
try {
    String messageId = result.get(5, TimeUnit.SECONDS);
    System.out.println("Notification sent with ID: " + messageId);
} catch (TimeoutException e) {
    System.out.println("Notification is still processing...");
}
```

**Example with HTML Content**:

```java
String htmlContent = """
    <html>
        <body>
            <h2>Welcome to Our Service!</h2>
            <p>Thank you for joining us. Here's what you can do:</p>
            <ul>
                <li>Explore our features</li>
                <li>Update your profile</li>
                <li>Contact support if needed</li>
            </ul>
            <p>Best regards,<br>The Team</p>
        </body>
    </html>
    """;

MessageDto message = MessageDto.builder()
    .to("user@example.com")
    .subject("Welcome to Our Service")
    .content(htmlContent)
    .build();

notificationService.sendMessage(message);
```

**Example with Template**:

```java
// Using Map for template values
Map<String, Object> templateValues = new HashMap<>();
templateValues.put("userName", "John Doe");
templateValues.put("activationLink", "https://example.com/activate/123");

MessageDto message = MessageDto.builder()
    .to("john@example.com")
    .subject("Account Activation")
    .templateId("welcome-email")  // Template file: src/main/resources/templates/welcome-email.hbs
    .templateValues(templateValues)
    .build();

notificationService.sendMessage(message);
```

**Example with DTO for Template Values**:

```java
// Using DTO for template values
UserActivationDto activationData = UserActivationDto.builder()
    .userName("John Doe")
    .activationLink("https://example.com/activate/123")
    .expiryDate("2024-12-31")
    .build();

MessageDto message = MessageDto.builder()
    .to("john@example.com")
    .subject("Account Activation")
    .templateId("welcome-email")  // Template file: src/main/resources/templates/welcome-email.hbs
    .templateValues(activationData)
    .build();

notificationService.sendMessage(message);
```

**Example with Attachment**:

```java
AttachmentDto attachment = AttachmentDto.builder()
    .attachmentUrl("https://example.com/files/invoice.pdf")
    .name("Invoice_2024_001.pdf")
    .description("Monthly invoice for services")
    .build();

MessageDto message = MessageDto.builder()
    .to("customer@example.com")
    .subject("Your Invoice")
    .content("Please find your invoice attached.")
    .attachment(attachment)
    .build();

notificationService.sendMessage(message);
```

---

## Template System

The notification service supports Handlebars templates for dynamic content generation:

### Template Location

Templates must be placed in the classpath under the `templates` directory:

```
src/main/resources/templates/
├── welcome-email.hbs
├── password-reset.hbs
├── order-confirmation.hbs
└── invoice-notification.hbs
```

### Template File Naming

- Use the `templateId` as the filename (e.g., templateId: `welcome-email` → filenameWithExtenstion: `welcome-email.hbs` → fqn: `<base-package>.src.main.resources.template.welcome-email`)
- File extension must be `.hbs` for Handlebars templates
- Case-sensitive template IDs

### Handlebars Features

Templates support standard Handlebars syntax:

```handlebars
<!-- welcome-email.hbs -->
<html>
  <body>
    <h2>Welcome {{userName}}!</h2>
    <p>Thank you for joining our service.</p>

    {{#if activationLink}}
      <p>Please activate your account: <a href='{{activationLink}}'>Activate Account</a></p>
    {{/if}}

    {{#if expiryDate}}
      <p><small>This link expires on {{expiryDate}}</small></p>
    {{/if}}

    <p>Best regards,<br />The Team</p>
  </body>
</html>
```

### Template Values

The `templateValues` can be:

1. **Map<String, Object>**: Simple key-value pairs

```java
Map<String, Object> values = new HashMap<>();
values.put("userName", "John Doe");
values.put("isPremium", true);
```

2. **DTO Objects**: Rich objects with nested properties

```java
UserDto user = UserDto.builder()
    .name("John Doe")
    .profile(ProfileDto.builder()
        .avatar("avatar.jpg")
        .preferences(Map.of("theme", "dark"))
        .build())
    .build();
```

### Template Context Access

In Handlebars templates, access values using dot notation:

- Map values: `{{userName}}`, `{{isPremium}}`
- DTO properties: `{{name}}`, `{{profile.avatar}}`, `{{profile.preferences.theme}}`
