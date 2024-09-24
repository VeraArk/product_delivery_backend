package org.product_delivery_backend;

import lombok.extern.slf4j.Slf4j;
import org.product_delivery_backend.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ProductDeliveryBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductDeliveryBackendApplication.class, args);
    }

    @Autowired
    private Config config;

    @Autowired
    public void start(StartToken unused)
    {
        log.info("It works, the config: {}", config);
    }
}
