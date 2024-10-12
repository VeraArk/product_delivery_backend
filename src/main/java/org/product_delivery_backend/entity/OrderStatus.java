package org.product_delivery_backend.entity;

public enum OrderStatus {

    PENDING, //ожидает подтверждения
    CONFIRMED, //подтвержден
    PAID, //  оплачен.
    PROCESSING, //в процессе обработки.
    SHIPPED, // отправлен.
    DELIVERED, // доставлен клиенту.
    CANCELLED,  // отменён.
    REFUNDED // средства за заказ возвращены.
}
