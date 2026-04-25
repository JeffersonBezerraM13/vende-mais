package br.com.vendemais.config;

import br.com.vendemais.security.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig(
            mock(Environment.class),
            mock(JWTUtil.class),
            mock(UserDetailsService.class),
            new ObjectMapper()
    );

    @Test
    void corsConfigurationSourceAllowsExpectedMethodsWithoutCredentials() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();

        CorsConfiguration configuration = source.getCorsConfiguration(new MockHttpServletRequest());

        assertNotNull(configuration);

        assertEquals(
                List.of("http://localhost:5173"),
                configuration.getAllowedOrigins()
        );

        assertEquals(
                List.of("POST", "GET", "PUT", "PATCH", "DELETE", "OPTIONS"),
                configuration.getAllowedMethods()
        );

        assertEquals(
                List.of("Authorization", "Content-Type", "Accept"),
                configuration.getAllowedHeaders()
        );

        assertEquals(
                List.of("Authorization"),
                configuration.getExposedHeaders()
        );

        assertFalse(Boolean.TRUE.equals(configuration.getAllowCredentials()));
    }
}