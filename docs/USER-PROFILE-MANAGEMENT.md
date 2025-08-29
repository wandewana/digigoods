# User Profile Management Feature

## Overview

The User Profile Management feature extends the Digigoods API to allow authenticated users to view and update their profile information. This feature demonstrates Augment Code's test generation capabilities by implementing a complete CRUD functionality with comprehensive unit and integration tests.

## Feature Implementation

### 1. Database Schema Changes

**Migration File**: `src/main/resources/db/changelog/007-add-user-profile-fields.yaml`

Added new profile fields to the existing `users` table:
- `full_name` (VARCHAR(255), nullable)
- `email` (VARCHAR(255), nullable) 
- `phone` (VARCHAR(50), nullable)

### 2. Entity Updates

**File**: `src/main/java/com/example/digigoods/model/User.java`

Extended the User entity with new profile fields:
```java
@Column(name = "full_name")
private String fullName;

@Column(name = "email")
private String email;

@Column(name = "phone")
private String phone;
```

### 3. Data Transfer Objects (DTOs)

**UserProfileDto** (`src/main/java/com/example/digigoods/dto/UserProfileDto.java`)
- Response DTO containing user profile information
- Excludes sensitive data like password
- Fields: id, username, fullName, email, phone

**UpdateUserProfileRequest** (`src/main/java/com/example/digigoods/dto/UpdateUserProfileRequest.java`)
- Request DTO for profile updates
- Includes validation annotations:
  - `@Email` for email format validation
  - `@Size` constraints for field lengths
- Fields: fullName, email, phone

### 4. Service Layer

**File**: `src/main/java/com/example/digigoods/service/UserService.java`

Implements core business logic:
- **getUserProfile()**: Retrieves user profile with authorization validation
- **updateUserProfile()**: Updates profile fields with authorization validation
- **validateUserAuthorization()**: Ensures users can only access their own profiles
- **convertToDto()**: Converts User entity to UserProfileDto

**Key Features**:
- Authorization validation (users can only access their own profiles)
- Transactional operations
- Proper error handling with custom exceptions

### 5. Controller Layer

**File**: `src/main/java/com/example/digigoods/controller/UserController.java`

REST API endpoints:
- `GET /users/{userId}/profile` - Get user profile
- `PUT /users/{userId}/profile` - Update user profile

**Security Features**:
- JWT token extraction and validation
- User authorization checks
- Request validation using `@Valid`

### 6. Security Configuration

**File**: `src/main/java/com/example/digigoods/config/SecurityConfig.java`

Updated security configuration to include user profile endpoints:
- `/users/*/profile` endpoints require authentication
- Follows existing JWT-based security patterns

## API Endpoints

### Get User Profile
```http
GET /users/{userId}/profile
Authorization: Bearer <JWT_TOKEN>
```

**Response**:
```json
{
  "id": 1,
  "username": "testuser",
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+1234567890"
}
```

### Update User Profile
```http
PUT /users/{userId}/profile
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "fullName": "John Smith",
  "email": "john.smith@example.com",
  "phone": "+0987654321"
}
```

**Response**:
```json
{
  "id": 1,
  "username": "testuser",
  "fullName": "John Smith",
  "email": "john.smith@example.com",
  "phone": "+0987654321"
}
```

## Test Implementation

### Unit Tests

**File**: `src/test/java/com/example/digigoods/service/UserServiceTest.java`

Comprehensive unit tests for UserService using JUnit 5 and Mockito:

**Test Structure**:
- Uses `@ExtendWith(MockitoExtension.class)` for Mockito integration
- Organized with `@Nested` classes for logical grouping
- Follows AAA (Arrange-Act-Assert) pattern
- Uses descriptive test method names with Given-When-Then format

**Test Coverage**:

**Get User Profile Tests**:
- ✅ Valid user ID with authorized access returns profile DTO
- ✅ Unauthorized access throws UnauthorizedAccessException
- ✅ Non-existent user ID throws RuntimeException

**Update User Profile Tests**:
- ✅ Valid update request with authorized access returns updated profile
- ✅ Unauthorized access throws UnauthorizedAccessException  
- ✅ Non-existent user ID throws RuntimeException
- ✅ Null values in request properly update profile with null values

### Integration Tests

**File**: `src/test/java/com/example/digigoods/controller/UserControllerIntegrationTest.java`

Full integration tests using Spring Boot Test framework:

**Test Setup**:
- `@SpringBootTest` with random port
- `@ActiveProfiles("test")` for test configuration
- `@Transactional` for test isolation
- MockMvc for HTTP request simulation
- H2 in-memory database for testing

**Test Coverage**:

**Get User Profile Tests**:
- ✅ Valid token and own user ID returns profile data
- ✅ Valid token but other user ID returns unauthorized (403)
- ✅ No token returns unauthorized (401)
- ✅ Invalid token returns unauthorized (401)
- ✅ Non-existent user ID returns internal server error (500)

**Update User Profile Tests**:
- ✅ Valid update request and own user ID returns updated profile
- ✅ Valid token but other user ID returns unauthorized (403)
- ✅ No token returns unauthorized (401)
- ✅ Invalid email format returns bad request (400)
- ✅ Null values in request successfully updates with null values

## Code Quality Standards

### Checkstyle Compliance
- All code follows Google Java Style Guide
- Line length limited to 100 characters
- Proper variable declaration usage distance
- No Checkstyle violations

### Testing Best Practices
- Descriptive test method names using Given-When-Then format
- `@DisplayName` annotations for clear test descriptions
- Proper use of `@BeforeEach` for test setup
- Comprehensive edge case coverage
- Proper exception testing

### Security Considerations
- JWT token validation for all endpoints
- User authorization checks prevent unauthorized access
- Input validation using Bean Validation annotations
- Sensitive data (passwords) excluded from responses

## Integration with Existing Codebase

### Follows Established Patterns
- **Architecture**: Controller → Service → Repository pattern
- **Security**: JWT-based authentication and authorization
- **Error Handling**: Custom exceptions with global exception handler
- **Database**: Liquibase migrations for schema changes
- **Testing**: JUnit 5 with Mockito for unit tests, Spring Boot Test for integration

### Maintains Consistency
- Uses existing JWT service for token handling
- Follows same authorization patterns as CheckoutService
- Consistent DTO and validation patterns
- Same transaction management approach

## Benefits for Augment Code Demonstration

This feature showcases Augment Code's capabilities in:

1. **Comprehensive Test Generation**: Generated both unit and integration tests with extensive coverage
2. **Multiple Test Scenarios**: Success paths, error conditions, edge cases, and validation scenarios
3. **Proper Test Structure**: AAA pattern, nested test classes, descriptive naming
4. **Integration Testing**: Full HTTP request/response testing with authentication
5. **Code Quality**: Checkstyle-compliant code following project standards
6. **Business Logic Testing**: Authorization rules, validation logic, and data transformation

The implementation demonstrates how Augment Code can generate production-ready code with comprehensive test coverage while maintaining consistency with existing project patterns and standards.
