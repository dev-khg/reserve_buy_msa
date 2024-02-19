package hg.reserve_buy.orderserviceapi.infrastructure.kafka;

import hg.reserve_buy.commonkafka.config.KafkaConsumerConfig;
import hg.reserve_buy.commonkafka.config.KafkaProducerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
@Import({KafkaConsumerConfig.class, KafkaProducerConfig.class})
public class KafkaConfig {
}
