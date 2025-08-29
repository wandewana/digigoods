package com.example.digigoods.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user profile response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {

  private Long id;
  private String username;
  private String fullName;
  private String email;
  private String phone;
}
