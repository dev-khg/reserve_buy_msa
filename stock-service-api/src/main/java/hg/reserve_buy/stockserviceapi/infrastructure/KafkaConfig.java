package hg.reserve_buy.stockserviceapi.infrastructure;

import hg.reserve_buy.commonkafka.config.KafkaConsumerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
@Import({KafkaConsumerConfig.class})
public class KafkaConfig {
}
