package org.product_delivery_backend.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.product_delivery_backend.dto.userDto.UserRequestDto;
import org.product_delivery_backend.dto.userDto.UserResponseDto;
import org.product_delivery_backend.entity.Role;
import org.product_delivery_backend.entity.User;
import org.product_delivery_backend.exceptions.NotFoundException;
import org.product_delivery_backend.mapper.UserMapper;
import org.product_delivery_backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private RoleService roleService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UserRequestDto userRequestDto;
    private User user;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {

        userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName("John");
        userRequestDto.setLastName("Doe");
        userRequestDto.setEmail("john.doe@example.com");
        userRequestDto.setPassword("password");
        userRequestDto.setPhone("123456789");


        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("encodedPassword");
        user.setPhone("123456789");
        user.setRoles(Set.of());


        userResponseDto = UserResponseDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("123456789")
                .build();
    }

    @Test
    void testRegisterUser() {
        when(userMapper.toEntity(userRequestDto)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(roleService.getRoleUser()).thenReturn(new Role());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.registerUser(userRequestDto);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void testGetAllUsers() {

        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toResponse(user)).thenReturn(userResponseDto);

        List<UserResponseDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    void testGetUserProfileByEmail_UserFound() {
        lenient().when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        lenient().when(userMapper.toResponse(user)).thenReturn(userResponseDto);


        UserResponseDto result = userService.getUserProfileByEmail("john.doe@example.com");

        assertNotNull(result, "Expected non-null result");
        assertEquals("John", result.getFirstName(), "Expected first name to match");
        assertEquals("Doe", result.getLastName(), "Expected last name to match");
        assertEquals("john.doe@example.com", result.getEmail(), "Expected email to match");
        assertEquals("123456789", result.getPhone(), "Expected phone to match");
    }

    @Test
    void testGetUserProfileByEmail_UserNotFound() {
        when(userRepository.findByEmail("not.found@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserProfileByEmail("not.found@example.com");
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testDeleteUser_UserExists() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void testDeleteUser_UserDoesNotExist() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.deleteUser(userId);
        });

        assertEquals("User with id 1 not found", exception.getMessage());
    }
}