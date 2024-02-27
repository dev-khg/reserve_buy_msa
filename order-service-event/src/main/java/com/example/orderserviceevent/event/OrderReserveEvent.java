package com.example.orderserviceevent.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderReserveEvent {
    private String orderId;
    private Long userNumber;
    private Long itemNumber;
    private Integer price;
    private Integer count;
}
