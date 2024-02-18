package hg.reserve_buy.itemserviceapi.datasource;

import hg.reserve_buy.itemserviceapi.core.entity.ItemEntity;
import hg.reserve_buy.itemserviceapi.core.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final ItemJpaRepository itemJpaRepository;

    @Override
    public ItemEntity save(ItemEntity entity) {
        return itemJpaRepository.save(entity);
    }

    @Override
    public Optional<ItemEntity> findByItemNumber(Long itemNumber) {
        return itemJpaRepository.findById(itemNumber);
    }

    @Override
    public List<ItemEntity> findAll() {
        return itemJpaRepository.findAll();
    }
}
