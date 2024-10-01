package org.product_delivery_backend.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.product_delivery_backend.DTO.userDto.UserRequestDto;
import org.product_delivery_backend.DTO.userDto.UserResponseDto;
import org.product_delivery_backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.registerUser(userRequestDto));
    }

    @GetMapping
   ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
