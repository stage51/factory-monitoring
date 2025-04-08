package centrikt.factorymonitoring.modereport.services;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ReadService<Request, Response> {
    Response get(Long id);
    List<Response> getAll();
    Page<Response> getPage(int size, int number, String sortBy, String sortDirection, Map<String, String> filters, Map<String, String> dateRanges);
}
