package com.emanuel.BiblioPlus.modules.orders.infra.database.repositories;

import com.emanuel.BiblioPlus.modules.orders.infra.database.entities.OrderModel;
import com.emanuel.BiblioPlus.modules.orders.infra.database.entities.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository <OrderModel, UUID> {
    @Query(value = """
         SELECT *
         FROM tb_orders O
         WHERE
             O.user_id = :user_id
             AND (
                 (:start_date BETWEEN O.start_date AND O.end_date OR
                 :end_date BETWEEN O.start_date AND O.end_date) OR
                 (O.start_date BETWEEN :start_date AND :end_date OR
                 O.end_date BETWEEN :start_date AND :end_date)
             );
     """, nativeQuery = true)
    Optional<OrderModel> findOrderByStartAndEndDate(UUID user_id ,LocalDate start_date, LocalDate end_date);

    @Query("SELECT O from OrderModel O where O.orderStatus = 'DELIVERED' AND O.user.id = :userId")
    Optional<OrderModel> findOrderByUserAndOrderStatus(UUID userId);
}
