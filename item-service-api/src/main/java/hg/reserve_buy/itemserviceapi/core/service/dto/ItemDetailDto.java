package hg.reserve_buy.itemserviceapi.core.service.dto;

import hg.reserve_buy.itemserviceapi.core.entity.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDetailDto {
    private Long itemNumber;
    private String name;
    private String content;
    private Integer price;
    private ItemType type;
    private LocalDateTime startAt;
}
