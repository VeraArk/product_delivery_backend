package org.product_delivery_backend.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

@Configuration
@PropertySource("file:.properties")
//@PropertySource("classpath:.properties")
@ConfigurationProperties
@Getter
@Setter
@Validated
@ToString
@NoArgsConstructor
public class Config {

@NotEmpty
private String fileStorage;

// The max file size in kilobytes
@Max(4096)
private long maxFileSize;
}
