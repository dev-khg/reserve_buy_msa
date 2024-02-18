package hg.reserve_buy.stockserviceapi.core.service;

public interface StockService {

    Integer getStockByItemNumber(Long itemNumber);

    void increaseStockByItemNumber(Long itemNumber, Integer count);

    void decreaseStockByItemNumber(Long itemNumber, Integer count);
}
