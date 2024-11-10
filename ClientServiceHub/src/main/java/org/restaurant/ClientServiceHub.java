package org.restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableDiscoveryClient
public class ClientServiceHub {
    public static void main(String[] args) {
        SpringApplication.run(ClientServiceHub.class, args);
    }
}