package hg.reserve_buy.itemserviceapi.infrastructure;

import hg.reserve_buy.commonkafka.config.KafkaProducerConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
@ComponentScan(basePackageClasses = {KafkaProducerConfig.class})
public class KafkaConfig {
}
