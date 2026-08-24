package com.chanmorales.isp.service;

import com.chanmorales.isp.dto.CreateUserData;
import com.chanmorales.isp.dto.response.UserResponse;
import com.chanmorales.isp.exception.ResourceAlreadyExistsException;
import com.chanmorales.isp.exception.ResourceNotFoundException;
import com.chanmorales.isp.model.User;
import com.chanmorales.isp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public User createNewUser(CreateUserData request) {
    if (userRepository.existsByUsername(request.username())) {
      throw new ResourceAlreadyExistsException(
          "User with username '" + request.username() + "' already exists");
    }
    if (userRepository.existsByEmail(request.email())) {
      throw new ResourceAlreadyExistsException(
          "User with email '" + request.email() + "' already exists");
    }

    User user =
        User.builder()
            .username(request.username())
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .role(request.role())
            .build();

    return userRepository.save(user);
  }

  public UserResponse getByUsername(String username) {
    return userRepository
        .findByUsername(username)
        .map(UserResponse::from)
        .orElseThrow(ResourceNotFoundException::new);
  }
}
