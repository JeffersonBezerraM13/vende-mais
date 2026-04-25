package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.user.UserFilterDTO;
import br.com.vendemais.domain.dtos.user.UserRequestDTO;
import br.com.vendemais.domain.dtos.user.UserResponseDTO;
import br.com.vendemais.domain.entity.User;
import br.com.vendemais.repository.UserRepository;
import br.com.vendemais.repository.specification.UserSpecification;
import br.com.vendemais.security.SecurityUtils;
import br.com.vendemais.service.exceptions.BusinessRuleException;
import br.com.vendemais.service.exceptions.DuplicateResourceException;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles CRM user administration, including credential hashing, uniqueness
 * validation, and account removal rules.
 */
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    /**
     * Retrieves CRM users in pages, applying optional filters so administrators can
     * search the access base directly by the backend.
     *
     * @param filter optional filtering criteria, such as search term and role
     * @param pageable pagination and sorting instructions for the query
     * @return a page containing filtered user projections mapped to response DTOs
     */
    public Page<UserResponseDTO> findAll(UserFilterDTO filter, Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(
                UserSpecification.withFilters(filter),
                pageable
        );

        return usersPage.map(UserResponseDTO::daEntidade);
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
        User user = findUserById(id);
        return UserResponseDTO.daEntidade(user);
    }

    /**
     * Creates a CRM user account after validating email uniqueness and hashing
     * the provided password before persistence.
     *
     * @param dto payload describing the account to provision
     * @return the persisted user mapped to the API response DTO
     * @throws DuplicateResourceException if another account already uses the same email
     */
    @Transactional
    public UserResponseDTO create(UserRequestDTO dto) {
        ensureEmailAvailableForCreation(dto.email());

        User user = buildUser(dto);

        return UserResponseDTO.daEntidade(userRepository.save(user));
    }

    /**
     * Updates a user account after validating email uniqueness and re-hashing the
     * supplied password before saving the new credentials.
     *
     * @param id identifier of the user being updated
     * @param dto payload containing the revised account data
     * @return the persisted user mapped to the API response DTO
     * @throws ObjectNotFoundException if the user does not exist
     * @throws DuplicateResourceException if the new email belongs to another account
     */
    @Transactional
    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        User user = findUserById(id);

        ensureEmailAvailableForUpdate(id, dto.email());
        updateUserData(user, dto);

        return UserResponseDTO.daEntidade(userRepository.save(user));
    }

    /**
     * Deletes a CRM user account when access must be revoked permanently, while
     * preventing the authenticated administrator from deleting their own account.
     *
     * @param id identifier of the user to delete
     * @throws ObjectNotFoundException if the user does not exist
     * @throws BusinessRuleException if the authenticated user tries to delete their own account
     */
    @Transactional
    public void delete(Long id) {
        User user = findUserById(id);

        ensureLoggedUserIsNotDeletingOwnAccount(user);

        userRepository.delete(user);
    }

    private User buildUser(UserRequestDTO dto) {
        return new User(
                dto.name(),
                dto.email(),
                encodePassword(dto.password())
        );
    }

    private void updateUserData(User user, UserRequestDTO dto) {
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(encodePassword(dto.password()));
    }

    private String encodePassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    private void ensureEmailAvailableForCreation(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Já existe um usuário cadastrado com este e-mail.");
        }
    }

    private void ensureEmailAvailableForUpdate(Long userId, String email) {
        userRepository.findByEmail(email)
                .filter(existingUser -> !existingUser.getId().equals(userId))
                .ifPresent(existingUser -> {
                    throw new DuplicateResourceException("Já existe outro usuário cadastrado com este e-mail.");
                });
    }

    private void ensureLoggedUserIsNotDeletingOwnAccount(User targetUser) {
        User loggedUser = SecurityUtils.getLoggedUser();

        if (loggedUser.getId().equals(targetUser.getId())) {
            throw new BusinessRuleException("Você não pode excluir a própria conta.");
        }
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado. ID: " + id));
    }
}