package org.product_delivery_backend.service;


import lombok.RequiredArgsConstructor;
import org.product_delivery_backend.dto.userDto.UserProfileDto;
import org.product_delivery_backend.dto.userDto.UserRequestDto;
import org.product_delivery_backend.dto.userDto.UserResponseDto;
import org.product_delivery_backend.entity.User;
import org.product_delivery_backend.mapper.UserMapper;
import org.product_delivery_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Transactional
    public UserResponseDto registerUser(UserRequestDto request) {
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(roleService.getRoleUser()));
        userRepository.save(user);

        return userMapper.toResponse(user);
    }


    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public UserResponseDto getUserProfileByEmail(String username) {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            User u = user.get();
            return UserResponseDto.builder()
                    .id(u.getId())
                    .firstName(u.getFirstName())
                    .lastName(u.getLastName())
                    .email(u.getEmail())
                    .phone(u.getPhone())
                    .roles(u.getRoles())
                    .build();
        } else {
            logger.error("User with E-Mail " + username + " not found");
            throw new RuntimeException("User not found");
        }
    }


}
