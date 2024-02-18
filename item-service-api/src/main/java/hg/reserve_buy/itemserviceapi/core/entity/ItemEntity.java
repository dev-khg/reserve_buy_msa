package hg.reserve_buy.itemserviceapi.core.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "item")
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemNumber;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemType type;

    @Column(nullable = true)
    private LocalDateTime startAt;

    @CreatedDate
    private LocalDateTime createAt;

    @LastModifiedDate
    private LocalDateTime updateAt;

    public static ItemEntity createGeneral(String name, int price) {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.name = name;
        itemEntity.price = price;
        itemEntity.type = ItemType.GENERAL;

        return itemEntity;
    }

    public static ItemEntity createTimeDeal(String name, int price, LocalDateTime startAt) {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.name = name;
        itemEntity.price = price;
        itemEntity.startAt = startAt;
        itemEntity.type = ItemType.TIME_DEAL;

        return itemEntity;
    }
}
