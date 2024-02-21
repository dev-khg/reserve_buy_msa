package hg.reserve_buy.stockserviceapi.infrastructure;

import hg.reserve_buy.commonkafka.aop.KafkaListenerAop;
import hg.reserve_buy.commonkafka.config.KafkaConsumerConfig;
import hg.reserve_order.itemserviceevent.event.ItemCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@Import({KafkaConsumerConfig.class, KafkaListenerAop.class})
public class KafkaConfig {

}
