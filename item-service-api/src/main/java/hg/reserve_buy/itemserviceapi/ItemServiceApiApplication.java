package hg.reserve_buy.itemserviceapi;

import hg.reserve_buy.commonservicedata.response.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@Import({GlobalExceptionHandler.class})
@EnableDiscoveryClient
public class ItemServiceApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItemServiceApiApplication.class, args);
    }

}
