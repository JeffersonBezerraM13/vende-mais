package br.com.vendemais.security;

import br.com.vendemais.domain.entity.User;
import br.com.vendemais.domain.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adapts the CRM user entity to Spring Security's {@link UserDetails} contract
 * while preserving the internal user id for downstream authorization logic.
 */
public class UserSecurity implements UserDetails {

    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private User user;

    public UserSecurity(Long id, String email, String password, Set<Role> roles, User user) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = roles.stream()
                .map(x -> new SimpleGrantedAuthority(x.getDescription()))
                .collect(Collectors.toSet());
        this.user = user;
    }

    public UserSecurity(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getDescription()))
                .collect(Collectors.toSet());
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
