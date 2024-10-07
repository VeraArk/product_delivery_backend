package org.product_delivery_backend.security;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String error = (String) request.getAttribute("error");

        if ("invalid_token".equals(error)) {
            // Возвращаем статус 401, если токен недействителен или отсутствует
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing token");
        } else {
            // Возвращаем статус 403, если проблема с правами доступа
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        }
    }
}