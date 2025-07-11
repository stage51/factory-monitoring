package ru.centrikt.factorymonitoringservice.application.utils.filter.handlers;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

public interface CustomFilterHandler<Entity> {
    boolean supports(String fieldName);

    void applyFilter(Root<Entity> root,
                     CriteriaBuilder cb,
                     List<Predicate> predicates,
                     String rawValue);
}
