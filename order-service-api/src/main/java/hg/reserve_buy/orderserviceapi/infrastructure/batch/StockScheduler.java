package hg.reserve_buy.orderserviceapi.infrastructure.batch;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Component
public class StockScheduler {
    private final Map<String, ScheduledFuture<?>> schedulerMap = new HashMap<>();

    public void reserveSchedule(String key, Object obj) {
        cancelSchedule(key);
        schedulerMap.put(key, createSchedule(obj));
    }

    private void cancelSchedule(String key) {
        ScheduledFuture<?> scheduledFuture = this.schedulerMap.get(key);
        if(scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(true);
        }
    }

    private ScheduledFuture<?> createSchedule(Object obj) {
        return null;
    }
}
