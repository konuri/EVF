package com.evf.mail.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.evf.mail.domain.OrderDeliveryEntity;


@Repository
public interface OrderDeliveryRepository extends JpaRepository<OrderDeliveryEntity, String>, JpaSpecificationExecutor<OrderDeliveryEntity> {

	Optional<OrderDeliveryEntity> findAllByOrderId(String orderId);
	
}
