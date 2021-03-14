package com.evf.mail.tempaltes;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.evf.mail.domain.OrderDeliveryEntity;

public class BeerRightNow {
	
	public static OrderDeliveryEntity getBeerRightNowDetails(Document doc) {
		Elements tables = doc.select("table");
		String orderId = getValue(tables, 2, "Order Number:").trim();
		System.out.println("orderId\t = " + orderId);
		Integer quantity = getProductsCount(tables, 3);
		System.out.println("quantity\t = " + quantity);
		String subTotal = getValue(tables, 4, "Total").trim();
		System.out.println("subTotal\t = " + subTotal);
		String tip = getValue(tables, 4, "Tip").trim();;
		System.out.println("Tip\t = " + tip);
		String fullName = getValue(tables, 5, "username").trim();
		String firstName=fullName.split(" ")[0];
		String lastName=fullName.replace(firstName, "");
		System.out.println("fullName\t = " + fullName);
		String zipcode = getValue(tables, 5, "code").trim();
		System.out.println("zip code\t = " + zipcode);
		String phone = getValue(tables, 5, "phone").trim();
		System.out.println("Phone Number\t = " + phone);
		String address = getValue(tables, 5, "deliverydress").trim().replace(fullName, "").replace("Recip. Phone: "+phone, "");
		System.out.println("Delivery Address\t = " + address);
		
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
			if (tableData.text().contains("Order Number:") && check.equals("Order Number:")) {
				String element = tableData.text()
						.substring(tableData.text().indexOf(": ") + 1, tableData.text().length() - 1).trim();
				result = element.substring(0, element.indexOf(" ")).trim();
			} else if (tableData.text().startsWith("Total ") && check.equals("Total")) {
				String element = tableData.text()
						.substring(tableData.text().indexOf("Total ") + 5, tableData.text().length()).trim();
				result = element.substring(0, element.length()).trim();
			} else if (tableData.text().startsWith("Tip ") && check.equals("Tip")) {
				String element = tableData.text()
						.substring(tableData.text().indexOf("Tip ") + 3, tableData.text().length()).trim();
				result = element.substring(0, element.length()).trim();
			} else if (tableData.text().startsWith("Delivery Address ") && check.equals("username")) {
				result = tableData.select("p,br").get(1).childNodes().get(0).toString();
			} else if (tableData.text().startsWith("Delivery Address ") && check.equals("code")) {
				for(Node node: tableData.select("p,br").get(1).childNodes()) {
					String output = pinCodeChanges(node.outerHtml());
					if (output !=null && output.length() >= 5) {
						result = output;
						break;
					}
				}
			} else if (tableData.text().startsWith("Delivery Address ") && tableData.text().contains("Recip. Phone: ")
					&& check.equals("phone")) {
				result = tableData.select("p,br").get(1).childNodes().get(12).toString().split(":")[1];
				String element = tableData.text()
						.substring(tableData.text().indexOf("Recip. Phone: ") + 14, tableData.text().length()).trim();
				result = element.substring(0, element.indexOf(" ")).trim();
			} else if (tableData.text().startsWith("Delivery Address ")
					&& check.equals("deliverydress")) {
				result = tableData.select("p,br").get(1).text();
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
