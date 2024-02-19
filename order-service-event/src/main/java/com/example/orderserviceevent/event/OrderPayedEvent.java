package com.example.orderserviceevent.event;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class OrderPayedEvent {
    private String orderId;
    private Long itemNumber;
    private Integer count;
}
