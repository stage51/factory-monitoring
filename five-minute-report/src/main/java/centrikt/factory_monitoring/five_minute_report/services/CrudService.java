package centrikt.factory_monitoring.five_minute_report.services;

import java.util.List;

public interface CrudService<T, K> {
    T create(T dto);
    T get(Long id);
    T update(Long id, T dto);
    void delete(Long id);
    List<T> getAll();
}
