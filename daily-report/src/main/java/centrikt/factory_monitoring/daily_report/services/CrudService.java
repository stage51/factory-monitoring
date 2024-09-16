package centrikt.factory_monitoring.daily_report.services;

import java.util.List;

public interface CrudService<T, K> {
    K create(T dto);
    K get(Long id);
    K update(Long id, T dto);
    void delete(Long id);
    List<K> getAll();
}
