package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class StatisticApplication {
    public static void main(String[] args) {
        System.out.println("Hello 2");
        SpringApplication.run(StatisticApplication.class, args);
    }
}