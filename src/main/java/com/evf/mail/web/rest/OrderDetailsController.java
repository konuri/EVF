package com.evf.mail.web.rest;

import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.evf.mail.domain.OrderDeliveryEntity;
import com.evf.mail.service.OrderDetailsService;

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
    
    @PostMapping("/doordashAutoFilling")
    public ResponseEntity<Object>   doordashAutoFilling(@RequestParam("id") String id ){
    	try{
    		boolean status=orderDetailsService.doordashAutoFilling(id);
    		if(status){
    			return ResponseEntity.ok().body(JSONObject.quote("success"));
    			//return new ResponseEntity<Object>("success", HttpStatus.OK);
    		}else{
    			return ResponseEntity.ok().body(JSONObject.quote("failure"));
    			//return new ResponseEntity<Object>("failure", HttpStatus.EXPECTATION_FAILED);
    		}
    	}catch(Exception e){
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Please contact IT Team");
    	}
    }
}
