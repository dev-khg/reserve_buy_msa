package hg.reserve_buy.orderserviceapi.core.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PayService {
    private final Random r = new Random();

    /**
     * 결재 실패 확률은 20%로 Mocking 처리
     * @param userNumber 결제할 유저의 PK Number
     * @param price : 총 결제해야할 금액
     * @return
     */
    public boolean isPayable(Long userNumber, Integer price) {
        int mock = r.nextInt(1, 11) % 10;
        return mock > 2;
    }
}
