package hg.reserve_buy.itemserviceapi.core.service;

import hg.reserve_buy.commonservicedata.exception.BadRequestException;
import hg.reserve_buy.itemserviceapi.core.entity.ItemEntity;
import hg.reserve_buy.itemserviceapi.core.entity.ItemInfoEntity;
import hg.reserve_buy.itemserviceapi.core.repository.ItemInfoRepository;
import hg.reserve_buy.itemserviceapi.core.repository.ItemRepository;
import hg.reserve_buy.itemserviceapi.core.service.dto.ItemBriefDto;
import hg.reserve_buy.itemserviceapi.core.service.dto.ItemDetailDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static hg.reserve_buy.itemserviceapi.core.entity.ItemEntity.*;
import static java.time.LocalDateTime.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceImplTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemInfoRepository itemInfoRepository;
    @Autowired
    ItemServiceImpl itemService;
    @PersistenceContext
    EntityManager em;

    Random r = new Random();

    List<ItemInfoEntity> savedItemInfoEntities = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
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
    public void failure_find_item_detail() {
        // given

        // when

        //then
        assertThatThrownBy(() -> itemService.getItemDetail(Long.MAX_VALUE))
                .isInstanceOf(BadRequestException.class);
    }

    private ItemInfoEntity createMockItemInfoEntity() {
        boolean isTimeDeal = (r.nextInt(2) & 1) == 1;
        ItemEntity itemEntity;
        if (isTimeDeal) {
            itemEntity = createTimeDeal(
                    createRandomUUID(), r.nextInt(100000), now().plusMinutes(r.nextInt(10))
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