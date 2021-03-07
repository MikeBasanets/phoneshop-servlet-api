package com.es.phoneshop.model.security;

import java.time.Duration;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDosProtectionService implements DosProtectionService {
    private static final Duration DURATION = Duration.ofSeconds(60);
    private static final long MAX_REQUESTS_DURING_DURATION = 20;

    private Map<String, List<LocalTime>> requestTimeLists = new ConcurrentHashMap();

    private static class SingletonHolder {
        private static final DefaultDosProtectionService INSTANCE = new DefaultDosProtectionService();
    }

    private DefaultDosProtectionService() {
    }

    public static DefaultDosProtectionService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public boolean isAllowed(String ip) {
        List<LocalTime> times = requestTimeLists.get(ip);
        if(times == null) {
            times = new LinkedList<>();
            requestTimeLists.put(ip, times);
        }
        synchronized (times) {
            times.add(LocalTime.now());
            if(times.size() > MAX_REQUESTS_DURING_DURATION  + 1) {
                times.remove(0);
            }
            if(times.size() == MAX_REQUESTS_DURING_DURATION + 1 && times.get(0).plus(DURATION).isAfter(LocalTime.now())) {
                return false;
            }
        }
        return true;
    }
}
