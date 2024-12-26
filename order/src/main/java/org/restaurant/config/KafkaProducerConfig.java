package org.restaurant.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.restaurant.producer.ProtNotificationSerializer;
import org.restaurant.producer.ProtStatisticsSerializer;
import org.shared.NotificationEventOuterClass;
import org.shared.StatisticsEventWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, NotificationEventOuterClass.NotificationEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-1:9092,kafka-2:9093,kafka-3:9094");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // Ustawiony key.serializer
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ProtNotificationSerializer.class); // Custom value.serializer
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ProducerFactory<String, StatisticsEventWrapper.StatisticsEvent> statisticsEventProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-1:9092,kafka-2:9093,kafka-3:9094");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // Ustawiony key.serializer
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ProtStatisticsSerializer.class); // Custom value.serializer
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, StatisticsEventWrapper.StatisticsEvent> statisticsEventKafkaTemplate() {
        return new KafkaTemplate<>(statisticsEventProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, NotificationEventOuterClass.NotificationEvent> notificationKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
