package org.example.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "top_customers")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopCustomer {
    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long restaurantId;

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Double)
    private Double totalSpent;

    @Field(type = FieldType.Integer)
    private Integer totalOrders;
}
