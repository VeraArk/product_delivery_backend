package org.product_delivery_backend.security.security_config;


import org.product_delivery_backend.security.filter.TokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final TokenFilter tokenFilter;

    public SecurityConfig(TokenFilter tokenFilter) {
        this.tokenFilter = tokenFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/login","/api/auth/refresh").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/page").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/products").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

       return http.build();
    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
