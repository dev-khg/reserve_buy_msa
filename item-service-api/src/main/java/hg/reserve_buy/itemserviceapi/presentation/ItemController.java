package hg.reserve_buy.itemserviceapi.presentation;

import hg.reserve_buy.commonservicedata.response.ApiResponse;
import hg.reserve_buy.itemserviceapi.core.entity.ItemEntity;
import hg.reserve_buy.itemserviceapi.core.service.ItemService;
import hg.reserve_buy.itemserviceapi.core.service.dto.ItemBriefDto;
import hg.reserve_buy.itemserviceapi.core.service.dto.ItemDetailDto;
import hg.reserve_order.itemserviceevent.api.ItemCacheResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static hg.reserve_buy.commonservicedata.response.ApiResponse.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ApiResponse<List<ItemBriefDto>> getItems() {
        return success(itemService.getItemList());
    }

    @GetMapping("/{itemNumber}")
    public ApiResponse<ItemDetailDto> getItemDetail(@PathVariable Long itemNumber) {
        return success(itemService.getItemDetail(itemNumber));
    }

    @GetMapping("/{itemNumber}/price")
    public ApiResponse<Integer> getItemPrice(@PathVariable Long itemNumber) {
        return success(itemService.getItemPrice(itemNumber));
    }

    @GetMapping("/{itemNumber}/cache")
    public ItemCacheResponse getItem(@PathVariable Long itemNumber) {
        return itemService.getOrderCache(itemNumber);
    }
}
