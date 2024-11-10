package org.notification.consumer;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;
import org.shared.NotificationEventOuterClass;

public class ProtNotificationDeserializer implements Deserializer<NotificationEventOuterClass.NotificationEvent> {
    @Override
    public NotificationEventOuterClass.NotificationEvent deserialize(String topic, byte[] data) {
        try {
            return NotificationEventOuterClass.NotificationEvent.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            throw new RuntimeException("excepiton while parsing");
        }
    }
}
