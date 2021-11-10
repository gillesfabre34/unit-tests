package com.airbus.retex.service;

import com.airbus.retex.configuration.CustomLocalResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

public class LocalResolverTest {

    @InjectMocks
    private CustomLocalResolver customLocalResolver = new CustomLocalResolver();
    @Mock
    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void noLocale() {
        assertResolve(null, Locale.ENGLISH);
    }

    @Test
    public void locale_en_us() {
        assertResolve(Locale.US, Locale.ENGLISH);
    }

    @Test
    public void locale_en_uk() {
        assertResolve(Locale.UK, Locale.ENGLISH);
    }

    @Test
    public void locale_fr_fr() {
        assertResolve(Locale.FRENCH, Locale.FRENCH);
    }

    @Test
    public void locale_fr_ca() {
        assertResolve(Locale.CANADA_FRENCH, Locale.FRENCH);
    }

    @Test
    public void locale_it() {
        assertResolve(Locale.ITALIAN, Locale.ENGLISH);
    }

    @Test
    public void locale_notManagedShouldReturnDefault() {
        assertResolve(Locale.ITALIAN, Locale.ENGLISH);
    }

    private void assertResolve(Locale localeRequest, Locale expectedLocale) {
        if(localeRequest != null) {
            when(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE)).thenReturn(localeRequest.getLanguage()+(localeRequest.getCountry().isBlank() ? "" : "_")+localeRequest.getCountry());

            final Iterator<Locale> localeIt = List.of(localeRequest).iterator();
            when(request.getLocales()).thenReturn(new Enumeration<Locale>() {
                @Override
                public boolean hasMoreElements() {
                    return localeIt.hasNext();
                }

                @Override
                public Locale nextElement() {
                    return localeIt.next();
                }
            });
        }

        assertThat(customLocalResolver.resolveLocale(request), equalTo(expectedLocale));
    }
}
