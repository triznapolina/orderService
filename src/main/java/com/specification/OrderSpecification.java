package com.specification;


import com.entity.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class OrderSpecification {

    public static Specification<Order> hasStatus(String status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);

    }

    public static Specification<Order> hasCreationDate(LocalDate date) {
        return (root, query, cb) -> cb.equal(root.get("createdAt"), date);
    }


}
