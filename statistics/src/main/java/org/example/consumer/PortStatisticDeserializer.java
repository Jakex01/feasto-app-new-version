package org.example.consumer;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;
import org.shared.StatisticsEventWrapper;

public class PortStatisticDeserializer implements Deserializer<StatisticsEventWrapper.StatisticsEvent> {
    @Override
    public StatisticsEventWrapper.StatisticsEvent deserialize(String s, byte[] data) {
        try {
            return StatisticsEventWrapper.StatisticsEvent.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            throw new RuntimeException("excepiton while parsing");
        }
    }
}
