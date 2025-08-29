package com.example.digigoods.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.digigoods.dto.UpdateUserProfileRequest;
import com.example.digigoods.dto.UserProfileDto;
import com.example.digigoods.exception.UnauthorizedAccessException;
import com.example.digigoods.model.User;
import com.example.digigoods.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;
  private Long authenticatedUserId;
  private Long otherUserId;

  @BeforeEach
  void setUp() {
    authenticatedUserId = 1L;
    otherUserId = 2L;

    testUser = new User();
    testUser.setId(authenticatedUserId);
    testUser.setUsername("testuser");
    testUser.setPassword("hashedpassword");
    testUser.setFullName("Test User");
    testUser.setEmail("test@example.com");
    testUser.setPhone("+1234567890");
  }

  @Nested
  @DisplayName("Get User Profile Tests")
  class GetUserProfileTests {

    @Test
    @DisplayName("Given valid user ID and authorized access, when getting user profile, "
        + "then return profile DTO")
    void givenValidUserIdAndAuthorizedAccess_whenGettingUserProfile_thenReturnProfileDto() {
      // Arrange
      when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));

      // Act
      UserProfileDto result = userService.getUserProfile(authenticatedUserId, authenticatedUserId);

      // Assert
      assertNotNull(result);
      assertEquals(authenticatedUserId, result.getId());
      assertEquals("testuser", result.getUsername());
      assertEquals("Test User", result.getFullName());
      assertEquals("test@example.com", result.getEmail());
      assertEquals("+1234567890", result.getPhone());
      verify(userRepository).findById(authenticatedUserId);
    }

    @Test
    @DisplayName("Given unauthorized access, when getting user profile, "
        + "then throw UnauthorizedAccessException")
    void givenUnauthorizedAccess_whenGettingUserProfile_thenThrowUnauthorizedAccessException() {
      // Arrange & Act & Assert
      assertThrows(UnauthorizedAccessException.class,
          () -> userService.getUserProfile(otherUserId, authenticatedUserId));
    }

    @Test
    @DisplayName("Given non-existent user ID, when getting user profile, "
        + "then throw RuntimeException")
    void givenNonExistentUserId_whenGettingUserProfile_thenThrowRuntimeException() {
      // Arrange
      when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.empty());

      // Act & Assert
      assertThrows(RuntimeException.class,
          () -> userService.getUserProfile(authenticatedUserId, authenticatedUserId));
    }
  }

  @Nested
  @DisplayName("Update User Profile Tests")
  class UpdateUserProfileTests {

    @Test
    @DisplayName("Given valid update request and authorized access, when updating profile, "
        + "then return updated profile")
    void givenValidUpdateRequestAndAuthorizedAccess_whenUpdatingProfile_thenReturnUpdatedProfile() {
      // Arrange
      final UpdateUserProfileRequest request = new UpdateUserProfileRequest(
          "Updated Name", "updated@example.com", "+9876543210");

      User updatedUser = new User();
      updatedUser.setId(authenticatedUserId);
      updatedUser.setUsername("testuser");
      updatedUser.setPassword("hashedpassword");
      updatedUser.setFullName("Updated Name");
      updatedUser.setEmail("updated@example.com");
      updatedUser.setPhone("+9876543210");

      when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
      when(userRepository.save(any(User.class))).thenReturn(updatedUser);

      // Act
      UserProfileDto result = userService.updateUserProfile(authenticatedUserId, request,
          authenticatedUserId);

      // Assert
      assertNotNull(result);
      assertEquals(authenticatedUserId, result.getId());
      assertEquals("testuser", result.getUsername());
      assertEquals("Updated Name", result.getFullName());
      assertEquals("updated@example.com", result.getEmail());
      assertEquals("+9876543210", result.getPhone());
      verify(userRepository).findById(authenticatedUserId);
      verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Given unauthorized access, when updating user profile, "
        + "then throw UnauthorizedAccessException")
    void givenUnauthorizedAccess_whenUpdatingUserProfile_thenThrowUnauthorizedAccessException() {
      // Arrange
      UpdateUserProfileRequest request = new UpdateUserProfileRequest(
          "Updated Name", "updated@example.com", "+9876543210");

      // Act & Assert
      assertThrows(UnauthorizedAccessException.class,
          () -> userService.updateUserProfile(otherUserId, request, authenticatedUserId));
    }

    @Test
    @DisplayName("Given non-existent user ID, when updating profile, then throw RuntimeException")
    void givenNonExistentUserId_whenUpdatingProfile_thenThrowRuntimeException() {
      // Arrange
      UpdateUserProfileRequest request = new UpdateUserProfileRequest(
          "Updated Name", "updated@example.com", "+9876543210");
      when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.empty());

      // Act & Assert
      assertThrows(RuntimeException.class,
          () -> userService.updateUserProfile(authenticatedUserId, request, authenticatedUserId));
    }

    @Test
    @DisplayName("Given null values in update request, when updating profile, "
        + "then update with null values")
    void givenNullValuesInUpdateRequest_whenUpdatingProfile_thenUpdateWithNullValues() {
      // Arrange
      final UpdateUserProfileRequest request = new UpdateUserProfileRequest(null, null, null);

      User updatedUser = new User();
      updatedUser.setId(authenticatedUserId);
      updatedUser.setUsername("testuser");
      updatedUser.setPassword("hashedpassword");
      updatedUser.setFullName(null);
      updatedUser.setEmail(null);
      updatedUser.setPhone(null);

      when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
      when(userRepository.save(any(User.class))).thenReturn(updatedUser);

      // Act
      UserProfileDto result = userService.updateUserProfile(authenticatedUserId, request,
          authenticatedUserId);

      // Assert
      assertNotNull(result);
      assertEquals(authenticatedUserId, result.getId());
      assertEquals("testuser", result.getUsername());
      assertEquals(null, result.getFullName());
      assertEquals(null, result.getEmail());
      assertEquals(null, result.getPhone());
    }
  }
}
