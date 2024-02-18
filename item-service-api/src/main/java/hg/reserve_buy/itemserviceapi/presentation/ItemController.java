package hg.reserve_buy.itemserviceapi.presentation;

import hg.reserve_buy.commonservicedata.response.ApiResponse;
import hg.reserve_buy.itemserviceapi.core.service.ItemService;
import hg.reserve_buy.itemserviceapi.core.service.dto.ItemBriefDto;
import hg.reserve_buy.itemserviceapi.core.service.dto.ItemDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ApiResponse<List<ItemBriefDto>> getItems() {
        return ApiResponse.success(itemService.getItemList());
    }

    @GetMapping("/{itemNumber}")
    public ApiResponse<ItemDetailDto> getItemDetail(@PathVariable Long itemNumber) {
        return ApiResponse.success(itemService.getItemDetail(itemNumber));
    }
}
