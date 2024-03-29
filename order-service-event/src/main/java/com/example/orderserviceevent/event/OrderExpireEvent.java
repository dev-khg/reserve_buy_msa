package com.example.orderserviceevent.event;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderExpireEvent {
    private String orderId;
    private Long itemNumber;
    private Integer count;
}
