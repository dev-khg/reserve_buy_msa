package hg.reserve_buy.orderserviceapi.core.repository;

import hg.reserve_buy.orderserviceapi.core.entity.OrderEntity;

import java.util.Optional;

public interface OrderRepository {
    OrderEntity save(OrderEntity entity);

    Optional<OrderEntity> findById(String orderId);
}
