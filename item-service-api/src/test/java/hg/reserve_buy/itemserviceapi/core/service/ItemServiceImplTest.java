package hg.reserve_buy.itemserviceapi.core.service;

import hg.reserve_buy.commonkafka.constant.KafkaTopic;
import hg.reserve_buy.commonservicedata.exception.BadRequestException;
import hg.reserve_buy.itemserviceapi.core.entity.ItemEntity;
import hg.reserve_buy.itemserviceapi.core.entity.ItemInfoEntity;
import hg.reserve_buy.itemserviceapi.core.entity.ItemType;
import hg.reserve_buy.itemserviceapi.core.repository.ItemInfoRepository;
import hg.reserve_buy.itemserviceapi.core.repository.ItemRepository;
import hg.reserve_buy.itemserviceapi.core.service.dto.ItemBriefDto;
import hg.reserve_buy.itemserviceapi.core.service.dto.ItemDetailDto;
import hg.reserve_buy.itemserviceapi.init.InitData;
import hg.reserve_buy.itemserviceapi.presentation.request.ItemCreateRequest;
import hg.reserve_order.itemserviceevent.event.ItemCreatedEvent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static hg.reserve_buy.itemserviceapi.core.entity.ItemEntity.*;
import static java.time.LocalDateTime.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class ItemServiceImplTest {

    @MockBean
    InitData initData;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemInfoRepository itemInfoRepository;
    @Autowired
    ItemServiceImpl itemService;
    @MockBean
    ItemProducerService itemProducerService;
    @PersistenceContext
    EntityManager em;

    Random r = new Random();
    LocalDateTime currentDate;
    List<ItemInfoEntity> savedItemInfoEntities = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        currentDate = LocalDateTime.of(2019, 5, 7, 1, 1, 1, 0);
        int itemCount = 10;

        for (int i = 0; i < itemCount; i++) {
            savedItemInfoEntities.add(createMockItemInfoEntity());
        }
        flushAndClearPersistence();
    }

    @Test
    @DisplayName("저장된 아이템 정보는 정상적으로 조회되어야 한다.")
    void find_all_item_list() {
        // given
        List<ItemBriefDto> itemList = itemService.getItemList();
        List<ItemBriefDto> savedItemBriefList = savedItemInfoEntities.stream()
                .map(ItemInfoEntity::getItemEntity)
                .map(this::parseEntityToBrief)
                .toList();

        // when

        // then
        for (ItemBriefDto itemBriefDto : itemList) {
            ItemBriefDto itemBrief = savedItemBriefList.stream().filter(
                    a -> a.getItemNumber().equals(itemBriefDto.getItemNumber())
            ).findAny().orElseThrow();

            assertEquals(itemBrief.getItemNumber(), itemBriefDto.getItemNumber());
            assertEquals(itemBrief.getName(), itemBriefDto.getName());
            assertEquals(itemBrief.getStartAt(), itemBriefDto.getStartAt());
            assertEquals(itemBrief.getType(), itemBriefDto.getType());
            assertEquals(itemBrief.getPrice(), itemBriefDto.getPrice());
        }
    }

    @Test
    @DisplayName("존재하는 아이템 디테일 조회시, 정상적으로 조회되어야 한다.")
    void success_find_item_details() {
        // given
        List<ItemDetailDto> itemDetails = savedItemInfoEntities.stream()
                .map(this::parseEntityToDetail)
                .toList();

        // when

        // then
        for (ItemDetailDto itemDetail : itemDetails) {
            ItemDetailDto foundItemDetail = itemService.getItemDetail(itemDetail.getItemNumber());

            assertEquals(itemDetail.getItemNumber(), foundItemDetail.getItemNumber());
            assertEquals(itemDetail.getType(), foundItemDetail.getType());
            assertEquals(itemDetail.getContent(), foundItemDetail.getContent());
            assertEquals(itemDetail.getStartAt(), foundItemDetail.getStartAt());
            assertEquals(itemDetail.getPrice(), foundItemDetail.getPrice());
            assertEquals(itemDetail.getName(), foundItemDetail.getName());
        }
    }

    @Test
    @DisplayName("존재하지 않는 아이템 조회시 예외가 반환되어야 한다.")
    void failure_find_item_detail() {
        // given

        // when

        //then
        assertThatThrownBy(() -> itemService.getItemDetail(Long.MAX_VALUE))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("존재하는 아이템 가격 조회시 정상적으로 조회되어야 한다.")
    void success_inquiry_item_price() {
        // given
        List<ItemEntity> itemEntities = savedItemInfoEntities.stream()
                .map(ItemInfoEntity::getItemEntity)
                .toList();

        for (ItemEntity itemEntity : itemEntities) {
            // when
            Integer foundPrice = itemService.getItemPrice(itemEntity.getItemNumber());

            // then
            assertEquals(foundPrice, itemEntity.getPrice());
        }
    }

    @Test
    @DisplayName("존재하지 않는 아이템 가격 조회시 예외가 발생되어야 한다.")
    void failure_inquiry_item_price_not_exists_item() {
        // given

        // when

        // then
        assertThatThrownBy(() ->
                itemService.getItemPrice(Long.MAX_VALUE)
        ).isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("일반 아이템 생성 요청시 정상적으로 디비에 반영되어야 한다.")
    void success_general_item_create_request() {
        // given
        ItemCreateRequest request = new ItemCreateRequest(
                createRandomUUID(),
                r.nextInt(1, 10000),
                r.nextInt(1, 10000),
                ItemType.GENERAL,
                null,
                createRandomUUID()
        );

        // when
        Long itemNumber = itemService.createItem(request);
        ItemCreatedEvent itemCreatedEvent = createItemCreateEvent(request, itemNumber);

        // then
        ItemInfoEntity itemInfoEntity = itemInfoRepository.findByItemNumberFJItem(itemNumber).orElseThrow();

        assertEquals(itemNumber, itemInfoEntity.getItemEntity().getItemNumber());
        assertEquals(request.getName(), itemInfoEntity.getItemEntity().getName());
        assertEquals(request.getStartAt(), itemInfoEntity.getItemEntity().getStartAt());
        assertEquals(request.getType(), itemInfoEntity.getItemEntity().getType());
        assertEquals(request.getPrice(), itemInfoEntity.getItemEntity().getPrice());
        assertEquals(request.getDescription(), itemInfoEntity.getContent());

        verify(itemProducerService, times(1))
                .publish(String.valueOf(itemNumber), KafkaTopic.ITEM_CREATED,  itemCreatedEvent);
    }

    @Test
    @DisplayName("선착순 아이템 생성 요청시 정상적으로 디비에 반영되어야 한다.")
    void success_time_deal_item_create_request() {
        // given
        ItemCreateRequest request = new ItemCreateRequest(
                createRandomUUID(),
                r.nextInt(1, 10000),
                r.nextInt(1, 10000),
                ItemType.TIME_DEAL,
                now(),
                createRandomUUID()
        );

        // when
        Long itemNumber = itemService.createItem(request);
        ItemCreatedEvent itemCreatedEvent = createItemCreateEvent(request, itemNumber);

        // then
        ItemInfoEntity itemInfoEntity = itemInfoRepository.findByItemNumberFJItem(itemNumber).orElseThrow();

        assertEquals(itemNumber, itemInfoEntity.getItemEntity().getItemNumber());
        assertEquals(request.getName(), itemInfoEntity.getItemEntity().getName());
        assertEquals(request.getStartAt(), itemInfoEntity.getItemEntity().getStartAt());
        assertEquals(request.getType(), itemInfoEntity.getItemEntity().getType());
        assertEquals(request.getPrice(), itemInfoEntity.getItemEntity().getPrice());
        assertEquals(request.getDescription(), itemInfoEntity.getContent());

        verify(itemProducerService, times(1))
                .publish(String.valueOf(itemNumber), KafkaTopic.ITEM_CREATED,  itemCreatedEvent);
    }

    private ItemCreatedEvent createItemCreateEvent(ItemCreateRequest request, Long itemNumber) {
        return new ItemCreatedEvent(itemNumber, request.getStock(), request.getType().name(), request.getStartAt());
    }

    private ItemInfoEntity createMockItemInfoEntity() {

        boolean isTimeDeal = (r.nextInt(2) & 1) == 1;
        ItemEntity itemEntity;
        if (isTimeDeal) {
            itemEntity = createTimeDeal(
                    createRandomUUID(), r.nextInt(100000), currentDate.plusMinutes(r.nextInt(10))
            );
        } else {
            itemEntity = createGeneral(
                    createRandomUUID(), r.nextInt(100000)
            );
        }
        itemRepository.save(itemEntity);

        ItemInfoEntity itemInfoEntity = ItemInfoEntity.create(createRandomUUID(), itemEntity);
        itemInfoRepository.save(itemInfoEntity);

        return itemInfoEntity;
    }

    private String createRandomUUID() {
        return UUID.randomUUID().toString();
    }

    private void flushAndClearPersistence() {
        em.flush();
        em.clear();
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
}