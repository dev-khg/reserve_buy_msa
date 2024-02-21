package hg.reserve_buy.commonkafka.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static hg.reserve_buy.commonkafka.constant.KafkaTopic.ORDER_PAYED;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class KafkaListenerAop {

    @Before("@annotation(org.springframework.kafka.annotation.KafkaListener)")
    public void log(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        KafkaListener kafkaListener = method.getAnnotation(KafkaListener.class);

        log.info("<======= [KAFKA] Topic = {} | data = {}", kafkaListener.topics(), joinPoint.getArgs());
    }
}
