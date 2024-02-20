package hg.reserve_buy.itemserviceapi.core.service;

import hg.reserve_buy.itemserviceapi.core.service.dto.ItemBriefDto;
import hg.reserve_buy.itemserviceapi.core.service.dto.ItemDetailDto;
import hg.reserve_buy.itemserviceapi.presentation.request.ItemCreateRequest;

import java.util.List;

public interface ItemService {
    List<ItemBriefDto> getItemList();

    ItemDetailDto getItemDetail(Long itemNumber);

    Integer getItemPrice(Long itemNumber);

    Long createItem(ItemCreateRequest request);
}
