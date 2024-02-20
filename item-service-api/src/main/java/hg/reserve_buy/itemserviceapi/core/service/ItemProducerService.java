package hg.reserve_buy.itemserviceapi.core.service;

import hg.reserve_buy.commonkafka.config.KafkaProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ItemProducerService extends KafkaProducer {
    public ItemProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }
}
