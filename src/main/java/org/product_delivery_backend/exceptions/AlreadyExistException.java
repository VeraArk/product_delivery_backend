package org.product_delivery_backend.exceptions;
//Ресур уже существует
public class AlreadyExistException extends AppBaseException {
    public AlreadyExistException(String message) {
        super(message);
    }
    public AlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
