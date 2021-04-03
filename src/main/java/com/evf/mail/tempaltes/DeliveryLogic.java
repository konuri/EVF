package com.evf.mail.tempaltes;

import com.evf.mail.domain.OrderDeliveryEntity;
import com.evf.mail.domain.ShortNameAndTipDetails;
import com.evf.mail.service.MailContentExtraction;
import com.evf.mail.util.PhoneFormatter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("delivery")
public class DeliveryLogic implements MailContentExtraction {
    private final Logger log = LoggerFactory.getLogger(DeliveryLogic.class);

    @Autowired
    Map<String,ShortNameAndTipDetails> areaShortNames;
    
    public OrderDeliveryEntity extractContent(Document doc) {
        String deliverTo = doc.getElementById("CUSTOMER-INFO-AND-SPECIAL-INSTRUCTIONS").select("td").first().text();
        String nameAndAddressAndPhone = deliverTo.substring(0, deliverTo.indexOf("(Order placed:")).replace("(Order placed:", "").trim();
        String phone = nameAndAddressAndPhone.substring(nameAndAddressAndPhone.length() - 12).trim().replaceAll("[^0-9]", "");
        phone = new PhoneFormatter(phone).getPhoneNumber();
        String nameAndAddress = nameAndAddressAndPhone.replace(nameAndAddressAndPhone.substring(nameAndAddressAndPhone.length() - 12).trim(), "").trim();
        String fullName = nameAndAddress.substring(0, nameAndAddress.indexOf("(")).trim();
       // String address = nameAndAddress.substring(nameAndAddress.indexOf(")"), nameAndAddress.length()).replace(")", "").trim();
		String[] addressAndApartment=doc.getElementById("CUSTOMER-INFO-AND-SPECIAL-INSTRUCTIONS").select("td").first().html().split("<br>");
		String apartment=addressAndApartment[1].substring(addressAndApartment[1].indexOf("Unit:"));
		String address=addressAndApartment[1].replace(apartment, "")+addressAndApartment[2];
        Elements qtyHtml = doc.getElementById("ITEMS-TABLE").select("tbody").select("tr");
        Long quantity = null;
        for (int i = 1; i < qtyHtml.size(); i++) {
            if (quantity == null) {
                quantity = Long.parseLong(qtyHtml.select("td").first().text().toString());
            } else {
                quantity = quantity + Long.parseLong(qtyHtml.select("td").first().text().toString());
            }
        }
        List<String> billInfoKeys = Arrays.asList((doc.getElementById("MERCHANT-RECEIVES-LABELS").select("td").text().split(":")));
        billInfoKeys.replaceAll(String::trim);
        List<String> billInfoValues = Arrays.asList((doc.getElementById("MERCHANT-RECEIVES-VALUES").select("td").text().split("\\s")));
        billInfoValues.replaceAll(String::trim);
        log.info(billInfoValues.toString());

        String subTotal = billInfoValues.get(billInfoKeys.indexOf("Subtotal")).trim();
        String tip = "$0";
        if (billInfoValues.contains("Delivery fee")) {
            tip = billInfoValues.get(billInfoKeys.indexOf("Delivery fee")).trim();
        }
        String orderInfo = doc.getElementById("DCOM-LOGO-AND-MERCHANT-INFO").select("tr").select("td").last().text();

        String zipcode = address.substring(address.lastIndexOf(' '), address.length()).trim();

        String orderId = "";
        if(orderInfo.substring(orderInfo.indexOf("Order"), orderInfo.length()).replace("Order", "").trim().contains("#")){
        	orderId=orderInfo.substring(orderInfo.indexOf("Order"), orderInfo.length()).replace("Order", "").trim().replace("#", "").trim();
        }else{
        	orderId=orderInfo.substring(orderInfo.indexOf("Order"), orderInfo.length()).replace("Order", "").trim().trim();
        }
        String firstName = fullName.split(" ")[0];
        String lastName = fullName.replace(firstName, "");
        log.info("fullName :: " + fullName);
        log.info("Address :: " + address);
        log.info("Phone :: " + phone);
        log.info("quantity :: " + quantity);
        log.info("subTotal :: " + subTotal);
        log.info("tip :: " + tip);
        log.info("zipcode :: " + zipcode);
        log.info("orderId :: " + orderId);
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
        orderDeliveryEntity.setZipcode(Long.parseLong(zipcode));
        orderDeliveryEntity.setApartment(apartment);
        return orderDeliveryEntity;
    }

    public void deliveryLogic(String result) {
        String nameAndAddressAndPhone = result
            .substring(result.indexOf("Deliver ASAP Prepaid (Do not collect payment)"), result.indexOf("(Order placed:"))
            .replace("Deliver ASAP Prepaid (Do not collect payment)", "")
            .trim();
        String phonevalue = nameAndAddressAndPhone.substring(nameAndAddressAndPhone.length() - 12).trim();
        String nameAndAddress = nameAndAddressAndPhone.replace(phonevalue, "").trim();
        String fullName = nameAndAddress.substring(0, nameAndAddress.indexOf("(")).trim();
        String address = nameAndAddress.substring(nameAndAddress.indexOf(")"), nameAndAddress.length() - 1).replace(")", "").trim();

        List<String> keyDriverTip = Arrays.asList(
            result.substring(result.indexOf("Subtotal:"), result.indexOf("Merchant receives:")).trim().split(":")
        );
        keyDriverTip.replaceAll(String::trim);
        List<String> ValueDriverTip = Arrays.asList(
            result
                .substring(result.indexOf("Merchant receives:"), result.indexOf("Confirmation Code:"))
                .replace("Merchant receives:", "")
                .trim()
                .split("\\s")
        );
        String subtotal = ValueDriverTip.get(keyDriverTip.indexOf("Subtotal"));
        String driverTip = "0";

        if (keyDriverTip.contains("Delivery fee")) {
            driverTip = ValueDriverTip.get(keyDriverTip.indexOf("Delivery fee"));
        }
        log.info("fullName :: " + fullName);

        log.info("Address :: " + address);
        log.info("Phone :: " + phonevalue);
        log.info("subtotal :: " + subtotal);
        log.info("driverTip :: " + driverTip);
        //log.info("quantity :: "+quantity);
    }
}
