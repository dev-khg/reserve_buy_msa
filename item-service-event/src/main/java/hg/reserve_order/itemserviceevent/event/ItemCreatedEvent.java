package hg.reserve_order.itemserviceevent.event;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ItemCreatedEvent {
    private Long itemNumber;
    private Integer count;
    private String status;
    private LocalDateTime startAt;
}
