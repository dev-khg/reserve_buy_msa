package hg.reserve_buy.itemserviceapi.presentation.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import hg.reserve_buy.commonservicedata.exception.BadRequestException;
import hg.reserve_buy.itemserviceapi.core.entity.ItemEntity;
import hg.reserve_buy.itemserviceapi.core.entity.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemCreateRequest {
    @NotBlank(message = "name must be not blank.")
    private String name;
    @NotNull(message = "price must be not null.")
    private Integer price;
    @NotNull(message = "stock must be not null.")
    private Integer stock;
    @NotNull(message = "type must be not null.")
    private ItemType type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmm", timezone = "Asia/Seoul")
    private LocalDateTime startAt;

    @NotBlank(message = "description must be not blank.")
    private String description;

    public ItemEntity toEntity() {
        this.validation();
        if (type == ItemType.TIME_DEAL) {
            return ItemEntity.createTimeDeal(name, price, startAt);
        } else if(type == ItemType.GENERAL) {
            return ItemEntity.createGeneral(name, price);
        }
        return null;
    }

    private void validation() {
        if(type == ItemType.TIME_DEAL && startAt == null) {
            throw new BadRequestException("선착순 아이템은 시작시간을 지정해야 합니다.");
        }
    }
}
