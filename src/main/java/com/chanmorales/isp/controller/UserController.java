package com.chanmorales.isp.controller;

import com.chanmorales.isp.dto.response.UserResponse;
import com.chanmorales.isp.model.User;
import com.chanmorales.isp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/myself")
  public ResponseEntity<UserResponse> myself(@AuthenticationPrincipal User user) {
    return ResponseEntity.ok(userService.getByUsername(user.getUsername()));
  }
}
