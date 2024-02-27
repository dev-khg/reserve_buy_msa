package hg.reserve_buy.stockserviceapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class StockServiceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockServiceApiApplication.class, args);
	}

}
