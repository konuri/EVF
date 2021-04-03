package com.evf.mail.tempaltes;

import com.evf.mail.domain.OrderDeliveryEntity;
import com.evf.mail.domain.ShortNameAndTipDetails;
import com.evf.mail.service.MailContentExtraction;
import com.evf.mail.util.PhoneFormatter;

import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("sharebite")
public class SharebiteLogic implements MailContentExtraction {
    private final Logger log = LoggerFactory.getLogger(SharebiteLogic.class);

    @Autowired
    Map<String,ShortNameAndTipDetails> areaShortNames;
    
    public OrderDeliveryEntity extractContent(Document doc) {
        Elements tables = doc.select("table");
        Elements deliveryTo = tables.get(6).select("div");
        String fullName = deliveryTo.get(1).text();
        String firstName = fullName.split(" ")[0];
        String lastName = fullName.replace(firstName, "");
        String phone = deliveryTo.get(3).text().trim().replaceAll("[^0-9]", "");
        phone = new PhoneFormatter(phone).getPhoneNumber();
        String address = tables.get(6).select("a").get(0).text();
        String orderId = "";
        if(tables.get(1).select("div").first().select("span").text().contains("#")){
        	orderId=tables.get(1).select("div").first().select("span").text().replace("#", "").trim();
        }else{
        	orderId=tables.get(1).select("div").first().select("span").text().trim();
        }

        Elements qtyTable = tables.get(10).select("table").first().select("td").select("table");

        Long quantity = null;
        for (Element qtyElment : qtyTable) {
        	if(quantity==null){
        		quantity = Long.parseLong(qtyElment.select("div").first().text().replace("x", ""));
        	}else{
                quantity = quantity + Long.parseLong(qtyElment.select("div").first().text().replace("x", ""));
            }
        }
        //String subTotal = tables.get(13).select("table").first() .select("td").select("table").select("tr").first().select("div").get(2).text();
        
        String subTotal= tables.get(13).select("tr").first().select("div").get(2).text();
        String tip = "$0";
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

        return orderDeliveryEntity;
    }
}
