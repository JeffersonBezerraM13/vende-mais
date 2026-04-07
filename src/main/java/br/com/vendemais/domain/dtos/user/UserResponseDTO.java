package br.com.vendemais.domain.dtos.user;

import br.com.vendemais.domain.entity.User;
import br.com.vendemais.domain.enums.Role;
import java.util.Set;

public record UserResponseDTO(
        Long id,
        String email,
        Set<Role> roles
) {
    public static UserResponseDTO daEntidade(User entidade) {
        return new UserResponseDTO(
                entidade.getId(),
                entidade.getEmail(),
                entidade.getRoles()
        );
    }
}