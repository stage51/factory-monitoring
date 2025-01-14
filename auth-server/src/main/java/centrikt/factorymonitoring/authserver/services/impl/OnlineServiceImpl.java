package centrikt.factorymonitoring.authserver.services.impl;

import centrikt.factorymonitoring.authserver.dtos.responses.OnlineResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.OnlineResponse;
import centrikt.factorymonitoring.authserver.mappers.OnlineMapper;
import centrikt.factorymonitoring.authserver.models.Online;
import centrikt.factorymonitoring.authserver.repos.OnlineRepository;
import centrikt.factorymonitoring.authserver.services.OnlineService;
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
public class OnlineServiceImpl implements OnlineService<OnlineResponse> {
    private OnlineRepository onlineRepository;
    private FilterUtil<Online> filterUtil;

    public OnlineServiceImpl(OnlineRepository onlineRepository, FilterUtil<Online> filterUtil) {
        this.onlineRepository = onlineRepository;
        this.filterUtil = filterUtil;
        log.info("OnlineServiceImpl initialized");
    }

    @Autowired
    public void setOnlineRepository(OnlineRepository onlineRepository) {
        this.onlineRepository = onlineRepository;
        log.debug("OnlineRepository set");
    }

    @Autowired
    public void setFilterUtil(FilterUtil<Online> filterUtil) {
        this.filterUtil = filterUtil;
        log.debug("FilterUtil set");
    }

    @Override
    public Page<OnlineResponse> getPage(int size, int number, String sortBy, String sortDirection, Map<String, String> filters, Map<String, String> dateRanges) {
        log.trace("Entering getPage method with parameters: size={}, number={}, sortBy={}, sortDirection={}, filters={}, dateRanges={}",
                size, number, sortBy, sortDirection, filters, dateRanges);

        Sort.Direction direction = sortDirection != null ? Sort.Direction.fromString(sortDirection) : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "activeAt");
        log.debug("Using sort direction: {}, sortBy: {}", direction, sortBy != null ? sortBy : "activeAt");

        Pageable pageable = PageRequest.of(number, size, sort);
        log.debug("Created pageable with page number: {}, page size: {}, sort: {}", number, size, sort);

        Specification<Online> specification = filterUtil.buildSpecification(filters, dateRanges);
        log.debug("Built specification for filters: {} and dateRanges: {}", filters, dateRanges);

        log.info("Fetching online records with the specification and pageable");
        Page<OnlineResponse> response = onlineRepository.findAll(specification, pageable).map(OnlineMapper::toResponse);

        log.debug("Fetched {} records from the database", response.getContent().size());
        log.trace("Exiting getPage method with result: {}", response);

        return response;
    }
}
