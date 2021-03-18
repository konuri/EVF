package com.evf.mail.web.rest;

import com.evf.mail.domain.OrderDeliveryEntity;
import com.evf.mail.service.OrderDetailsService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order-details")
public class OrderDetailsController {
    private final OrderDetailsService orderDetailsService;

    public OrderDetailsController(OrderDetailsService orderDetailsService) {
        this.orderDetailsService = orderDetailsService;
    }

    @GetMapping("/")
    public Page<OrderDeliveryEntity> getOrderDetails(
        @RequestParam(defaultValue = "0") Integer offset,
        @RequestParam(defaultValue = "0") Integer limit,
        @RequestParam(defaultValue = "orderFrom") String sortBy,
        @RequestParam(defaultValue = "asc") String sortOrder,
        @RequestParam(required = false) String filter
    ) {
        return orderDetailsService.getAllOrderDetails(offset, limit, sortBy, sortOrder, filter);
    }
}
