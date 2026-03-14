package com.example.javaweb.service;

import com.example.javaweb.dto.LoginDTO;
import com.example.javaweb.dto.RegisterDTO;

public interface AuthService {
    String login(LoginDTO loginDTO);
    void register(RegisterDTO registerDTO);
    void logout();
}