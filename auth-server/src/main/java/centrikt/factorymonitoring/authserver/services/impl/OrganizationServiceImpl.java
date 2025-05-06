package centrikt.factorymonitoring.authserver.services.impl;

import centrikt.factorymonitoring.authserver.dtos.messages.EmailMessage;
import centrikt.factorymonitoring.authserver.dtos.messages.ReportMessage;
import centrikt.factorymonitoring.authserver.dtos.requests.OrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.users.AuthOrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.OrganizationResponse;
import centrikt.factorymonitoring.authserver.exceptions.EntityNotFoundException;
import centrikt.factorymonitoring.authserver.exceptions.MessageSendingException;
import centrikt.factorymonitoring.authserver.mappers.OrganizationMapper;
import centrikt.factorymonitoring.authserver.models.Organization;
import centrikt.factorymonitoring.authserver.models.User;
import centrikt.factorymonitoring.authserver.models.enums.ReportNotification;
import centrikt.factorymonitoring.authserver.repos.OrganizationRepository;
import centrikt.factorymonitoring.authserver.repos.UserRepository;
import centrikt.factorymonitoring.authserver.services.OrganizationService;
import centrikt.factorymonitoring.authserver.utils.filter.FilterUtil;
import centrikt.factorymonitoring.authserver.utils.entityvalidator.EntityValidator;
import centrikt.factorymonitoring.authserver.utils.jwt.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
@Slf4j
public class OrganizationServiceImpl implements OrganizationService {
    private UserRepository userRepository;
    private OrganizationRepository organizationRepository;
    private FilterUtil<Organization> filterUtil;
    private EntityValidator entityValidator;
    private JwtTokenUtil jwtTokenUtil;
    private RabbitTemplate rabbitTemplate;

    public OrganizationServiceImpl(UserRepository userRepository, OrganizationRepository organizationRepository,
                                   FilterUtil<Organization> filterUtil, EntityValidator entityValidator,
                                   JwtTokenUtil jwtTokenUtil, RabbitTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.filterUtil = filterUtil;
        this.entityValidator = entityValidator;
        this.jwtTokenUtil = jwtTokenUtil;
        this.rabbitTemplate = rabbitTemplate;
        log.info("OrganizationServiceImpl instantiated");
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        log.debug("UserRepository set");
    }

    @Autowired
    public void setOrganizationRepository(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
        log.debug("OrganizationRepository set");
    }

    @Autowired
    public void setFilterUtil(FilterUtil<Organization> filterUtil) {
        this.filterUtil = filterUtil;
        log.debug("FilterUtil set");
    }

    @Autowired
    public void setEntityValidator(EntityValidator entityValidator) {
        this.entityValidator = entityValidator;
        log.debug("EntityValidator set");
    }

    @Autowired
    public void setJwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
        log.debug("JwtTokenUtil set");
    }

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        log.debug("RabbitTemplate set");
    }

    @Override
    public OrganizationResponse create(OrganizationRequest dto) {
        log.trace("Entering create method with DTO: {}", dto);
        entityValidator.validate(dto);
        Organization organization = OrganizationMapper.toEntity(dto, userRepository.findById(dto.getUserId()).orElseThrow(() -> {
            log.error("User not found with id {}", dto.getUserId());
            return new EntityNotFoundException("User not found with id " + dto.getUserId());
        }));
        OrganizationResponse response = OrganizationMapper.toResponse(organizationRepository.save(organization));
        log.info("Created organization: {}", response);
        log.trace("Exiting create method with response: {}", response);
        return response;
    }

    @Override
    public OrganizationResponse get(Long id) {
        log.trace("Entering get method with id: {}", id);
        OrganizationResponse response = OrganizationMapper.toResponse(organizationRepository.findById(id).orElseThrow(() -> {
            log.error("Organization not found with id {}", id);
            return new EntityNotFoundException("Organization not found with id " + id);
        }));
        log.info("Fetched organization: {}", response);
        log.trace("Exiting get method with response: {}", response);
        return response;
    }

    @Override
    public OrganizationResponse update(Long id, OrganizationRequest dto) {
        log.trace("Entering update method with id: {} and DTO: {}", id, dto);
        entityValidator.validate(dto);
        Organization existingOrganization = organizationRepository.findById(id).orElseThrow(() -> {
            log.error("Organization not found with id {}", id);
            return new EntityNotFoundException("Organization not found with id " + id);
        });
        existingOrganization = OrganizationMapper.toEntityUpdate(dto, userRepository.findById(dto.getUserId()).orElseThrow(() -> {
            log.error("User not found with id {}", dto.getUserId());
            return new EntityNotFoundException("User not found with id " + dto.getUserId());
        }), existingOrganization);
        OrganizationResponse response = OrganizationMapper.toResponse(organizationRepository.save(existingOrganization));
        log.info("Updated organization: {}", response);
        log.trace("Exiting update method with response: {}", response);
        return response;
    }

    @Override
    public void delete(Long id) {
        log.trace("Entering delete method with id: {}", id);
        Organization organization = organizationRepository.findById(id).orElseThrow(() -> {
            log.error("Organization not found with id {}", id);
            return new EntityNotFoundException("Organization not found with id " + id);
        });
        User user = organization.getUser();
        user.setOrganization(null);
        userRepository.save(user);
        organizationRepository.deleteById(id);
        log.info("Deleted organization with id {}", id);
        log.trace("Exiting delete method");
    }

    @Override
    public List<OrganizationResponse> getAll() {
        log.trace("Entering getAll method");
        List<OrganizationResponse> responses = organizationRepository.findAll().stream().map(OrganizationMapper::toResponse).collect(Collectors.toList());
        log.info("Fetched all organizations, total count: {}", responses.size());
        log.trace("Exiting getAll method");
        return responses;
    }

    @Override
    public Page<OrganizationResponse> getPage(int size, int number, String sortBy, String sortDirection, Map<String, String> filters, Map<String, String> dateRanges) {
        log.trace("Entering getPage method with size: {}, number: {}, sortBy: {}, sortDirection: {}, filters: {}, dateRanges: {}", size, number, sortBy, sortDirection, filters, dateRanges);
        Sort.Direction direction = sortDirection != null ? Sort.Direction.fromString(sortDirection) : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "shortName");
        Pageable pageable = PageRequest.of(number, size, sort);
        log.debug("Created Pageable: {}", pageable);
        Specification<Organization> specification = filterUtil.buildSpecification(filters, dateRanges);
        log.debug("Built specification for filters: {} and dateRanges: {}", filters, dateRanges);
        Page<OrganizationResponse> response = organizationRepository.findAll(specification, pageable).map(OrganizationMapper::toResponse);
        log.info("Fetched {} organizations with filters and pagination", response.getTotalElements());
        log.trace("Exiting getPage method with response: {}", response);
        return response;
    }

    @Override
    public OrganizationResponse createOrganization(String accessToken, AuthOrganizationRequest organizationRequest) {
        log.trace("Entering createOrganization method with accessToken and DTO: {}", organizationRequest);
        entityValidator.validate(organizationRequest);
        User user = userRepository.findByEmail(jwtTokenUtil.extractUsername(accessToken))
                .orElseThrow(() -> {
                    log.error("User with email {} not found", jwtTokenUtil.extractUsername(accessToken));
                    return new EntityNotFoundException("User with email " + jwtTokenUtil.extractUsername(accessToken) + " not found");
                });
        Organization organization = OrganizationMapper.toEntityFromCreateRequest(organizationRequest, user);
        user.setOrganization(organization);
        organizationRepository.save(organization);
        userRepository.save(user);
        OrganizationResponse response = OrganizationMapper.toResponse(organization);
        log.info("Created organization for user: {}", response);
        log.trace("Exiting createOrganization method with response: {}", response);
        return response;
    }

    @Override
    public OrganizationResponse updateOrganization(String accessToken, AuthOrganizationRequest organizationRequest) {
        log.trace("Entering updateOrganization method with accessToken and DTO: {}", organizationRequest);
        entityValidator.validate(organizationRequest);
        User user = userRepository.findByEmail(jwtTokenUtil.extractUsername(accessToken))
                .orElseThrow(() -> {
                    log.error("User with email {} not found", jwtTokenUtil.extractUsername(accessToken));
                    return new EntityNotFoundException("User with email " + jwtTokenUtil.extractUsername(accessToken) + " not found");
                });
        Organization existingOrganization = organizationRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.error("Organization for user {} not found", user.getEmail());
                    return new EntityNotFoundException("Organization for user " + user.getEmail() + " not found");
                });
        Organization organization = OrganizationMapper.toEntityFromUpdateRequest(existingOrganization, organizationRequest);
        organizationRepository.save(organization);
        OrganizationResponse response = OrganizationMapper.toResponse(organization);
        log.info("Updated organization for user: {}", response);
        log.trace("Exiting updateOrganization method with response: {}", response);
        return response;
    }

    @Override
    @Transactional
    public void deleteOrganization(String accessToken) {
        log.trace("Entering deleteOrganization method with accessToken: {}", accessToken);
        User user = userRepository.findByEmail(jwtTokenUtil.extractUsername(accessToken))
                .orElseThrow(() -> {
                    log.error("User with email {} not found", jwtTokenUtil.extractUsername(accessToken));
                    return new EntityNotFoundException("User with email " + jwtTokenUtil.extractUsername(accessToken) + " not found");
                });
        user.setOrganization(null);
        userRepository.save(user);
        organizationRepository.deleteByUser(user);
        log.info("Deleted organization for user with email {}", user.getEmail());
        log.trace("Exiting deleteOrganization method");
    }

    @RabbitListener(queues = "reportQueue")
    public void receiveReportMessageAndSendEmail(ReportMessage reportMessage) {
        log.trace("Entering receiveReportMessageAndSendEmail method with reportMessage: {}", reportMessage);
        Organization organization = getOrganizationByTaxpayerNumber(reportMessage.getTaxpayerNumber());
        if (organization.getUser().getSetting().isSubscribe()) {
            if (reportMessage.getReportType().equals("Дневной отчет") && organization.getUser().getSetting().getReportNotifications().contains(ReportNotification.DAILY)) {
                sendEmailReport(reportMessage, organization);
                log.debug("Send daily report to mail service");
            }
            if (reportMessage.getReportType().equals("Пятиминутный отчет") && organization.getUser().getSetting().getReportNotifications().contains(ReportNotification.FIVE_MINUTE)) {
                sendEmailReport(reportMessage, organization);
                log.debug("Send five minute report to mail service");
            }
            if (reportMessage.getReportType().equals("Отчет по режимам") && organization.getUser().getSetting().getReportNotifications().contains(ReportNotification.MODE)) {
                sendEmailReport(reportMessage, organization);
                log.debug("Send mode report to mail service");
            }
        } else {
            log.debug("User is not subscribed. Skipping email report.");
        }
        log.trace("Exiting receiveReportMessageAndSendEmail method");
    }

    private void sendEmailReport(ReportMessage reportMessage, Organization organization) {
        log.debug("Sending email for report type: {} to email: {}", reportMessage.getReportType(), organization.getSpecialEmail());
        try{
            rabbitTemplate.convertAndSend("emailQueue", new EmailMessage(
                    new String[]{organization.getSpecialEmail()},
                    "Factory Monitoring",
                    String.format("%s для сенсора %s статус отчета: %s", reportMessage.getReportType(), reportMessage.getSensorNumber(), reportMessage.getMessage())
            ));
        } catch (MessageSendingException e) {
            log.error("Failed to send report email: {}", e.getMessage());
        }
    }

    private Organization getOrganizationByTaxpayerNumber(String taxpayerNumber) {
        log.trace("Entering getOrganizationByTaxpayerNumber with taxpayerNumber: {}", taxpayerNumber);
        Organization organization = organizationRepository.findByTaxpayerNumber(taxpayerNumber).orElseThrow(() -> {
            log.error("Taxpayer number {} not found", taxpayerNumber);
            return new EntityNotFoundException("Taxpayer number " + taxpayerNumber + " not found");
        });
        log.trace("Exiting getOrganizationByTaxpayerNumber method with organization: {}", organization);
        return organization;
    }
}

