package com.evf.mail.tempaltes;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import com.evf.mail.domain.OrderDeliveryEntity;

public class Mercato {

	public static OrderDeliveryEntity getMercatoDetails(Document document) {
		List<Node> tables = document.select("body,br").get(0).childNodes();
		String orderId = getValue(tables, " Order Number:").trim();
		System.out.println("Order Number\t = " + orderId);
		Integer quantity = getProductsCount(tables,  " Quantity:");
		System.out.println("quantity\t = " + quantity);
		String subTotal = getValue(tables, " Item Total:").trim();
		System.out.println("subTotal\t = " + subTotal);
		String tip = getValue(tables, " Delivery Tip:").trim();;
		System.out.println("Tip\t = " + tip);
		String fullName = getValue(tables, " Customer Name:").trim();
		String firstName=fullName.split(" ")[0];
		String lastName=fullName.replace(firstName, "");
		System.out.println("fullName\t = " + fullName);
		String phone = getValue(tables, " Customer Phone:").trim();
		System.out.println("Phone Number\t = " + phone);
		String deliveryTo = document.text().substring(document.text().indexOf(" Delivery to:") + " Delivery to:".length(), document.text().indexOf("Items in your order:")).trim();
		String zipcode = pinCodeChanges(deliveryTo.substring(deliveryTo.lastIndexOf(",") + 1, deliveryTo.length()).trim());
		System.out.println("zip code\t = " + zipcode);
		String address = deliveryTo.replace(" Delivery to:", "").replace(fullName, "");
		System.out.println("address \t = " + address);
		
		
		OrderDeliveryEntity orderDeliveryEntity=new OrderDeliveryEntity();
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
				String element = text
						.substring(text.indexOf(check) + check.length(), text.length()).trim();
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
				String element = text
						.substring(text.indexOf(check) + check.length(), text.length()).trim();
				count = count + Integer.parseInt(element.substring(0, element.length()).trim());
			}
		}
		return count;
	}
	
	public static String pinCodeChanges(String s) {
 		String pincode="";
		for(int i=0;i<s.length();i++)
		{
		   if(Character.isDigit(s.charAt(i))) {
			   pincode = pincode +s.charAt(i);
		   } else {
			   pincode = "";
		   }
		}
		return pincode;
	}
}
