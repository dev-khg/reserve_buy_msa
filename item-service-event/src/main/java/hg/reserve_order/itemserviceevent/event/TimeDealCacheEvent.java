package hg.reserve_order.itemserviceevent.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeDealCacheEvent {
    private Long itemNumber;
}
