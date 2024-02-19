package hg.reserve_buy.stockserviceapi.core.service;

import hg.reserve_buy.commonredis.lock.DistributionLock;
import hg.reserve_buy.commonservicedata.exception.BadRequestException;
import hg.reserve_buy.stockserviceapi.core.entity.StockEntity;
import hg.reserve_buy.stockserviceapi.core.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;

    //TODO : 동시성 및 Lock 고려
    @Override
    public Integer getStockByItemNumber(Long itemNumber) {
        StockEntity stockEntity = getStockEntity(itemNumber);
        return stockEntity.getTotal();
    }

    @Override
    @DistributionLock(prefix = "ITEM:STOCK:", key = "#itemNumber")
    public void increaseStockByItemNumber(Long itemNumber, Integer count) {
        StockEntity stockEntity = getStockEntity(itemNumber);
        stockEntity.increaseStock(count);
    }

    @Override
    @DistributionLock(prefix = "ITEM:STOCK:", key = "#itemNumber")
    public void decreaseStockByItemNumber(Long itemNumber, Integer count) {
        StockEntity stockEntity = getStockEntity(itemNumber);
        stockEntity.decreaseStock(count);
    }

    private StockEntity getStockEntity(Long itemNumber) {
        return stockRepository.findByItemNumber(itemNumber).orElseThrow(
                () -> new BadRequestException("Not exists stock info")
        );
    }
}
