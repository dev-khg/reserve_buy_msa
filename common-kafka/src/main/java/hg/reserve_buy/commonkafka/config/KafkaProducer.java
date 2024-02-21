package hg.reserve_buy.commonkafka.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String topic, Object data) {
        log.info("======> KAFKA TOPIC [{}], Data[{}]", topic, data);
        kafkaTemplate.send(topic, data);
    }

    public void publish(String key, String topic, Object data) {
        log.info("======> KAFKA TOPIC [{}]  Data[{}] Key [{}]", topic, data, key);
        kafkaTemplate.send(topic, key, data);
    }
}
