package com.evf.mail.tempaltes;

import com.evf.mail.domain.OrderDeliveryEntity;
import com.evf.mail.service.MailContentExtraction;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("sharebite")
public class SharebiteLogic implements MailContentExtraction {
    private final Logger log = LoggerFactory.getLogger(SharebiteLogic.class);

    public OrderDeliveryEntity extractContent(Document doc) {
        Elements tables = doc.select("table");
        Elements deliveryTo = tables.get(6).select("div");
        String fullName = deliveryTo.get(1).text();
        String firstName = fullName.split(" ")[0];
        String lastName = fullName.replace(firstName, "");
        String phone = deliveryTo.get(3).text();
        String address = tables.get(6).select("a").get(0).text();
        String orderId = tables.get(1).select("div").first().select("span").text();

        Elements qtyTable = tables.get(10).select("table").first().select("td").select("table");

        Long quantity = null;
        for (Element qtyElment : qtyTable) {
            quantity = quantity + Integer.parseInt(qtyElment.select("div").first().text().replace("x", ""));
        }
        String subTotal = tables
            .get(13)
            .select("table")
            .first()
            .select("td")
            .select("table")
            .select("tr")
            .first()
            .select("div")
            .get(2)
            .text();
        String tip = "0";
        String zipcode = address.substring(address.lastIndexOf(',') - 6, address.lastIndexOf(',')).trim();

        log.info("fullName :: " + fullName);
        log.info("Phone :: " + phone);
        log.info("Address :: " + address);
        log.info("subTotal :: " + subTotal);
        log.info("tip :: " + tip);
        log.info("quantity :: " + quantity);
        log.info("zipcode :: " + zipcode);
        log.info("orderId :: " + orderId);

        OrderDeliveryEntity orderDeliveryEntity = new OrderDeliveryEntity();
        orderDeliveryEntity.setFirstName(firstName);
        orderDeliveryEntity.setLastName(lastName);
        orderDeliveryEntity.setAddress(address);
        orderDeliveryEntity.setPhone(phone);
        orderDeliveryEntity.setQuantity(quantity.longValue());
        orderDeliveryEntity.setSubTotal(subTotal);
        orderDeliveryEntity.setTip(tip);
        orderDeliveryEntity.setOrderId(orderId);
        orderDeliveryEntity.setZipcode(Long.parseLong(zipcode));

        return orderDeliveryEntity;
    }
}
