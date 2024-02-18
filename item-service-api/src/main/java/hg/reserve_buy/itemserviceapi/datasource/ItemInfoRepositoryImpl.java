package hg.reserve_buy.itemserviceapi.datasource;

import hg.reserve_buy.itemserviceapi.core.entity.ItemInfoEntity;
import hg.reserve_buy.itemserviceapi.core.repository.ItemInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ItemInfoRepositoryImpl implements ItemInfoRepository {
    private final ItemInfoJpaRepository itemInfoJpaRepository;

    @Override
    public ItemInfoEntity save(ItemInfoEntity entity) {
        return itemInfoJpaRepository.save(entity);
    }

    @Override
    public Optional<ItemInfoEntity> findByItemInfoNumber(Long itemInfoNumber) {
        return itemInfoJpaRepository.findById(itemInfoNumber);
    }

    @Override
    public Optional<ItemInfoEntity> findByItemNumberFJItem(Long itemNumber) {
        return itemInfoJpaRepository.findByItemInfoFJItem(itemNumber);
    }
}
