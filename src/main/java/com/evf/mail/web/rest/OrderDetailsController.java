package com.evf.mail.web.rest;

import com.evf.mail.domain.OrderDeliveryEntity;
import com.evf.mail.repository.OrderDeliveryRepository;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order-details")
public class OrderDetailsController {
    private final OrderDeliveryRepository orderDeliveryRepository;

    public OrderDetailsController(OrderDeliveryRepository orderDeliveryRepository) {
        this.orderDeliveryRepository = orderDeliveryRepository;
    }

    @GetMapping("/")
    public List<OrderDeliveryEntity> getOrderDetails(HttpServletRequest request) {
        return orderDeliveryRepository.findAll();
    }
}
