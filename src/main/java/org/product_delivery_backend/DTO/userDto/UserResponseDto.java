package org.product_delivery_backend.dto.userDto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.product_delivery_backend.entity.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Role role;

    public UserResponseDto(UserResponseDto response) {
        this.id = response.getId();
        this.firstName = response.getFirstName();
        this.lastName = response.getLastName();
        this.email = response.getEmail();
        this.phone = response.getPhone();
        this.role = response.getRole();
    }
}
