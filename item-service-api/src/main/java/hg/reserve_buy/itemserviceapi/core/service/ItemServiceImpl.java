package hg.reserve_buy.itemserviceapi.core.service;

import hg.reserve_buy.commonkafka.constant.KafkaTopic;
import hg.reserve_buy.commonservicedata.exception.BadRequestException;
import hg.reserve_buy.itemserviceapi.core.entity.ItemEntity;
import hg.reserve_buy.itemserviceapi.core.entity.ItemInfoEntity;
import hg.reserve_buy.itemserviceapi.core.repository.ItemInfoRepository;
import hg.reserve_buy.itemserviceapi.core.repository.ItemRepository;
import hg.reserve_buy.itemserviceapi.core.service.dto.ItemBriefDto;
import hg.reserve_buy.itemserviceapi.core.service.dto.ItemDetailDto;
import hg.reserve_buy.itemserviceapi.presentation.request.ItemCreateRequest;
import hg.reserve_order.itemserviceevent.api.ItemCacheResponse;
import hg.reserve_order.itemserviceevent.event.ItemCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemInfoRepository itemInfoRepository;
    private final ItemProducerService itemProducerService;

    @Override
    public List<ItemBriefDto> getItemList() {
        List<ItemEntity> itemEntities = itemRepository.findAll();

        return itemEntities.stream()
                .map(this::parseEntityToBrief)
                .toList();
    }

    @Override
    public ItemDetailDto getItemDetail(Long itemNumber) {
        ItemInfoEntity itemInfoEntity = getItemInfoEntity(itemNumber);
        return parseEntityToDetail(itemInfoEntity);
    }

    @Override
    public Integer getItemPrice(Long itemNumber) {
        ItemEntity itemEntity = getItemEntity(itemNumber);
        return itemEntity.getPrice();
    }

    @Override
    @Transactional
    public Long createItem(ItemCreateRequest request) {
        ItemEntity itemEntity = request.toEntity();
        ItemInfoEntity itemInfoEntity = ItemInfoEntity.create(request.getDescription(), itemEntity);
        itemRepository.save(itemEntity);
        itemInfoRepository.save(itemInfoEntity);

        ItemCreatedEvent event = createItemEvent(request, itemEntity);
        itemProducerService.publish(
                String.valueOf(itemEntity.getItemNumber()), KafkaTopic.ITEM_CREATED, event
        );

        return itemEntity.getItemNumber();
    }

    @Override
    public ItemCacheResponse getOrderCache(Long itemNumber) {
        ItemEntity itemEntity = getItemEntity(itemNumber);
        return entityToCacheResponse(itemEntity);
    }

    private ItemCacheResponse entityToCacheResponse(ItemEntity itemEntity) {
        return new ItemCacheResponse(itemEntity.getItemNumber(), itemEntity.getPrice(), itemEntity.getType().name(), itemEntity.getStartAt());
    }

    private ItemBriefDto parseEntityToBrief(ItemEntity itemEntity) {
        return new ItemBriefDto(
                itemEntity.getItemNumber(),
                itemEntity.getName(),
                itemEntity.getPrice(),
                itemEntity.getType(),
                itemEntity.getStartAt()
        );
    }

    private ItemDetailDto parseEntityToDetail(ItemInfoEntity itemInfoEntity) {
        return new ItemDetailDto(
                itemInfoEntity.getItemEntity().getItemNumber(),
                itemInfoEntity.getItemEntity().getName(),
                itemInfoEntity.getContent(),
                itemInfoEntity.getItemEntity().getPrice(),
                itemInfoEntity.getItemEntity().getType(),
                itemInfoEntity.getItemEntity().getStartAt()
        );
    }

    private ItemInfoEntity getItemInfoEntity(Long itemNumber) {
        return itemInfoRepository.findByItemNumberFJItem(itemNumber).orElseThrow(
                () -> new BadRequestException("Not exists item.")
        );
    }

    private ItemEntity getItemEntity(Long itemNumber) {
        return itemRepository.findByItemNumber(itemNumber).orElseThrow(
                () -> new BadRequestException("Not exists item")
        );
    }

    private ItemCreatedEvent createItemEvent(ItemCreateRequest request, ItemEntity itemEntity) {
        return new ItemCreatedEvent(itemEntity.getItemNumber(), request.getStock(), request.getType().name(), request.getStartAt());
    }
}
