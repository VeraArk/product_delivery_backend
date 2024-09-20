package org.product_delivery_backend.exceptions;

//Ошибки связанные с базой данных
public class DatabaseException extends AppBaseException {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
