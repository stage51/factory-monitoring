package centrikt.factorymonitoring.authserver.services.impl;

import centrikt.factorymonitoring.authserver.dtos.requests.UserRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.UserResponse;
import centrikt.factorymonitoring.authserver.enums.Role;
import centrikt.factorymonitoring.authserver.exceptions.EntityNotFoundException;
import centrikt.factorymonitoring.authserver.exceptions.IllegalArgumentException;
import centrikt.factorymonitoring.authserver.mappers.UserMapper;
import centrikt.factorymonitoring.authserver.models.User;
import centrikt.factorymonitoring.authserver.repos.UserRepository;
import centrikt.factorymonitoring.authserver.services.UserService;
import centrikt.factorymonitoring.authserver.utils.filter.FilterUtil;
import centrikt.factorymonitoring.authserver.utils.entityvalidator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private FilterUtil<User> filterUtil;
    private EntityValidator entityValidator;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, FilterUtil<User> filterUtil , EntityValidator entityValidator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.filterUtil = filterUtil;
        this.entityValidator = entityValidator;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @Autowired
    public void setFilterUtil(FilterUtil<User> filterUtil) {
        this.filterUtil = filterUtil;
    }
    @Autowired
    public void setEntityValidator(EntityValidator entityValidator) {
        this.entityValidator = entityValidator;
    }

    @Override
    public UserResponse create(UserRequest dto) {
        try {
            entityValidator.validate(dto);
            User user = UserMapper.toEntity(dto);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setActive(true);
            user.setRole(Role.ROLE_USER);
            user.setCreatedAt(ZonedDateTime.now());
            user.setUpdatedAt(ZonedDateTime.now());
            return UserMapper.toResponse(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("User with email " + dto.getEmail() + " already exists");
        }
    }

    @Override
    public UserResponse get(Long id) {
        return UserMapper.toResponse(userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id)));
    }

    @Override
    public UserResponse update(Long id, UserRequest dto) {
        entityValidator.validate(dto);
        User existingUser = UserMapper.toEntity(dto);
        if (userRepository.existsById(id)){
            existingUser.setId(id);
            existingUser.setUpdatedAt(ZonedDateTime.now());
            existingUser.setPassword(passwordEncoder.encode(existingUser.getPassword()));
        } else throw new EntityNotFoundException("User not found with id: " + id);
        return UserMapper.toResponse(userRepository.save(existingUser));
    }

    @Override
    public void delete(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else throw new EntityNotFoundException("User not found with id: " + id);
    }

    @Override
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public Page<UserResponse> getPage(int size, int number, String sortBy, String sortDirection,
                                          Map<String, String> filters, Map<String, String> dateRanges) {

        Sort.Direction direction = sortDirection != null ? Sort.Direction.fromString(sortDirection) : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "firstName");
        Pageable pageable = PageRequest.of(number, size, sort);
        Specification<User> specification = filterUtil.buildSpecification(filters, dateRanges);
        return userRepository.findAll(specification, pageable).map(UserMapper::toResponse);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws EntityNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + username));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }

    @Override
    public UserResponse getByEmail(String email) {
        return UserMapper.toResponse(userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email)));
    }
}
