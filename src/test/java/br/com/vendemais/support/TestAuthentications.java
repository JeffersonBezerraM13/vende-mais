package br.com.vendemais.support;

import br.com.vendemais.domain.enums.Role;
import br.com.vendemais.security.UsuarioSecurity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Arrays;
import java.util.LinkedHashSet;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

public final class TestAuthentications {

    private TestAuthentications() {
    }

    public static RequestPostProcessor admin() {
        return authentication(authenticatedUser(1L, "admin@vendemais.com", Role.ADMIN, Role.USER));
    }

    public static RequestPostProcessor user() {
        return authentication(authenticatedUser(2L, "user@vendemais.com", Role.USER));
    }

    public static RequestPostProcessor principal(Long id, String email, Role... roles) {
        return authentication(authenticatedUser(id, email, roles));
    }

    public static Authentication authenticatedUser(Long id, String email, Role... roles) {
        UsuarioSecurity principal = new UsuarioSecurity(
                id,
                email,
                "encoded-password",
                new LinkedHashSet<>(Arrays.asList(roles))
        );

        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }
}
