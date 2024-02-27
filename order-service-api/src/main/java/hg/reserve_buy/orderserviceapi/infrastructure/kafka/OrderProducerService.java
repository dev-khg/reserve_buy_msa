package hg.reserve_buy.orderserviceapi.infrastructure.kafka;

import hg.reserve_buy.commonkafka.config.KafkaProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducerService extends KafkaProducer {
    public OrderProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

}
