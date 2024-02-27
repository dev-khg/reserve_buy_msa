package hg.reserve_buy.itemserviceapi.core.repository;

import hg.reserve_buy.itemserviceapi.core.entity.ItemInfoEntity;

import java.util.Optional;

public interface ItemInfoRepository {

    ItemInfoEntity save(ItemInfoEntity entity);

    Optional<ItemInfoEntity> findByItemInfoNumber(Long itemInfoNumber);

    Optional<ItemInfoEntity> findByItemNumberFJItem(Long itemNumber);

}
