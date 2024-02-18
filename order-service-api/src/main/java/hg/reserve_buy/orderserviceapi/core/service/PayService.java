package hg.reserve_buy.orderserviceapi.core.service;

import org.springframework.stereotype.Service;

@Service
public class PayService {

    // TODO: pay-service 엔드포인트 연결 필요
    public boolean isPayable(Long userNumber, Integer price) {
        return true;
    }
}
