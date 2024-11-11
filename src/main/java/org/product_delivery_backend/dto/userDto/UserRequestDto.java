package org.product_delivery_backend.dto.userDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDto {
    private String firstName;
    private String lastName;
    @NotNull
    @Email
    private String email;
    @NotNull
    private String password;
    private String phone;
}
