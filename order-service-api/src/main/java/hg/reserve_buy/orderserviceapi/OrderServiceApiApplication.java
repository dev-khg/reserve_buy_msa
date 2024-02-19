package hg.reserve_buy.orderserviceapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
public class OrderServiceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApiApplication.class, args);
	}

}
