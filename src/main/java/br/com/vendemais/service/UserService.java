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


/**
 * Handles CRM user administration, including credential hashing and uniqueness
 * validation.
 */
@Service
public class UserService {

    private final UserRepository UserRepository;

    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository UserRepository, BCryptPasswordEncoder encoder) {
        this.UserRepository = UserRepository;
        this.encoder = encoder;
    };

    /**
     * Retrieves CRM users in pages so administrators can inspect the access base
     * efficiently.
     *
     * @param pageable pagination and sorting instructions for the query
     * @return a page containing user projections mapped to response DTOs
     */
    public Page<UserResponseDTO> findAll(Pageable pageable) {
        Page<User> paginaDeUsers = UserRepository.findAll(pageable);

        return paginaDeUsers.map(UserResponseDTO::daEntidade);
    }

    /**
     * Loads a user account by identifier so profile and authorization data can be
     * displayed.
     *
     * @param id identifier of the user to retrieve
     * @return the requested user mapped to the API response DTO
     * @throws ObjectNotFoundException if the user does not exist
     */
    public UserResponseDTO findById(Long id) {
        User User = findUserById(id);
        return UserResponseDTO.daEntidade(User);
    }

    /**
     * Creates a CRM user account and hashes the provided password before
     * persistence.
     *
     * @param dto payload describing the account to provision
     * @return the persisted user mapped to the API response DTO
     * @throws DataIntegrityViolationException if another account already uses the same email
     */
    public UserResponseDTO create(UserRequestDTO dto) {
        if(existsByEmail(dto.email())){
            throw new DataIntegrityViolationException("User já está cadastrado no sistema");
        }

        User User = new User(
                dto.name(),
                dto.email(),
                encoder.encode(dto.password())
        );

        return UserResponseDTO.daEntidade(UserRepository.save(User));
    }

    /**
     * Updates a user account and re-hashes the supplied password before saving
     * the new credentials.
     *
     * @param id identifier of the user being updated
     * @param dto payload containing the revised account data
     * @return the persisted user mapped to the API response DTO
     * @throws ObjectNotFoundException if the user does not exist
     */
    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        User user = findUserById(id);

        user.setEmail(dto.email());
        user.setPassword(encoder.encode(dto.password()));

        return UserResponseDTO.daEntidade(UserRepository.save(user));
    }

    /**
     * Deletes a CRM user account when access must be revoked permanently.
     *
     * @param id identifier of the user to delete
     * @throws ObjectNotFoundException if the user does not exist
     */
    public void delete(Long id){
        User user = findUserById(id);
        UserRepository.delete(user);
    }

    private User findUserById(Long id) {
        return UserRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("User não encontrado. ID:" +id));
    }

    private boolean existsByEmail(String email) {
        return UserRepository.existsByEmail(email);
    }
}
