package com.example.digigoods.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating user profile request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileRequest {

  @Size(max = 255, message = "Full name must not exceed 255 characters")
  private String fullName;

  @Email(message = "Email must be a valid email address")
  @Size(max = 255, message = "Email must not exceed 255 characters")
  private String email;

  @Size(max = 50, message = "Phone must not exceed 50 characters")
  private String phone;
}
