package org.restaurant;

import org.restaurant.model.elasticsearch.ElasticRestaurant;
import org.restaurant.service.RestaurantService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableDiscoveryClient
public class RestaurantApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestaurantApplication.class, args);
    }
    @Bean
    CommandLineRunner init(RestaurantService restaurantService) {
        return args -> {
            List<ElasticRestaurant> restaurants = new ArrayList<>();

            // Manually create each restaurant with a unique name and specific attributes
            restaurants.add(ElasticRestaurant.builder()
                    .name("Golden Dragon")
                    .description("Authentic Chinese cuisine with a modern twist.")
                    .openingHours("10:00 - 22:00")
                    .rating(4.5)
                    .foodType("Chinese")
                    .prices(45)
                    .commentsCount(120)
                    .build());

            restaurants.add(ElasticRestaurant.builder()
                    .name("Roma Cucina")
                    .description("Traditional Italian recipes passed down through generations.")
                    .openingHours("10:00 - 22:00")
                    .rating(4.0)
                    .foodType("Italian")
                    .prices(60)
                    .commentsCount(110)
                    .build());

            restaurants.add(ElasticRestaurant.builder()
                    .name("Beijing Bites")
                    .description("A taste of Beijing in the heart of the city.")
                    .openingHours("10:00 - 22:00")
                    .rating(3.5)
                    .foodType("Chinese")
                    .prices(30)
                    .commentsCount(85)
                    .build());

            restaurants.add(ElasticRestaurant.builder()
                    .name("Venice Veneto")
                    .description("Enjoy the romantic vibes of Venice with our exquisite dishes.")
                    .openingHours("10:00 - 22:00")
                    .rating(5.0)
                    .foodType("Italian")
                    .prices(15)
                    .commentsCount(95)
                    .build());

            restaurants.add(ElasticRestaurant.builder()
                    .name("Shanghai Spring")
                    .description("Fresh and vibrant flavors from Shanghai.")
                    .openingHours("10:00 - 22:00")
                    .rating(3.0)
                    .foodType("Chinese")
                    .prices(45)
                    .commentsCount(130)
                    .build());

            restaurants.add(ElasticRestaurant.builder()
                    .name("Beijing Delights")
                    .description("Authentic Beijing cuisine with a modern touch.")
                    .openingHours("11:00 - 23:00")
                    .rating(4.0)
                    .foodType("Chinese")
                    .prices(30)
                    .commentsCount(85)
                    .build());

            restaurants.add(ElasticRestaurant.builder()
                    .name("Venice Affair")
                    .description("Experience the heart of Italy with every bite.")
                    .openingHours("12:00 - 23:00")
                    .rating(4.5)
                    .foodType("Italian")
                    .prices(60)
                    .commentsCount(95)
                    .build());

            restaurants.add(ElasticRestaurant.builder()
                    .name("Sicilian Pasta")
                    .description("Traditional Sicilian pasta dishes from old family recipes.")
                    .openingHours("10:00 - 22:00")
                    .rating(3.5)
                    .foodType("Italian")
                    .prices(25)
                    .commentsCount(120)
                    .build());

            restaurants.add(ElasticRestaurant.builder()
                    .name("Napoli Nights")
                    .description("Classic Neapolitan pizzas and more.")
                    .openingHours("10:00 - 24:00")
                    .rating(5.0)
                    .foodType("Italian")
                    .prices(15)
                    .commentsCount(140)
                    .build());

            restaurants.add(ElasticRestaurant.builder()
                    .name("Canton Garden")
                    .description("A garden of Cantonese delights awaits you.")
                    .openingHours("11:00 - 21:00")
                    .rating(3.5)
                    .foodType("Chinese")
                    .prices(40)
                    .commentsCount(115)
                    .build());

            restaurantService.deleteAllRestaurants();
            restaurants.forEach(restaurantService::saveRestaurant);
        };
    }


}
