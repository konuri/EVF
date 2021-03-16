package com.evf.mail.repository;

import com.evf.mail.domain.OrderDeliveryEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDeliveryRepository extends JpaRepository<OrderDeliveryEntity, UUID>, JpaSpecificationExecutor<OrderDeliveryEntity> {}
