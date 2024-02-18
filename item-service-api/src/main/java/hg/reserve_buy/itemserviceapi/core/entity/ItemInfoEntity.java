package hg.reserve_buy.itemserviceapi.core.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemInfoNumber;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, unique = true, name = "item_number")
    private ItemEntity itemEntity;

    public static ItemInfoEntity create(String content, ItemEntity itemEntity) {
        ItemInfoEntity entity = new ItemInfoEntity();
        entity.content = content;
        entity.itemEntity = itemEntity;

        return entity;
    }
}
