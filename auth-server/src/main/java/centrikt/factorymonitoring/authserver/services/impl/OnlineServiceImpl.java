package centrikt.factorymonitoring.authserver.services.impl;

import centrikt.factorymonitoring.authserver.dtos.responses.OnlineResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.OnlineResponse;
import centrikt.factorymonitoring.authserver.mappers.OnlineMapper;
import centrikt.factorymonitoring.authserver.models.Online;
import centrikt.factorymonitoring.authserver.repos.OnlineRepository;
import centrikt.factorymonitoring.authserver.services.OnlineService;
import centrikt.factorymonitoring.authserver.utils.filter.FilterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OnlineServiceImpl implements OnlineService<OnlineResponse> {
    private OnlineRepository onlineRepository;
    private FilterUtil<Online> filterUtil;

    public OnlineServiceImpl(OnlineRepository onlineRepository, FilterUtil<Online> filterUtil) {
        this.onlineRepository = onlineRepository;
        this.filterUtil = filterUtil;
    }

    @Autowired
    public void setOnlineRepository(OnlineRepository onlineRepository) {
        this.onlineRepository = onlineRepository;
    }
    @Autowired
    public void setFilterUtil(FilterUtil<Online> filterUtil) {
        this.filterUtil = filterUtil;
    }

    @Override
    public Page<OnlineResponse> getPage(int size, int number, String sortBy, String sortDirection, Map<String, String> filters, Map<String, String> dateRanges) {
        Sort.Direction direction = sortDirection != null ? Sort.Direction.fromString(sortDirection) : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "activeAt");
        Pageable pageable = PageRequest.of(number, size, sort);
        Specification<Online> specification = filterUtil.buildSpecification(filters, dateRanges);
        return onlineRepository.findAll(specification, pageable).map(OnlineMapper::toResponse);
    }
}
