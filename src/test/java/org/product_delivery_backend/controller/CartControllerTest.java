package org.product_delivery_backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.product_delivery_backend.entity.Role;
import org.product_delivery_backend.entity.User;
import org.product_delivery_backend.exceptions.NotFoundException;
import org.product_delivery_backend.repository.RoleRepository;
import org.product_delivery_backend.repository.UserRepository;
import org.product_delivery_backend.security.dto.LoginRequestDto;
import org.product_delivery_backend.security.dto.TokenResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CartControllerTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate template;
    private HttpHeaders headers;
    private String adminAccessToken;
    private String userAccessToken;


    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

//    private static final String TEST_PRODUCT_TITLE = "Test Product";
//    private static final int TEST_PRODUCT_PRICE = 12;
//    private static final  String TEST_PRODUCT_CODE= "Test ProductCode";
//    private static final String TEST_PRODUCT_QUANTITY = "Test MinQuantity";
//    private static final String TEST_DESCRIPTION = "Test Product Description";

    private static final String TEST_ADMIN_EMAIL = "email1@gmal.com";
    private static final String TEST_USER_EMAIL = "email2@gmal.com";
    private static final String FIRST_NAME = "First name";
    private static final String LAST_NAME = "Last name";
    private static final String PHONE_ADMIN = "PHONE_ADMIN";
    private static final String PHONE_USER = "PHONE_USER";


    private static final String TEST_PASSWORD = "$2a$10$G0JgWc2R.9uDn0Wn5BT9XO012sYVwSm482V0UfTPb7MqDk6fRfJVO";
    private static final String ROLE_ADMIN_TITLE = "ROLE_ADMIN";
    private static final String ROLE_USER_TITLE = "ROLE_USER";

    private final String URL_HOST = "http://localhost:";
    private final String AUTH_RESOURCE_NAME = "/api/auth";
    private final String CART_RESOURCE_NAME = "/api/cart";
    private final String LOGIN_ENDPOINT = "/login";

    private final String BEARER_TOKEN_PREFIX = "Bearer ";

    @BeforeEach
    public void setUp() {

        template = new TestRestTemplate();
        headers = new HttpHeaders();

        Role roleUser=null;
        Role roleAdmin;

        User admin = userRepository.findByEmail(TEST_ADMIN_EMAIL).orElse(null);
        User user = userRepository.findByEmail(TEST_USER_EMAIL).orElse(null);

        if (admin == null) {
            roleAdmin = roleRepository.findByTitle(ROLE_ADMIN_TITLE).orElseThrow(() -> new NotFoundException("Role ADMIN is not found"));
            roleUser = roleRepository.findByTitle(ROLE_USER_TITLE).orElseThrow(() -> new NotFoundException("Role USER not found in DB"));
            admin = new User();
            admin.setFirstName(FIRST_NAME);
            admin.setLastName(LAST_NAME);
            admin.setEmail(TEST_ADMIN_EMAIL);
            admin.setPassword(encoder.encode(TEST_PASSWORD));
            admin.setRoles(Set.of(roleAdmin, roleUser));
            admin.setPhone(PHONE_ADMIN);

            userRepository.save(admin);
        }

        if (user == null) {
            roleUser = (roleUser != null)
                    ? roleUser
                    : roleRepository.findByTitle(ROLE_USER_TITLE).orElseThrow(() -> new RuntimeException("Role USER not found in DB"));

            user = new User();
            user.setFirstName(FIRST_NAME);
            user.setLastName(LAST_NAME);
            user.setEmail(TEST_USER_EMAIL);
            user.setPassword(encoder.encode(TEST_PASSWORD));
            user.setRoles(Set.of(roleUser));
            user.setPhone(PHONE_USER);

            userRepository.save(user);
        }

        LoginRequestDto loginAdminDto = new LoginRequestDto(TEST_ADMIN_EMAIL, TEST_PASSWORD);
        LoginRequestDto loginUserDto = new LoginRequestDto(TEST_USER_EMAIL, TEST_PASSWORD);

        String authUrl = URL_HOST + port + AUTH_RESOURCE_NAME + LOGIN_ENDPOINT;

        HttpEntity<LoginRequestDto> request = new HttpEntity<>(loginAdminDto, headers);

        ResponseEntity<TokenResponseDto> response = template.exchange(
                authUrl,                // URL
                HttpMethod.POST,        //метод
                request,                 //запрос, который отправляем
                TokenResponseDto.class    // что хотім получіть в ответ
        );

        assertTrue(response.hasBody(), "Authorization admin response body is empty");

        TokenResponseDto tokenResponseDto = response.getBody();

        adminAccessToken = BEARER_TOKEN_PREFIX + tokenResponseDto.getAccessToken();

        // Получаем токен юзера
        request = new HttpEntity<>(loginUserDto, headers);
        response = template.exchange(
                authUrl,
                HttpMethod.POST,
                request,
                TokenResponseDto.class
        );

        assertTrue(response.hasBody(), "Authorization user response body is empty");
        tokenResponseDto = response.getBody();
        userAccessToken = BEARER_TOKEN_PREFIX + tokenResponseDto.getAccessToken();

    }

    @Test
    public void test() {

    }
}

//}