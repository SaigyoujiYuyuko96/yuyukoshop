package com.yuyukoshop.yuyukoshop.service;

import com.yuyukoshop.yuyukoshop.dto.UserProfileDto;
import com.yuyukoshop.yuyukoshop.entity.User;

public interface UserService {

    User getCurrentUser();

    User getUserById(Long id);

    void updateProfile(Long userId, UserProfileDto dto);
}
