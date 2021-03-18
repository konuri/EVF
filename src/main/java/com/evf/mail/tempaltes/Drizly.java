package com.evf.mail.tempaltes;

import com.evf.mail.domain.OrderDeliveryEntity;
import com.evf.mail.service.MailContentExtraction;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("Drizly")
public class Drizly implements MailContentExtraction {
    private final Logger log = LoggerFactory.getLogger(Drizly.class);

    public OrderDeliveryEntity extractContent(Document doc) {
        Elements tables = doc.select("table");
        String orderId = getValue(tables, 2, "Drizly Order #:").trim();
        log.info("orderId\t = " + orderId);
        Integer quantity = getProductsCount(tables, 5);
        log.info("quantity\t = " + quantity);
        String subTotal = getValue(tables, 6, "Total").trim();
        log.info("subTotal\t = " + subTotal);
        String tip = getValue(tables, 6, "Tip").trim();
        log.info("Tip\t = " + tip);
        String fullName = getValue(tables, 4, "username").trim();
        String firstName = fullName.split(" ")[0];
        String lastName = fullName.replace(firstName, "");
        log.info("fullName\t = " + fullName);
        String zipcode = getValue(tables, 4, "code").trim();
        log.info("zip code\t = " + zipcode);
        String phone = getValue(tables, 4, "phone").trim();
        log.info("Phone Number\t = " + phone);
        String address = getValue(tables, 4, "deliverydress").trim().replace(fullName, "").replace("Phone: " + phone, "");
        log.info("Delivery Address\t = " + address);
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

    public static String getValue(Elements tables, int tableOrder, String check) {
        String result = "";
        Elements tableRows = tables.get(tableOrder).select("tr");
        for (Element tableRow : tableRows) {
            Elements tableData = tableRow.select("td");
            if (tableData.text().contains("Drizly Order #:") && check.equals("Drizly Order #:")) {
                String element = tableData.text().substring(tableData.text().indexOf("#:") + 2, tableData.text().length() - 1).trim();
                result = element.substring(0, element.indexOf(" ")).trim();
            } else if (tableData.text().startsWith("Store Total:") && check.equals("Total")) {
                String element = tableData
                    .text()
                    .substring(tableData.text().indexOf("Store Total:") + 12, tableData.text().length())
                    .trim();
                result = element.substring(0, element.length()).trim();
            } else if (tableData.text().startsWith("Tip") && check.equals("Tip") && tableData.text().indexOf("$") != -1) {
                String element = tableData.text().substring(tableData.text().indexOf("$") - 1, tableData.text().length()).trim();
                result = element.substring(0, element.length()).trim();
            } else if (tableData.text().startsWith("Delivery Address: ") && check.equals("username")) {
                result = tableData.select("td,br").get(0).childNodes().get(4).toString();
            } else if (tableData.text().startsWith("Delivery Address: ") && check.equals("code")) {
                for (Node node : tableData.select("td,br").get(0).childNodes()) {
                    String input = pinCodeChanges(node.outerHtml());
                    if (input != null && input.length() >= 5) {
                        result = input;
                    }
                }
            } else if (tableData.text().startsWith("Delivery Address: ") && tableData.text().contains("Phone: ") && check.equals("phone")) {
                String element = tableData.text().substring(tableData.text().indexOf("Phone:") + 6, tableData.text().length()).trim();
                result = element.substring(0, element.indexOf(" ")).trim();
            } else if (tableData.text().startsWith("Delivery Address: ") && check.equals("deliverydress")) {
                result = tableData.select("td,br").get(0).text().replace("Delivery Address: ", "");
            }
        }
        return result;
    }

    public static Integer getProductsCount(Elements tables, int tableOrder) {
        Integer count = 0;
        Elements tableRows = tables.get(tableOrder).select("tr");
        for (Element tableRow : tableRows) {
            Elements tableData = tableRow.select("td");
            if (tableData.text().contains(" x")) {
                String cnt = tableData.text().substring(0, tableData.text().indexOf(" x"));
                count = count + Integer.parseInt(cnt);
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
