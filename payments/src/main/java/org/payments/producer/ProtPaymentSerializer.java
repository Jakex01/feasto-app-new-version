package org.payments.producer;

import org.apache.kafka.common.serialization.Serializer;
import org.shared.PaymentEventWrapper;

public class ProtPaymentSerializer implements Serializer<PaymentEventWrapper.PaymentEvent> {
    @Override
    public byte[] serialize(String topic, PaymentEventWrapper.PaymentEvent data) {
        return data.toByteArray();
    }
}
