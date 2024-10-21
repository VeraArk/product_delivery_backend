package org.product_delivery_backend.dto.userDto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.product_delivery_backend.entity.Role;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Set<Role> roles;

    public UserResponseDto(UserResponseDto response) {
        this.id = response.getId();
        this.firstName = response.getFirstName();
        this.lastName = response.getLastName();
        this.email = response.getEmail();
        this.phone = response.getPhone();
        this.roles = response.getRoles();
    }

}
