package com.github.bwar.sb.ms.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.bwar.sb.ms.orderservice.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{

}
