package org.product_delivery_backend.exceptions;
//Когда ресурс не найден
public class NotFoundException extends AppBaseException {
    public NotFoundException(String message) {
        super(message);
    }
}
