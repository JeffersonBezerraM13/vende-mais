package br.com.vendemais.support;

import br.com.vendemais.domain.entity.User;
import br.com.vendemais.domain.enums.Role;
import br.com.vendemais.security.UserSecurity;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

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
        User userEntity = BeanUtils.instantiateClass(User.class);

        Set<Role> rolesSet = new LinkedHashSet<>(Arrays.asList(roles));

        ReflectionTestUtils.setField(userEntity, "id", id);
        ReflectionTestUtils.setField(userEntity, "email", email);
        ReflectionTestUtils.setField(userEntity, "roles", rolesSet);

        UserSecurity principal = new UserSecurity(
                id,
                email,
                "encoded-password",
                rolesSet,
                userEntity // Agora com a entidade recheada!
        );

        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }
}