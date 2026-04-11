package br.com.vendemais.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    void corsConfigurationSourceAllowsExpectedMethodsWithoutCredentials() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();

        CorsConfiguration configuration = source.getCorsConfiguration(new MockHttpServletRequest());

        assertNotNull(configuration);
        assertEquals(java.util.List.of("POST", "GET", "PUT","PATCH","DELETE", "OPTIONS"), configuration.getAllowedMethods());
        assertFalse(Boolean.TRUE.equals(configuration.getAllowCredentials()));
    }
}
