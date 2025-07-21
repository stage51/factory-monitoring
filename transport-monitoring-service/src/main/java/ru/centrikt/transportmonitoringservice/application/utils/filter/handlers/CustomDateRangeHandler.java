package ru.centrikt.transportmonitoringservice.application.utils.filter.handlers;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

public interface CustomDateRangeHandler<Entity> {
    boolean supports(String fieldName);

    void applyDateRange(Root<Entity> root,
                     CriteriaBuilder cb,
                     List<Predicate> predicates,
                     String rawValue);
}
