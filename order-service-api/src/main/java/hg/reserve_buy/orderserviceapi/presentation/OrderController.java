package hg.reserve_buy.orderserviceapi.presentation;

import hg.reserve_buy.commonservicedata.response.ApiResponse;
import hg.reserve_buy.orderserviceapi.core.service.OrderService;
import hg.reserve_buy.orderserviceapi.presentation.request.OrderReserveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import static hg.reserve_buy.commonservicedata.response.ApiResponse.*;
import static org.springframework.http.HttpHeaders.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ApiResponse<String> reserveOrder(
            @RequestHeader(AUTHORIZATION) Long userNumber, @RequestBody OrderReserveRequest request) {
        return success(orderService.reserveOrder(userNumber, request.getItemNumber(), request.getCount()));
    }

    @PostMapping("/{orderId}")
    public ApiResponse<String> order(@RequestHeader(AUTHORIZATION) Long userNumber, @PathVariable String orderId) {
        return success(orderService.order(userNumber, orderId));
    }
}
