package hg.reserve_buy.stockserviceapi.core.entity;

import hg.reserve_buy.commonservicedata.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stock")
public class StockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockNumber;

    @Column(nullable = false, unique = true)
    private Long itemNumber;

    @Column(nullable = false)
    private Integer total;

    public static StockEntity create(Long itemNumber, Integer total) {
        StockEntity stockEntity = new StockEntity();
        stockEntity.itemNumber = itemNumber;
        stockEntity.total = total;

        return stockEntity;
    }

    public void increaseStock(Integer count) {
        if (total != null) {
            this.total += count;
        }
    }

    public void decreaseStock(Integer count) {
        if(total == null) return;
        if (total - count < 0) {
            throw new BadRequestException("재고가 충분하지 않습니다.");
        }
        this.total -= count;
    }
}
