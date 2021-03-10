package com.es.phoneshop.model.security;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class DefaultDosProtectionServiceTest {
    private static final String IP_THAT_MUST_BE_ALLOWED = "192.16.16.1";
    private static final String IP_THAT_MUST_NOT_BE_ALLOWED = "192.16.16.2";
    private static final int REQUESTS_QUANTITY = 40;
    
    private DefaultDosProtectionService service;

    @Before
    public void setup() {
        service = DefaultDosProtectionService.getInstance();
    }

    @Test
    public void shouldTrySingleRequest() {
        boolean isAllowed = service.isAllowed(IP_THAT_MUST_BE_ALLOWED);

        assert(isAllowed);
    }

    @Test
    public void shouldTryTooManyRequests() {
        for(int i = 0; i < REQUESTS_QUANTITY - 1; i++) {
            service.isAllowed(IP_THAT_MUST_NOT_BE_ALLOWED);
        }

        boolean isAllowed = service.isAllowed(IP_THAT_MUST_NOT_BE_ALLOWED);

        assertFalse(isAllowed);
    }
}
