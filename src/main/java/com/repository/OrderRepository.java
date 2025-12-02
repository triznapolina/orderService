package com.repository;

import com.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE Order o SET o.deleted = :status WHERE o.id = :orderId")
    void setDeletedStatus(@Param("status") boolean status, @Param("orderId") Long orderId);


    List<Order> findByStatus(String status);

    List<Order> findByUserIdAndStatus(Long userId, String status);


    @Query("SELECT d FROM Order d WHERE d.deleted = false")
    List<Order> findAllActiveOrders();


    Page<Order> findAll(Specification<Order> spec, Pageable pageable);
}
