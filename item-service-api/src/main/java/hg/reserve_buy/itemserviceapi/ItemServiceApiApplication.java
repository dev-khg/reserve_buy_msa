package hg.reserve_buy.itemserviceapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ItemServiceApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItemServiceApiApplication.class, args);
    }

}
