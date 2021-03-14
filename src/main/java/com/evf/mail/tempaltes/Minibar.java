package com.evf.mail.tempaltes;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.evf.mail.domain.OrderDeliveryEntity;

public class Minibar {

	public static OrderDeliveryEntity getMinibarDetails(Document doc) {
		Elements tables = doc.select("table");
		String orderId = getValue(tables, 8, "ORDER #:").trim();
		System.out.println("orderId\t = " + orderId);
		Integer quantity = getProductsCount(tables, 10);
		System.out.println("quantity\t = " + quantity);
		String subTotal = getValue(tables, 11, "Total").trim();
		System.out.println("subTotal\t = " + subTotal);
		String tip = getValue(tables, 11, "Tip").trim();;
		System.out.println("Tip\t = " + tip);
		String fullName = getValue(tables, 9, "username").trim();
		String firstName=fullName.split(" ")[0];
		String lastName=fullName.replace(firstName, "");
		System.out.println("fullName\t = " + fullName);
		String zipcode = getValue(tables, 9, "code").trim();
		System.out.println("zip code\t = " + zipcode);
		String phone = getValue(tables, 4, "phone").trim();
		System.out.println("Phone Number\t = " + phone);
		String address = getValue(tables, 4, "deliverydress").trim().replace(fullName, "").replace("Tel: "+phone, "");
		System.out.println(" Address\t = " + address);
		
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

	public static String getValue(Elements tables, int tableOrder, String check) {
		String result = "";
		Elements tableRows = tables.get(tableOrder).select("tr");
		for (Element tableRow : tableRows) {
			Elements tableData = tableRow.select("td");
			if (tableData.text().contains("ORDER #:") && check.equals("ORDER #:")) {
				String element = tableData.text()
						.substring(tableData.text().indexOf("#:") + 2, tableData.text().length() - 1).trim();
				result = element.substring(0, element.indexOf(" ")).trim();
			} else if (tableData.text().startsWith("Subtotal:") && check.equals("Total")) {
				String element = tableData.text()
						.substring(tableData.text().indexOf("Subtotal:") + 9, tableData.text().length()).trim();
				result = element.substring(0, element.indexOf(" ")).trim();
			} else if (tableData.text().contains("Tip:") && check.equals("Tip")) {
				String element = tableData.text()
						.substring(tableData.text().indexOf("Tip:") + 4, tableData.text().length()).trim();
				result = element.substring(0, element.indexOf(" ")).trim();
			} else if (tableData.text().startsWith("DELIVER TO: ") && check.equals("username")) {
				result = tableData.select("td,br").get(0).childNodes().get(3).toString();
			} else if (tableData.text().startsWith("DELIVER TO: ") && check.equals("code")) {
				for(Node node: tableData.select("td,br").get(0).childNodes()) {
					String input = pinCodeChanges(node.outerHtml());
					if (input !=null && input.length() >= 5) {
						result = input;
					}
				}
			} else if (tableData.text().startsWith("DELIVER TO: ") && tableData.text().contains("Tel: ")
					&& check.equals("phone")) {
				String element = tableData.text()
						.substring(tableData.text().indexOf("Tel:") + 4, tableData.text().length()).trim();
				result = element.substring(0, element.length()).trim();
			} else if (tableData.text().startsWith("DELIVER TO: ")
					&& check.equals("deliverydress")) {
				result = tableData.text().replace("DELIVER TO: ", "");
			}
		}
		return result;
	}

	public static Integer getProductsCount(Elements tables, int tableOrder) {
		Integer count = 0;
		Elements tableRows = tables.get(tableOrder).select("tr");
 		for (Element tableRow : tableRows) {
 			Elements tableData = tableRow.select("td");
			String quantity = tableData.get(0).text();
			if (quantity.matches("\\d+")) {
			  count = count + Integer.parseInt(quantity);
			}
		}
		return count;
	}
	
	public static  String pinCodeChanges(String s) {
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
