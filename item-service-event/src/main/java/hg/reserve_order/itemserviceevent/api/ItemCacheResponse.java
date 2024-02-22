package hg.reserve_order.itemserviceevent.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemCacheResponse {
    private Long itemNumber;
    private Integer price;
    private String type;
    private LocalDateTime startAt;
}
