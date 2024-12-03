package centrikt.factorymonitoring.modereport.services;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface CrudService<Request, Response> {
    Response create(Request dto);
    Response get(Long id);
    Response update(Long id, Request dto);
    void delete(Long id);
    List<Response> getAll();
    Page<Response> getPage(int size, int number, String sortBy, String sortDirection, Map<String, String> filters, Map<String, String> dateRanges);
}
