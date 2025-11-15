package com.certimaster.commoncore.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Base specification for building dynamic queries
 */
public class BaseSpecification<T> implements Specification<T> {

    private final List<SearchCriteria> criteriaList = new ArrayList<>();

    public void add(SearchCriteria criteria) {
        criteriaList.add(criteria);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();

        for (SearchCriteria criteria : criteriaList) {
            Predicate predicate = buildPredicate(root, builder, criteria);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }

        return builder.and(predicates.toArray(new Predicate[0]));
    }

    private Predicate buildPredicate(Root<T> root, CriteriaBuilder builder, SearchCriteria criteria) {
        String key = criteria.key();
        Object value = criteria.value();
        String operation = criteria.operation();

        if (value == null) {
            return null;
        }

        switch (operation) {
            case "=":
                return builder.equal(root.get(key), value);

            case "!=":
                return builder.notEqual(root.get(key), value);

            case ">":
                if (value instanceof Comparable) {
                    return builder.greaterThan(root.get(key), (Comparable) value);
                }
                break;

            case "<":
                if (value instanceof Comparable) {
                    return builder.lessThan(root.get(key), (Comparable) value);
                }
                break;

            case ">=":
                if (value instanceof Comparable) {
                    return builder.greaterThanOrEqualTo(root.get(key), (Comparable) value);
                }
                break;

            case "<=":
                if (value instanceof Comparable) {
                    return builder.lessThanOrEqualTo(root.get(key), (Comparable) value);
                }
                break;

            case "LIKE":
                if (value instanceof String) {
                    return builder.like(
                            builder.lower(root.get(key)),
                            "%" + ((String) value).toLowerCase() + "%"
                    );
                }
                break;

            case "IN":
                if (value instanceof List) {
                    return root.get(key).in((List<?>) value);
                }
                break;

            case "BETWEEN":
                if (value instanceof Object[] range && range.length == 2) {
                    if (range[0] instanceof LocalDateTime && range[1] instanceof LocalDateTime) {
                        return builder.between(root.get(key),
                                (LocalDateTime) range[0],
                                (LocalDateTime) range[1]);
                    }
                }
                break;
        }

        return null;
    }

    /**
     * Search criteria holder
     */
    public record SearchCriteria(String key, String operation, Object value) {
    }
}
