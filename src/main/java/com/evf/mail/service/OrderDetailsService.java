package com.evf.mail.service;

import com.evf.mail.domain.OrderDeliveryEntity;
import com.evf.mail.repository.OrderDeliveryRepository;
import com.evf.mail.repository.OrderDetailsSpecification;
import com.evf.mail.repository.SearchCriteria;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailsService {
    private final OrderDeliveryRepository orderDeliveryRepository;

    public OrderDetailsService(OrderDeliveryRepository orderDeliveryRepository) {
        this.orderDeliveryRepository = orderDeliveryRepository;
    }

    public Page<OrderDeliveryEntity> getAllOrderDetails(Integer offset, Integer limit, String sortBy, String sortOrder, String filter) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
        if (filter == null || "".equals(filter.trim())) {
            return orderDeliveryRepository.findAll(pageable);
        } else {
            OrderDetailsSpecification spec1 = new OrderDetailsSpecification(new SearchCriteria("orderId", ":", filter));
            OrderDetailsSpecification spec2 = new OrderDetailsSpecification(new SearchCriteria("orderFrom", ":", filter));
            OrderDetailsSpecification spec3 = new OrderDetailsSpecification(new SearchCriteria("tip", ":", filter));
            OrderDetailsSpecification spec4 = new OrderDetailsSpecification(new SearchCriteria("subTotal", ":", filter));
            OrderDetailsSpecification spec7 = new OrderDetailsSpecification(new SearchCriteria("phone", ":", filter));
            OrderDetailsSpecification spec8 = new OrderDetailsSpecification(new SearchCriteria("address", ":", filter));
            OrderDetailsSpecification spec9 = new OrderDetailsSpecification(new SearchCriteria("lastName", ":", filter));
            OrderDetailsSpecification spec10 = new OrderDetailsSpecification(new SearchCriteria("firstName", ":", filter));
            Specification<OrderDeliveryEntity> or = Specification
                .where(spec1)
                .or(spec2)
                .or(spec3)
                .or(spec4)
                .or(spec7)
                .or(spec8)
                .or(spec9)
                .or(spec10);
            if (NumberUtils.isDigits(filter)) {
                OrderDetailsSpecification spec5 = new OrderDetailsSpecification(new SearchCriteria("quantity", ":", filter));
                OrderDetailsSpecification spec6 = new OrderDetailsSpecification(new SearchCriteria("zipcode", ":", filter));
                or.or(spec5).or(spec6);
            }
            return orderDeliveryRepository.findAll(or, pageable);
        }
    }
}
