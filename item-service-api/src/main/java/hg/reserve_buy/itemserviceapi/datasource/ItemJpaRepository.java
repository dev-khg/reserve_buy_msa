package hg.reserve_buy.itemserviceapi.datasource;

import hg.reserve_buy.itemserviceapi.core.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ItemJpaRepository extends JpaRepository<ItemEntity, Long> {

}
