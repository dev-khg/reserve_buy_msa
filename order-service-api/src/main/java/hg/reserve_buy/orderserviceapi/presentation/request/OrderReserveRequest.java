package hg.reserve_buy.orderserviceapi.presentation.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderReserveRequest {
    @NotNull(message = "item_number must be not null.")
    private Long itemNumber;
    @NotNull(message = "count must be not null.")
    private Integer count;
}
