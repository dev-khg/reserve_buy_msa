package hg.reserve_buy.itemserviceapi.core.service;

import hg.reserve_buy.itemserviceapi.core.service.dto.ItemBriefDto;
import hg.reserve_buy.itemserviceapi.core.service.dto.ItemDetailDto;

import java.util.List;

public interface ItemService {
    List<ItemBriefDto> getItemList();

    ItemDetailDto getItemDetail(Long itemNumber);

    Integer getItemPrice(Long itemNumber);
}
