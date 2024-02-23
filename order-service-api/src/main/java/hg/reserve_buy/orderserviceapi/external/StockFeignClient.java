package hg.reserve_buy.orderserviceapi.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "STOCK-SERVICE", path = "/stock")
public interface StockFeignClient {

    @GetMapping("/{itemNumber}/cache")
    Integer getStockCache(@PathVariable("itemNumber") Long itemNumber);
}
