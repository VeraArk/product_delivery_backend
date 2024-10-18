package org.product_delivery_backend.entity;

public enum Category {
    DAIRY("200"),
    MEAT("300"),
    BEVERAGES("900"),
    VEGETABLES_FRUITS("500"),
    BAKERY("100"),
    SEAFOOD("400"),
    SNACKS_SWEETS("600"),
    GRAINS("700"),
    FROZEN("800");

    private final String code;

    Category(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Category fromString(String categoryName) {
        for (Category category : Category.values()) {
            if (category.name().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid category name: " + categoryName);
    }
}