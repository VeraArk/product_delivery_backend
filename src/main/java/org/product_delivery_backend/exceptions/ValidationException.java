package org.product_delivery_backend.exceptions;

//Ошибка валидации данных
public class ValidationException extends AppBaseException{
    //Поле для хранения имени поля, вызвавшего ошибку
    private String field;

    public ValidationException(String message, String field) {
        super(message);
        this.field = field;
    }

    public ValidationException(String message) {
        super(message);
    }

    public String getField() {return field;}
}
