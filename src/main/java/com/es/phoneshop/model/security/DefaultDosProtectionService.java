package com.es.phoneshop.model.security;

import java.time.Duration;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDosProtectionService implements DosProtectionService {
    private static final Long CLEAR_TASK_CYCLE_DURATION_MILLISECONDS = 60000L;
    private static final Long CLEAR_TASK_START_DELAY_MILLISECONDS = 0L;
    private static final long MAX_REQUESTS_DURING_DURATION = 20;

    private Map<String, Integer> requestQuantities = new ConcurrentHashMap();

    private static class SingletonHolder {
        private static final DefaultDosProtectionService INSTANCE = new DefaultDosProtectionService();
    }

    private DefaultDosProtectionService() {
        TimerTask cycleTask = new TimerTask() {
            @Override
            public void run() {
                requestQuantities.clear();
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(cycleTask, CLEAR_TASK_START_DELAY_MILLISECONDS, CLEAR_TASK_CYCLE_DURATION_MILLISECONDS);
    }

    public static DefaultDosProtectionService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public boolean isAllowed(String ip) {
        Integer quantity = requestQuantities.get(ip);
        if(quantity == null) {
            quantity = 0;
        }
        quantity += 1;
        requestQuantities.put(ip, quantity);
        if(quantity > MAX_REQUESTS_DURING_DURATION) {
            return false;
        }
        return true;
    }
}
