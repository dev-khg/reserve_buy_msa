package hg.reserve_buy.orderserviceapi.core.repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface KeyValueStorage {

    Optional<Integer> getValue(String key);

    void putValue(String key, Integer value, int timeout, TimeUnit timeUnit);
}
