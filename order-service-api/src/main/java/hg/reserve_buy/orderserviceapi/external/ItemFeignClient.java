package hg.reserve_buy.orderserviceapi.external;

import hg.reserve_buy.commonservicedata.response.ApiResponse;
import hg.reserve_order.itemserviceevent.api.ItemCacheResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ITEM-SERVICE", path = "/item")
public interface ItemFeignClient {

    @GetMapping("/{itemNumber}/price")
    ApiResponse<Integer> getItemPrice(@PathVariable("itemNumber") Long itemNumber);

    @GetMapping("/{itemNumber}/cache")
    ItemCacheResponse getItemCache(@PathVariable("itemNumber") Long itemNumber);
}
