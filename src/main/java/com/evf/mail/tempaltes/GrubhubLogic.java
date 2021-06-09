package com.evf.mail.tempaltes;

import com.evf.mail.domain.OrderDeliveryEntity;
import com.evf.mail.domain.ShortNameAndTipDetails;
import com.evf.mail.service.MailContentExtraction;
import com.evf.mail.util.PhoneFormatter;

import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("grubhub")
public class GrubhubLogic implements MailContentExtraction {
    private final Logger log = LoggerFactory.getLogger(GrubhubLogic.class);

    @Autowired
    Map<String,ShortNameAndTipDetails> areaShortNames;
    
    public OrderDeliveryEntity extractContent(Document doc) {

        String orderId = "";
        if(doc.select("div:contains(Order:)").get(1).getAllElements().get(2).text().contains("#")){
        	orderId=doc.select("div:contains(Order:)").get(1).getAllElements().get(2).text().replace("#", "").trim();
        }else{
        	orderId=doc.select("div:contains(Order:)").get(1).getAllElements().get(2).text().trim();
        }
        String fullName = doc.select("div:contains(Deliver to:)").get(2).getAllElements().get(2).text();
        String phone = "";
        String address = "";
        String subTotal = "";
        String tip = "$0";
        String apartment = "";
        Long quantity = null;
        String zipcode = null;
        for (Element element : doc.getElementsByAttributeValue("data-section", "diner")) {
            phone = element.getElementsByAttributeValue("data-field", "phone").text().replaceAll("[^0-9]", "");
    		if(phone.length()>10){
    			phone=phone.substring(phone.length()-10);
    		}
            phone = new PhoneFormatter(phone).getPhoneNumber();
            address = element.getElementsByAttributeValue("data-field", "address1").text();
            apartment = element.getElementsByAttributeValue("data-field", "address2").text();
            address = address + " " + element.getElementsByAttributeValue("data-field", "city").text();
            address = address + " " + element.getElementsByAttributeValue("data-field", "state").text();
            address = address + "," + element.getElementsByAttributeValue("data-field", "zip").text();
            zipcode = element.getElementsByAttributeValue("data-field", "zip").text();
        }

        for (Element element : doc.getElementsByAttributeValue("data-section", "order")) {
            subTotal = element.getElementsByAttributeValue("data-field", "subtotal").text();
            tip = element.getElementsByAttributeValue("data-field", "tip").text();
        }

        for (Element element : doc.getElementsByAttributeValue("data-section", "menu-item")) {
            if (quantity == null) {
                quantity = Long.parseLong(element.getElementsByAttributeValue("data-field", "quantity").text());
            } else {
                quantity = quantity + Long.parseLong(element.getElementsByAttributeValue("data-field", "quantity").text());
            }
        }
        String firstName = fullName.split(" ")[0];
        String lastName = fullName.replace(firstName, "");
        log.info("fullName :: " + fullName);
        log.info("Phone :: " + phone);
        log.info("Address :: " + address);
        log.info("subTotal :: " + subTotal);
        log.info("tip :: " + tip);
        log.info("quantity :: " + quantity);
        log.info("apartment :: " + apartment);

        OrderDeliveryEntity orderDeliveryEntity = new OrderDeliveryEntity();
        orderDeliveryEntity.setFullName(fullName);
        orderDeliveryEntity.setModifiedName(fullName.concat(areaShortNames.containsKey(zipcode) ?  " ("+areaShortNames.get(zipcode).getAreaShortName()+")" : ""));
        orderDeliveryEntity.setFirstName(firstName);
        orderDeliveryEntity.setLastName(lastName);
        orderDeliveryEntity.setAddress(address);
        orderDeliveryEntity.setPhone(phone);
        orderDeliveryEntity.setQuantity(quantity.longValue());
        orderDeliveryEntity.setSubTotal(subTotal);
        orderDeliveryEntity.setTip(areaShortNames.containsKey(zipcode) ?areaShortNames.get(zipcode).getTip():tip);
        orderDeliveryEntity.setOrderId(orderId);
        orderDeliveryEntity.setZipcode(zipcode!=null ? Long.parseLong(zipcode) : null);
        orderDeliveryEntity.setApartment(apartment);
        orderDeliveryEntity.setRegionCode(areaShortNames.containsKey(zipcode) ?  areaShortNames.get(zipcode).getAreaShortName() : "NA");
        return orderDeliveryEntity;
    }
}
