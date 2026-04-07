package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.user.UserRequestDTO;
import br.com.vendemais.domain.dtos.user.UserResponseDTO;
import br.com.vendemais.domain.entity.User;
import br.com.vendemais.repository.UserRepository;
import br.com.vendemais.service.exceptions.DataIntegrityViolationException;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final UserRepository UserRepository;

    private final BCryptPasswordEncoder encoder;

    private UserService(UserRepository UserRepository, BCryptPasswordEncoder encoder) {
        this.UserRepository = UserRepository;
        this.encoder = encoder;
    };

    public Page<UserResponseDTO> findAll(Pageable pageable) {
        Page<User> paginaDeUsers = UserRepository.findAll(pageable);

        return paginaDeUsers.map(UserResponseDTO::daEntidade);
    }

    public UserResponseDTO findById(Long id) {
        User User = findUserById(id);
        return UserResponseDTO.daEntidade(User);
    }

    public UserResponseDTO create(UserRequestDTO userRequestDTO) {
        if(findByEmail(userRequestDTO.email())){
            throw new DataIntegrityViolationException("User já está cadastrado no sistema");
        }

        User User = new User(
                userRequestDTO.email(),
                encoder.encode(userRequestDTO.password())
        );

        return UserResponseDTO.daEntidade(UserRepository.save(User));
    }

    public UserResponseDTO update(Long id, UserRequestDTO userRequestDTO) {
        User user = findUserById(id);

        user.setEmail(userRequestDTO.email());
        user.setPassword(encoder.encode(userRequestDTO.password()));

        return UserResponseDTO.daEntidade(UserRepository.save(user));
    }

    public void delete(Long id){
        User user = findUserById(id);
        UserRepository.delete(user);
    }

    private User findUserById(Long id) {
        return UserRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("User não encontrado. ID:" +id));
    }

    private boolean findByEmail(String email) {
        return UserRepository.existsByEmail(email);
    }
}
