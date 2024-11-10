package org.restaurant.producer;

import org.apache.kafka.common.serialization.Serializer;
import org.shared.NotificationEventOuterClass;

public class ProtNotificationSerializer implements Serializer<NotificationEventOuterClass.NotificationEvent> {
    @Override
    public byte[] serialize(String topic, NotificationEventOuterClass.NotificationEvent data) {
        return data.toByteArray();
    }
}
