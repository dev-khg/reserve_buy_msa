package hg.reserve_buy.itemserviceapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hg.reserve_buy.commonservicedata.response.ApiResponse;
import hg.reserve_buy.itemserviceapi.core.entity.ItemEntity;
import hg.reserve_buy.itemserviceapi.core.entity.ItemInfoEntity;
import hg.reserve_buy.itemserviceapi.core.repository.ItemInfoRepository;
import hg.reserve_buy.itemserviceapi.core.repository.ItemRepository;
import hg.reserve_buy.itemserviceapi.core.service.ItemServiceImpl;
import hg.reserve_buy.itemserviceapi.core.service.dto.ItemBriefDto;
import hg.reserve_buy.itemserviceapi.core.service.dto.ItemDetailDto;
import hg.reserve_buy.itemserviceapi.init.InitData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static hg.reserve_buy.itemserviceapi.core.entity.ItemEntity.createGeneral;
import static hg.reserve_buy.itemserviceapi.core.entity.ItemEntity.createTimeDeal;

@Disabled
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public abstract class IntegrationTest {
    @MockBean
    InitData initData;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemInfoRepository itemInfoRepository;
    @Autowired
    ItemServiceImpl itemService;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @PersistenceContext
    EntityManager em;

    Random r = new Random();
    LocalDateTime currentDate;
    protected List<ItemInfoEntity> savedItemInfoEntities = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        currentDate = LocalDateTime.of(2019, 5, 7, 1, 1, 1, 0);
        int itemCount = 10;

        for (int i = 0; i < itemCount; i++) {
            savedItemInfoEntities.add(createMockItemInfoEntity());
        }
        flushAndClearPersistence();
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

    protected String createRandomUUID() {
        return UUID.randomUUID().toString();
    }

    protected void flushAndClearPersistence() {
        em.flush();
        em.clear();
    }

    protected ItemBriefDto parseEntityToBrief(ItemEntity itemEntity) {
        return new ItemBriefDto(
                itemEntity.getItemNumber(),
                itemEntity.getName(),
                itemEntity.getPrice(),
                itemEntity.getType(),
                itemEntity.getStartAt()
        );
    }

    protected ItemDetailDto parseEntityToDetail(ItemInfoEntity itemInfoEntity) {
        return new ItemDetailDto(
                itemInfoEntity.getItemEntity().getItemNumber(),
                itemInfoEntity.getItemEntity().getName(),
                itemInfoEntity.getContent(),
                itemInfoEntity.getItemEntity().getPrice(),
                itemInfoEntity.getItemEntity().getType(),
                itemInfoEntity.getItemEntity().getStartAt()
        );
    }

    protected <T> T readResponseJsonBody(String content, Class<T> clazz) throws Exception {
        TypeReference<ApiResponse<T>> typeReference = new TypeReference<>() { };
        ApiResponse<T> apiResponse = objectMapper.readValue(content, typeReference);
        T data = objectMapper.convertValue(apiResponse.getData(), clazz);

        return data;
    }
}
