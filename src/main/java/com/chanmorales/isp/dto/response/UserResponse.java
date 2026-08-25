package com.chanmorales.isp.dto.response;

import com.chanmorales.isp.model.User;

public record UserResponse(String username, String email) {
  public static UserResponse from(User user) {
    return new UserResponse(user.getUsername(), user.getEmail());
  }
}
