package hg.reserve_buy.itemserviceapi.core.service.dto;

import hg.reserve_buy.itemserviceapi.core.entity.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemBriefDto {
    private Long itemNumber;
    private String name;
    private Integer price;
    private ItemType type;
    private LocalDateTime startAt;
}
