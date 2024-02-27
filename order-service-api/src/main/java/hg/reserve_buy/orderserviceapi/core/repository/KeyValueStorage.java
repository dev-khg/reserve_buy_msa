package hg.reserve_buy.orderserviceapi.core.repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface KeyValueStorage<K, V> {

    Optional<V> getValue(K key);

    void putValue(K key, V value, int timeout, TimeUnit timeUnit);
}
