package org.restaurant.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "application.urls")
public class UrlConfig {
    private String orderService;
    private String userService;
    private String restaurantIndex;
}
