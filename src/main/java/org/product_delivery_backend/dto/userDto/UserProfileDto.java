package org.product_delivery_backend.dto.userDto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

}
