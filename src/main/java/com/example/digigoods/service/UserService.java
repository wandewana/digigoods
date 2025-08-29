package com.example.digigoods.service;

import com.example.digigoods.dto.UpdateUserProfileRequest;
import com.example.digigoods.dto.UserProfileDto;
import com.example.digigoods.exception.UnauthorizedAccessException;
import com.example.digigoods.model.User;
import com.example.digigoods.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for user profile operations.
 */
@Service
@Transactional
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Get user profile by user ID.
   *
   * @param userId the user ID
   * @param authenticatedUserId the ID of the authenticated user
   * @return user profile DTO
   * @throws UnauthorizedAccessException if user tries to access another user's profile
   * @throws RuntimeException if user not found
   */
  public UserProfileDto getUserProfile(Long userId, Long authenticatedUserId) {
    validateUserAuthorization(userId, authenticatedUserId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    return convertToDto(user);
  }

  /**
   * Update user profile.
   *
   * @param userId the user ID
   * @param request the update request
   * @param authenticatedUserId the ID of the authenticated user
   * @return updated user profile DTO
   * @throws UnauthorizedAccessException if user tries to update another user's profile
   * @throws RuntimeException if user not found
   */
  public UserProfileDto updateUserProfile(Long userId, UpdateUserProfileRequest request,
                                          Long authenticatedUserId) {
    validateUserAuthorization(userId, authenticatedUserId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    // Update profile fields
    user.setFullName(request.getFullName());
    user.setEmail(request.getEmail());
    user.setPhone(request.getPhone());

    User savedUser = userRepository.save(user);
    return convertToDto(savedUser);
  }

  private void validateUserAuthorization(Long requestedUserId, Long authenticatedUserId) {
    if (!requestedUserId.equals(authenticatedUserId)) {
      throw new UnauthorizedAccessException("User cannot access another user's profile");
    }
  }

  private UserProfileDto convertToDto(User user) {
    return new UserProfileDto(
        user.getId(),
        user.getUsername(),
        user.getFullName(),
        user.getEmail(),
        user.getPhone()
    );
  }
}
