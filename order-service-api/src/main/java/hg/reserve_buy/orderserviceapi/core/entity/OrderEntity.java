package hg.reserve_buy.orderserviceapi.core.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "_order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderEntity {
    @Id
    private String orderId;

    @Column(nullable = false)
    private Long itemNumber;

    @Column(nullable = false)
    private Long userNumber;

    @Column(nullable = false)
    private Integer unitPrice;

    @Column(nullable = false, name = "sell_count")
    private Integer count;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    public static OrderEntity create(
            String orderId, Long itemNumber, Long userNumber, Integer unitPrice, Integer count, OrderStatus status
    ) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.orderId = orderId;
        orderEntity.itemNumber = itemNumber;
        orderEntity.userNumber = userNumber;
        orderEntity.unitPrice = unitPrice;
        orderEntity.count = count;
        orderEntity.status = status;

        return orderEntity;
    }

    public void changeStatus(OrderStatus status) {
        this.status = status;
    }
}
