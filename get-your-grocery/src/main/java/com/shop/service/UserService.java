package com.shop.service;

import com.shop.dto.UserDto;
import com.shop.entity.User;

public interface UserService {

    User login(String username, String password);

    User register(UserDto userDto);

    User findById(Long id);

    User updateProfile(Long id, UserDto userDto);
}
