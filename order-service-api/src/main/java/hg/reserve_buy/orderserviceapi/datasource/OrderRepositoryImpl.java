package hg.reserve_buy.orderserviceapi.datasource;

import hg.reserve_buy.orderserviceapi.core.entity.OrderEntity;
import hg.reserve_buy.orderserviceapi.core.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;

    @Override
    public OrderEntity save(OrderEntity entity) {
        return orderJpaRepository.save(entity);
    }

    @Override
    public Optional<OrderEntity> findById(String orderId) {
        return orderJpaRepository.findById(orderId);
    }
}
