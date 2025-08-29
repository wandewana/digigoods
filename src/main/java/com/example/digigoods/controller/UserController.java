package com.example.digigoods.controller;

import com.example.digigoods.dto.UpdateUserProfileRequest;
import com.example.digigoods.dto.UserProfileDto;
import com.example.digigoods.exception.MissingJwtTokenException;
import com.example.digigoods.service.JwtService;
import com.example.digigoods.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for user profile endpoints.
 */
@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;
  private final JwtService jwtService;

  public UserController(UserService userService, JwtService jwtService) {
    this.userService = userService;
    this.jwtService = jwtService;
  }

  /**
   * Get user profile endpoint.
   *
   * @param userId the user ID
   * @param request the HTTP servlet request
   * @return user profile DTO
   */
  @GetMapping("/{userId}/profile")
  public ResponseEntity<UserProfileDto> getUserProfile(
      @PathVariable Long userId,
      HttpServletRequest request) {
    // Extract user ID from JWT token
    String token = extractTokenFromRequest(request);
    if (token == null) {
      throw new MissingJwtTokenException();
    }
    Long authenticatedUserId = jwtService.extractUserId(token);

    UserProfileDto profile = userService.getUserProfile(userId, authenticatedUserId);
    return ResponseEntity.ok(profile);
  }

  /**
   * Update user profile endpoint.
   *
   * @param userId the user ID
   * @param updateRequest the update request
   * @param request the HTTP servlet request
   * @return updated user profile DTO
   */
  @PutMapping("/{userId}/profile")
  public ResponseEntity<UserProfileDto> updateUserProfile(
      @PathVariable Long userId,
      @Valid @RequestBody UpdateUserProfileRequest updateRequest,
      HttpServletRequest request) {
    // Extract user ID from JWT token
    String token = extractTokenFromRequest(request);
    if (token == null) {
      throw new MissingJwtTokenException();
    }
    Long authenticatedUserId = jwtService.extractUserId(token);

    UserProfileDto updatedProfile = userService.updateUserProfile(userId, updateRequest,
        authenticatedUserId);
    return ResponseEntity.ok(updatedProfile);
  }

  private String extractTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
