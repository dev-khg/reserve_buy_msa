package hg.reserve_buy.commonkafka.config;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
public abstract class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String topic, Object data) {
        kafkaTemplate.send(topic, data);
    }
}
