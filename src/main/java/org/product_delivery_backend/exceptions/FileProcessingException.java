package org.product_delivery_backend.exceptions;

//Для ошибок, возникающие при работе с файлами
public class FileProcessingException extends AppBaseException {
    public FileProcessingException(String message) {
        super(message);
    }
}
