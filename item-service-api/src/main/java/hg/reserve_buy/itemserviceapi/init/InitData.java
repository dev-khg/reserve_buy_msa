package hg.reserve_buy.itemserviceapi.init;

import hg.reserve_buy.itemserviceapi.core.entity.ItemType;
import hg.reserve_buy.itemserviceapi.core.service.ItemService;
import hg.reserve_buy.itemserviceapi.presentation.request.ItemCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class InitData implements ApplicationRunner {
    private final ItemService itemService;
    private Random r = new Random();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<ItemCreateRequest> itemRequestList = List.of(
                createGeneralItem("즉석밥"), createGeneralItem("라면"), createGeneralItem("커피"),
                createGeneralItem("신발"), createGeneralItem("즉석밥"),
                createTimeDealItem("아이폰"), createTimeDealItem("맥북")
        );

        for (ItemCreateRequest itemCreateRequest : itemRequestList) {
            itemService.createItem(itemCreateRequest);
        }

        itemService.createItem(new ItemCreateRequest(
                        "테스트용 ",
                        1,
                        100,
                        ItemType.TIME_DEAL,
                        LocalDateTime.now(),
                        UUID.randomUUID().toString()
        ));
    }

    private ItemCreateRequest createGeneralItem(String name) {
        return new ItemCreateRequest(
                name,
                r.nextInt(1000, 100000),
                r.nextInt(1, 10000),
                ItemType.GENERAL,
                null,
                UUID.randomUUID().toString()
        );
    }

    private ItemCreateRequest createTimeDealItem(String name) {
        return new ItemCreateRequest(
                name,
                r.nextInt(1000, 100000),
                r.nextInt(1, 10000),
                ItemType.TIME_DEAL,
                LocalDateTime.now().plusMinutes(r.nextInt(1, 1000)),
                UUID.randomUUID().toString()
        );
    }
}
