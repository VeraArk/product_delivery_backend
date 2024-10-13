package org.product_delivery_backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Product delivery",
                description = "Application for home delivery of products.",
                version = "1.0.0",
                contact = @Contact(
                        name = "",
                        email = "",
                        url = "https://ait-tr.de"
                )
        )
)
public class SwaggerConfig {
}
