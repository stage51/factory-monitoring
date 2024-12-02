package centrikt.factorymonitoring.authserver.services;

import org.springframework.data.domain.Page;

import java.util.Map;

public interface OnlineService<OnlineResponse> {
    Page<OnlineResponse> getPage(int size, int number, String sortBy, String sortDirection, Map<String, String> filters, Map<String, String> dateRanges);
}
