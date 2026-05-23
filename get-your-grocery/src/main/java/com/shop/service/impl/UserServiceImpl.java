package com.shop.service.impl;

import com.shop.dto.UserDto;
import com.shop.entity.User;
import com.shop.exceptions.ResourceNotFoundException;
import com.shop.repository.UserRepository;
import com.shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User login(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid username or password"));
    }

    @Override
    public User register(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Username already taken. Please choose another.");
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already registered.");
        }
        User user = User.builder()
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .email(userDto.getEmail())
                .address(userDto.getAddress())
                .contactNumber(userDto.getContactNumber())
                .role(userDto.getRole())
                .build();
        return userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public User updateProfile(Long id, UserDto userDto) {
        User user = findById(id);
        user.setAddress(userDto.getAddress());
        user.setContactNumber(userDto.getContactNumber());
        // Only update password if a new one is provided
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            user.setPassword(userDto.getPassword());
        }
        return userRepository.save(user);
    }
}
