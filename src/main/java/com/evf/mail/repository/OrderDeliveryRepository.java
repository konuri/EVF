package com.evf.mail.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.evf.mail.domain.OrderDeliveryEntity;

@Repository
public interface OrderDeliveryRepository extends JpaRepository<OrderDeliveryEntity, UUID> {

}
