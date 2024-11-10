package org.restaurant.model.elasticsearch;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "restaurants")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticRestaurant {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Text)
    private String openingHours;

    @Field(type = FieldType.Double)
    private double rating;

    @Field(type = FieldType.Text)
    private String foodType;

    @Field(type = FieldType.Integer)
    private int prices;

    @Field(type = FieldType.Text)
    private String image;

    @Field(type = FieldType.Long)
    private long commentsCount;


}
