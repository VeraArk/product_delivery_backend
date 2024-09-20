package org.product_delivery_backend.exceptions;

//Исключение для ошибок авторизации
public class UnauthorizedException extends AppBaseException {
    public UnauthorizedException(String message) {
        super(message);
    }
}