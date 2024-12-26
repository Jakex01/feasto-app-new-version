package org.restaurant.producer;

import org.apache.kafka.common.serialization.Serializer;
import org.shared.StatisticsEventWrapper;

public class ProtStatisticsSerializer implements Serializer<StatisticsEventWrapper.StatisticsEvent> {
    @Override
    public byte[] serialize(String topic, StatisticsEventWrapper.StatisticsEvent data) {
        return data.toByteArray();
    }
}
