package org.product_delivery_backend.exceptions;
//Для обработки ошибок, связанных с недействительными или некорректными JWT (JSON Web Token)
// при аутентификации пользователей
import org.springframework.security.core.AuthenticationException;

public class InvalidJwtException extends AuthenticationException {
    public InvalidJwtException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public InvalidJwtException(String msg) {
        super(msg);
    }
}
