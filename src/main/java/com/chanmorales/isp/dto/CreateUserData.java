package com.chanmorales.isp.dto;

import com.chanmorales.isp.model.Role;

public record CreateUserData(String username, String email, String password, Role role) {}
