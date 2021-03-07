package com.es.phoneshop.web;

import com.es.phoneshop.model.security.DosProtectionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class DosFilterTest {
    private static final String REMOTE_ADDR = "";
    private static final int TOO_MANY_REQUESTS_CODE = 429;

    @Mock
    private ServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private DosProtectionService dosProtectionService;

    private DosFilter filter = new DosFilter();

    @Before
    public void setup() throws ServletException {
        filter.init(dosProtectionService);
    }

    @Test
    public void shouldBeAllowed() throws IOException, ServletException {
        when(request.getRemoteAddr()).thenReturn(REMOTE_ADDR);
        when(dosProtectionService.isAllowed(anyString())).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(any(), any());
    }

    @Test
    public void shouldNotBeAllowed() throws IOException, ServletException {
        when(request.getRemoteAddr()).thenReturn(REMOTE_ADDR);
        when(dosProtectionService.isAllowed(anyString())).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        verify(response).setStatus(TOO_MANY_REQUESTS_CODE);
    }
}
