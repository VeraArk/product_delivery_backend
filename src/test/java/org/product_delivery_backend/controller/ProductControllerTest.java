
package org.product_delivery_backend.controller;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.product_delivery_backend.dto.productDto.AllProductResponseDto;
import org.product_delivery_backend.dto.productDto.ProductRequestDto;
import org.product_delivery_backend.dto.productDto.ProductResponseDto;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductControllerTest {

    @LocalServerPort
    private int port;
    private TestRestTemplate template;
    private HttpHeaders headers;


    ProductResponseDto responseDto;
    private String adminAccessToken;
    private String userAccessToken;

    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;


    private static final String TEST_PRODUCT_TITLE = "TestProduct";
    private static final int TEST_PRODUCT_PRICE = 12;
    private static final String TEST_PRODUCT_CODE = "200-BR098766";
    private static final String TEST_PRODUCT_QUANTITY = "250 g";
    private static final String TEST_DESCRIPTION = "Test Product Description";
    private static final String FOTO_LINK = "HTTPS://VARUS.UA/IMG/PRODUCT/1140";

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
    private final String PRODUCTS_RESOURCE_NAME = "/api/products";
    private final String LOGIN_ENDPOINT = "/login";

    private final String BEARER_TOKEN_PREFIX = "Bearer ";

    @BeforeEach
    public void setUp() {
        template = new TestRestTemplate(); //отправляет запросы
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Создаем тестовый продукт
        responseDto = new ProductResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle(TEST_PRODUCT_TITLE);
        responseDto.setPrice(new BigDecimal(TEST_PRODUCT_PRICE));
        responseDto.setProductCode(TEST_PRODUCT_CODE);
        responseDto.setMinQuantity(TEST_PRODUCT_QUANTITY);
        responseDto.setDescription(TEST_DESCRIPTION);
        responseDto.setPhotoLink(FOTO_LINK);

        Role roleUser=null;
        Role roleAdmin;

        // Пытаемся найті тестовых пользователей в БД
        User admin = userRepository.findByEmail(TEST_ADMIN_EMAIL).orElse(null);
        User user = userRepository.findByEmail(TEST_USER_EMAIL).orElse(null);

        //если их нет в БД, создаем
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
        // Получить от сервера токены!
        // Создаем объекты LoginRequestDto для админа и пользователя

        LoginRequestDto loginAdminDto = new LoginRequestDto(TEST_ADMIN_EMAIL, TEST_PASSWORD);
        LoginRequestDto loginUserDto = new LoginRequestDto(TEST_USER_EMAIL, TEST_PASSWORD);

        // POST -> http://localhost:56500/api/auth/login
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

        System.out.println("Authorization response body: "  + " - " + response.getBody());

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
    @Order(1)
    public void findAllProducts() {
        String url = "http://localhost:" + port + PRODUCTS_RESOURCE_NAME;

        ResponseEntity<List<AllProductResponseDto>> response = template.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());
    }

    @Test
    @Order(2)
    public void notAddProductWithoutAuthorization() {
        String url = URL_HOST + port + PRODUCTS_RESOURCE_NAME;

        HttpEntity<ProductResponseDto> request = new HttpEntity<>(responseDto, headers);

        ResponseEntity<Void> response = template.exchange(
                url,
                HttpMethod.POST,
                request,
                Void.class // не интересует тело ответа
        );
        // Проверка статуса: статус 403 Forbidden
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(), "Response has unexpected status");
        System.out.println(response.getStatusCode());

    }

    @Test
    @Order(3)
    public void addProduct() {


        String url = "http://localhost:" + port + PRODUCTS_RESOURCE_NAME;

        ProductRequestDto productRequest = new ProductRequestDto("New Product", new BigDecimal(15), "123", "500g", "Description", "photoLink");
        headers.setBearerAuth(adminAccessToken);

        ResponseEntity<ProductResponseDto> response = template.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(productRequest, headers),
                ProductResponseDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("New Product", response.getBody().getTitle());
    }


    @Test
    @Order(5)
    public void findAllProductsByPage() {
        String url = "http://localhost:" + port + PRODUCTS_RESOURCE_NAME + "/page?page=0&size=2";

        ResponseEntity<Page<AllProductResponseDto>> response = template.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());
        assertEquals(2, response.getBody().getContent().size());
    }


    @Test
    @Order(6)
    public void deleteProduct() {
        String url = "http://localhost:" + port + PRODUCTS_RESOURCE_NAME + "/99";

        headers.setBearerAuth(adminAccessToken);
        System.out.println(adminAccessToken);

        ResponseEntity<Void> response = template.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @Order(7)
    public void findProductById() {
        String url = "http://localhost:" + port + PRODUCTS_RESOURCE_NAME + "/1";

        ResponseEntity<ProductResponseDto> response = template.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProductResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product 1", response.getBody().getTitle());
    }
}


//
//
//
//
//
//
//
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.product_delivery_backend.config.Config;
//import org.product_delivery_backend.dto.productDto.ProductResponseDto;
//import org.product_delivery_backend.repository.UserRepository;
//import org.product_delivery_backend.service.ProductService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//
//import java.math.BigDecimal;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@Import(Config.class)
//@WebMvcTest(ProductController.class)
//
//public class ProductControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ProductService productService;
//
//    @MockBean
//    private UserRepository userRepository;
//
//    ProductResponseDto productResponseDto;
//
//    private final String PRODUCTS_RESOURCE_NAME = "/api/products";
//
//    private static final String TEST_PRODUCT_TITLE = "TestProduct";
//    private static final int TEST_PRODUCT_PRICE = 12;
//    private static final String TEST_PRODUCT_CODE = "200-BR098766";
//    private static final String TEST_PRODUCT_QUANTITY = "250 g";
//    private static final String TEST_DESCRIPTION = "Test Product Description";
//    private static final String FOTO_LINK = "HTTPS://VARUS.UA/IMG/PRODUCT/1140";
//
//
//    @BeforeEach
//    public void setUp() {
//
//        // Создаем тестовый продукт
//        productResponseDto = new ProductResponseDto();
//        productResponseDto.setId(1L);
//        productResponseDto.setTitle(TEST_PRODUCT_TITLE);
//        productResponseDto.setPrice(new BigDecimal(TEST_PRODUCT_PRICE));
//        productResponseDto.setProductCode(TEST_PRODUCT_CODE);
//        productResponseDto.setMinQuantity(TEST_PRODUCT_QUANTITY);
//        productResponseDto.setDescription(TEST_DESCRIPTION);
//        productResponseDto.setPhotoLink(FOTO_LINK);
//
//    }
//
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    public void findProductById() throws Exception {
//        // Мокаем ProductService
//        when(productService.findProductById(1L)).thenReturn(productResponseDto);
//
//        // Выполняем запрос к контроллеру
//        mockMvc.perform(get(PRODUCTS_RESOURCE_NAME + "/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title").value(TEST_PRODUCT_TITLE));
//    }
//}
