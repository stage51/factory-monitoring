package centrikt.factorymonitoring.authserver.services.impl;

import centrikt.factorymonitoring.authserver.dtos.responses.OrganizationResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.RefreshTokenResponse;
import centrikt.factorymonitoring.authserver.mappers.OrganizationMapper;
import centrikt.factorymonitoring.authserver.mappers.RefreshTokenMapper;
import centrikt.factorymonitoring.authserver.models.Organization;
import centrikt.factorymonitoring.authserver.models.RefreshToken;
import centrikt.factorymonitoring.authserver.repos.RefreshTokenRepository;
import centrikt.factorymonitoring.authserver.services.RefreshTokenService;
import centrikt.factorymonitoring.authserver.utils.filter.FilterUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService<RefreshTokenResponse> {
    private RefreshTokenRepository refreshTokenRepository;
    private FilterUtil<RefreshToken> filterUtil;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, FilterUtil<RefreshToken> filterUtil) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.filterUtil = filterUtil;
        log.info("RefreshTokenServiceImpl initialized");
    }

    @Autowired
    public void setRefreshTokenRepository(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        log.debug("RefreshTokenRepository set");
    }

    @Autowired
    public void setFilterUtil(FilterUtil<RefreshToken> filterUtil) {
        this.filterUtil = filterUtil;
        log.debug("FilterUtil set");
    }

    @Override
    public Page<RefreshTokenResponse> getPage(int size, int number, String sortBy, String sortDirection, Map<String, String> filters, Map<String, String> dateRanges) {
        log.trace("Entering getPage method with size: {}, number: {}, sortBy: {}, sortDirection: {}, filters: {}, dateRanges: {}", size, number, sortBy, sortDirection, filters, dateRanges);

        Sort.Direction direction = sortDirection != null ? Sort.Direction.fromString(sortDirection) : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "id");
        Pageable pageable = PageRequest.of(number, size, sort);

        log.debug("Created Pageable with sorting: {}", sort);

        Specification<RefreshToken> specification = filterUtil.buildSpecification(filters, dateRanges);
        log.debug("Built specification with filters: {} and dateRanges: {}", filters, dateRanges);

        Page<RefreshTokenResponse> pageResponse = refreshTokenRepository.findAll(specification, pageable).map(RefreshTokenMapper::toResponse);
        log.info("Fetched {} refresh tokens with pagination, total elements: {}", pageResponse.getContent().size(), pageResponse.getTotalElements());

        log.trace("Exiting getPage method with response: {}", pageResponse);
        return pageResponse;
    }
}

