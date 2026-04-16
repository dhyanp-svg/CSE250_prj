package com.canteen.management.repository;

import com.canteen.management.entity.CanteenOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CanteenOrderRepository extends JpaRepository<CanteenOrder, Long> {
    List<CanteenOrder> findByUserIdOrderByOrderDateDesc(Long userId);
    List<CanteenOrder> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
}
