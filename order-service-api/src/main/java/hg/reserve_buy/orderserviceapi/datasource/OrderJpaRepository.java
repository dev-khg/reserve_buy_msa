package hg.reserve_buy.orderserviceapi.datasource;

import hg.reserve_buy.orderserviceapi.core.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, String> {
}
