package centrikt.factory_monitoring.five_minute_report.services;

import centrikt.factory_monitoring.five_minute_report.dtos.extended.DateRange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
