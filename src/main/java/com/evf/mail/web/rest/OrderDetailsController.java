package com.evf.mail.web.rest;

import com.evf.mail.domain.OrderDeliveryEntity;
import com.evf.mail.service.OrderDetailsService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
        @RequestParam(defaultValue = "createdDate") String sortBy,
        @RequestParam(defaultValue = "desc") String sortOrder,
        @RequestParam(required = false) String filter
    ) {
        return orderDetailsService.getAllOrderDetails(offset, limit, sortBy, "desc", filter);
    }

    @PostMapping("/doordashAutoFilling")
    public ResponseEntity<Object> doordashAutoFilling(@RequestParam("id") String id) {
        try {
            boolean status = orderDetailsService.doordashAutoFilling(id);
            if (status) {
                return new ResponseEntity<Object>("success", HttpStatus.OK);
            } else {
                return new ResponseEntity<Object>("failure", HttpStatus.EXPECTATION_FAILED);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Please contact IT Team");
        }
    }

    @PostMapping("/updateOrderDetails")
    public ResponseEntity<Object> updateOrderDetails(@RequestBody List<OrderDeliveryEntity> entity) {
        try {
            orderDetailsService.updateOrderDetails(entity);
            boolean status = orderDetailsService.doordashAutoFilling(entity.get(0).getId());
            if (status) {
                return new ResponseEntity<Object>("success", HttpStatus.OK);
            } else {
                return new ResponseEntity<Object>("failure", HttpStatus.EXPECTATION_FAILED);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Please contact IT Team");
        }
    }
}
