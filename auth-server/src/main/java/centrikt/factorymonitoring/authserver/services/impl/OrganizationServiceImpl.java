package centrikt.factorymonitoring.authserver.services.impl;

import centrikt.factorymonitoring.authserver.dtos.requests.OrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.users.AuthOrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.OrganizationResponse;
import centrikt.factorymonitoring.authserver.exceptions.EntityNotFoundException;
import centrikt.factorymonitoring.authserver.mappers.OrganizationMapper;
import centrikt.factorymonitoring.authserver.models.Organization;
import centrikt.factorymonitoring.authserver.models.User;
import centrikt.factorymonitoring.authserver.repos.OrganizationRepository;
import centrikt.factorymonitoring.authserver.repos.UserRepository;
import centrikt.factorymonitoring.authserver.services.OrganizationService;
import centrikt.factorymonitoring.authserver.utils.filter.FilterUtil;
import centrikt.factorymonitoring.authserver.utils.entityvalidator.EntityValidator;
import centrikt.factorymonitoring.authserver.utils.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    private UserRepository userRepository;
    private OrganizationRepository organizationRepository;
    private FilterUtil<Organization> filterUtil;
    private EntityValidator entityValidator;
    private JwtTokenUtil jwtTokenUtil;

    public OrganizationServiceImpl(UserRepository userRepository, OrganizationRepository organizationRepository,
                                   FilterUtil<Organization> filterUtil , EntityValidator entityValidator, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.filterUtil = filterUtil;
        this.entityValidator = entityValidator;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Autowired
    public void setOrganizationRepository(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Autowired
    public void setFilterUtil(FilterUtil<Organization> filterUtil) {
        this.filterUtil = filterUtil;
    }
    @Autowired
    public void setEntityValidator(EntityValidator entityValidator) {
        this.entityValidator = entityValidator;
    }
    @Autowired
    public void setJwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public OrganizationResponse create(OrganizationRequest dto) {
        entityValidator.validate(dto);
        Organization organization = OrganizationMapper.toEntity(dto, userRepository.findById(dto.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found with id " + dto.getUserId())));
        return OrganizationMapper.toResponse(organizationRepository.save(organization));
    }

    @Override
    public OrganizationResponse get(Long id) {
        return OrganizationMapper.toResponse(organizationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Organization not found with id " + id)));
    }

    @Override
    public OrganizationResponse update(Long id, OrganizationRequest dto) {
        entityValidator.validate(dto);
        Organization existingOrganization = organizationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Organization not found with id " + id));
        existingOrganization = OrganizationMapper.toEntityUpdate(dto, userRepository.findById(dto.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found with id " + dto.getUserId())), existingOrganization);
        return OrganizationMapper.toResponse(organizationRepository.save(existingOrganization));
    }

    @Override
    public void delete(Long id) {
        if (organizationRepository.existsById(id)) {
            organizationRepository.deleteById(id);
        } else throw new EntityNotFoundException("Organization not found with id " + id);
    }

    @Override
    public List<OrganizationResponse> getAll() {
        return organizationRepository.findAll().stream().map(OrganizationMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public Page<OrganizationResponse> getPage(int size, int number, String sortBy, String sortDirection, Map<String, String> filters, Map<String, String> dateRanges) {
        Sort.Direction direction = sortDirection != null ? Sort.Direction.fromString(sortDirection) : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "shortName");
        Pageable pageable = PageRequest.of(number, size, sort);
        Specification<Organization> specification = filterUtil.buildSpecification(filters, dateRanges);
        return organizationRepository.findAll(specification, pageable).map(OrganizationMapper::toResponse);
    }
    @Override
    public OrganizationResponse createOrganization(String accessToken, AuthOrganizationRequest organizationRequest) {
        User user = userRepository.findByEmail(jwtTokenUtil.extractUsername(accessToken))
                .orElseThrow(() -> new EntityNotFoundException("User with email "
                        + jwtTokenUtil.extractUsername(accessToken) + " not found"));

        Organization organization = OrganizationMapper.toEntityFromCreateRequest(organizationRequest, user);
        user.setOrganization(organization);
        organizationRepository.save(organization);
        userRepository.save(user);

        return OrganizationMapper.toResponse(organization);
    }

    @Override
    public OrganizationResponse updateOrganization(String accessToken, AuthOrganizationRequest organizationRequest) {
        User user = userRepository.findByEmail(jwtTokenUtil.extractUsername(accessToken))
                .orElseThrow(() -> new EntityNotFoundException("User with email "
                        + jwtTokenUtil.extractUsername(accessToken) + " not found"));

        Organization existingOrganization = organizationRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Organization for user " + user.getEmail() + " not found"));

        Organization organization = OrganizationMapper.toEntityFromUpdateRequest(existingOrganization, organizationRequest);
        organizationRepository.save(organization);

        return OrganizationMapper.toResponse(organization);
    }

    @Override
    @Transactional
    public void deleteOrganization(String accessToken) {
        User user = userRepository.findByEmail(jwtTokenUtil.extractUsername(accessToken))
                .orElseThrow(() -> new EntityNotFoundException("User with email "
                        + jwtTokenUtil.extractUsername(accessToken) + " not found"));
        user.setOrganization(null);
        userRepository.save(user);
        organizationRepository.deleteByUser(user);
    }
}
