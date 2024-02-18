package hg.reserve_buy.itemserviceapi.core.repository;

import hg.reserve_buy.itemserviceapi.core.entity.ItemEntity;

import java.util.Optional;

public interface ItemRepository {

    ItemEntity save(ItemEntity entity);

    Optional<ItemEntity> findByItemNumber(Long itemNumber);

}
