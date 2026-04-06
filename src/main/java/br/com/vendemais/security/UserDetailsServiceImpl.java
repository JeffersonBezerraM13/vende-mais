package br.com.vendemais.security;

import br.com.vendemais.domain.entity.User;
import br.com.vendemais.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Lembre-se: Para o orElseThrow funcionar, o findByEmail no UsuarioRepository
        // PRECISA retornar um Optional<Usuario>
        User user = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));

        // Retornamos o nosso Wrapper que implementa UserDetails
        return new UsuarioSecurity(user);
    }
}