package com.yuyukoshop.yuyukoshop.service;

import com.yuyukoshop.yuyukoshop.dto.LoginRequest;
import com.yuyukoshop.yuyukoshop.dto.LoginResponse;
import com.yuyukoshop.yuyukoshop.dto.RegisterRequest;

public interface AuthService {

    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
