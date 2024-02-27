package hg.reserve_buy.stockserviceapi.presentation;

import hg.reserve_buy.commonservicedata.response.ApiResponse;
import hg.reserve_buy.stockserviceapi.core.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static hg.reserve_buy.commonservicedata.response.ApiResponse.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stock")
public class StockController {
    private final StockService stockService;

    @GetMapping("/{itemNumber}")
    public ApiResponse<Integer> getStock(@PathVariable Long itemNumber) {
        return success(stockService.getStockByItemNumber(itemNumber));
    }

    @GetMapping("/{itemNumber}/cache")
    public Integer getStockCache(@PathVariable Long itemNumber) {
        return stockService.getStockByItemNumberWithLock(itemNumber);
    }
}
