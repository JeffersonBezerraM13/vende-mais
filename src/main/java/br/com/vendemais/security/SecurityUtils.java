package br.com.vendemais.security;

import br.com.vendemais.domain.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static User getLoggedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserSecurity userSecurity) {
            return userSecurity.getUser();
        }

        throw new ClassCastException("O principal no SecurityContext não é um UserSecurity.");
    }
}