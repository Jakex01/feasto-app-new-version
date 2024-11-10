package org.example.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.model.enums.DeliveryOption;
import org.example.model.enums.OrderStatus;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Document(indexName = "statistics")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Statistics {
    @Id
    private String id;
    @Field(type = FieldType.Long)
    private Long orderId;
    @Field(type = FieldType.Long)
    private Long restaurantId;
    @Field(type = FieldType.Long)
    private Long userId;
    @Field(type = FieldType.Text)
    LocalDateTime orderDate;
    @Field(type = FieldType.Text)
    LocalDateTime finishedDate;
    @Field(type = FieldType.Double)
    Double totalPrice;
    @Field(type = FieldType.Text)
    private OrderStatus orderStatus;
    @Field(type = FieldType.Text)
    DeliveryOption deliveryOption;
}
