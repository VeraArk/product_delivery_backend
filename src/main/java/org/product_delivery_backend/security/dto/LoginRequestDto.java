package org.product_delivery_backend.security.dto;

import java.util.Objects;

public record LoginRequestDto (String username, String password) {

}
