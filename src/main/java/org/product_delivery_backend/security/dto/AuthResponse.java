package org.product_delivery_backend.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.product_delivery_backend.dto.userDto.UserResponseDto;

@Data
@AllArgsConstructor
public class AuthResponse {

private TokenResponseDto token;
private UserResponseDto user;


}
