package br.com.vendemais.domain.entity;

import br.com.vendemais.domain.enums.Role;
import br.com.vendemais.security.UserSecurity;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    @Test
    void constructorAssignsDefaultUserRole() {
        User user = new User("Nikola Tesla", "tesla@gmail.com", "encoded");

        assertTrue(user.getRoles().contains(Role.USER));
    }

    @Test
    void addRoleAddsAdminRole() {
        User user = new User("Nikola Tesla", "tesla@gmail.com", "encoded");

        user.addRole(Role.ADMIN);

        assertTrue(user.getRoles().contains(Role.ADMIN));
        assertEquals(2, user.getRoles().size());
    }

    @Test
    void usuarioSecurityMapsUserRolesToAuthorities() {
        User user = new User("Albert Einstein", "einstein@gmail.com", "encoded");
        user.addRole(Role.ADMIN);
        ReflectionTestUtils.setField(user, "id", 7L);

        UserSecurity userSecurity = new UserSecurity(user);

        assertEquals(7L, userSecurity.getId());
        assertEquals("einstein@gmail.com", userSecurity.getUsername());
        assertTrue(userSecurity.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(userSecurity.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }
}
