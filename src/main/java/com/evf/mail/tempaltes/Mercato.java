package com.evf.mail.tempaltes;

import com.evf.mail.domain.OrderDeliveryEntity;
import com.evf.mail.service.MailContentExtraction;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("Mercato")
public class Mercato implements MailContentExtraction {
    private final Logger log = LoggerFactory.getLogger(Mercato.class);

    public OrderDeliveryEntity extractContent(Document document) {
        List<Node> tables = document.select("body,br").get(0).childNodes();
        String orderId = getValue(tables, " Order Number:").trim();
        log.info("Order Number\t = " + orderId);
        Integer quantity = getProductsCount(tables, " Quantity:");
        log.info("quantity\t = " + quantity);
        String subTotal = getValue(tables, " Item Total:").trim();
        log.info("subTotal\t = " + subTotal);
        String tip = getValue(tables, " Delivery Tip:").trim();
        log.info("Tip\t = " + tip);
        String fullName = getValue(tables, " Customer Name:").trim();
        String firstName = fullName.split(" ")[0];
        String lastName = fullName.replace(firstName, "");
        log.info("fullName\t = " + fullName);
        String phone = getValue(tables, " Customer Phone:").trim();
        log.info("Phone Number\t = " + phone);
        String deliveryTo = document
            .text()
            .substring(document.text().indexOf(" Delivery to:") + " Delivery to:".length(), document.text().indexOf("Items in your order:"))
            .trim();
        String zipcode = pinCodeChanges(deliveryTo.substring(deliveryTo.lastIndexOf(",") + 1, deliveryTo.length()).trim());
        log.info("zip code\t = " + zipcode);
        String address = deliveryTo.replace(" Delivery to:", "").replace(fullName, "");
        log.info("address \t = " + address);
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

    public static String getValue(List<Node> childNodes, String check) {
        String result = "";

        for (Node node2 : childNodes) {
            String text = node2.toString();
            if (text.startsWith(check)) {
                String element = text.substring(text.indexOf(check) + check.length(), text.length()).trim();
                result = element.substring(0, element.length()).trim();
            }
        }
        return result;
    }

    public static Integer getProductsCount(List<Node> childNodes, String check) {
        Integer count = 0;
        for (Node node2 : childNodes) {
            String text = node2.toString();
            if (text.startsWith(check)) {
                String element = text.substring(text.indexOf(check) + check.length(), text.length()).trim();
                count = count + Integer.parseInt(element.substring(0, element.length()).trim());
            }
        }
        return count;
    }

    public static String pinCodeChanges(String s) {
        String pincode = "";
        for (int i = 0; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i))) {
                pincode = pincode + s.charAt(i);
            } else {
                pincode = "";
            }
        }
        return pincode;
    }
}
