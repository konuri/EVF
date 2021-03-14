package com.evf.mail.tempaltes;

import java.util.Arrays;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.evf.mail.domain.OrderDeliveryEntity;

public class DeliveryLogic {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	
	public static OrderDeliveryEntity  mainLogic(Document doc){
		
		String deliverTo=doc.getElementById("CUSTOMER-INFO-AND-SPECIAL-INSTRUCTIONS").select("td").first().text();
		String nameAndAddressAndPhone=deliverTo.substring(0, deliverTo.indexOf("(Order placed:")).replace("(Order placed:", "").trim();
		String phone = nameAndAddressAndPhone.substring(nameAndAddressAndPhone.length() - 12).trim();
		String nameAndAddress=nameAndAddressAndPhone.replace(phone, "").trim();
		String fullName= nameAndAddress.substring(0, nameAndAddress.indexOf("(")).trim();
		String address= nameAndAddress.substring(nameAndAddress.indexOf(")"), nameAndAddress.length()).replace(")", "").trim();
		Elements qtyHtml=doc.getElementById("ITEMS-TABLE").select("tbody").select("tr");
		Long quantity=null;
		for(int i=1;i<qtyHtml.size();i++){
			if(quantity==null){
			quantity=Long.parseLong(qtyHtml.select("td").first().text().toString());
			}else{
				quantity=quantity+Long.parseLong(qtyHtml.select("td").first().text().toString());
			}
		}
		 List<String> billInfoKeys=Arrays.asList((doc.getElementById("MERCHANT-RECEIVES-LABELS").select("td").text().split(":")));
		 billInfoKeys.replaceAll(String::trim);
		 List<String> billInfoValues=Arrays.asList((doc.getElementById("MERCHANT-RECEIVES-VALUES").select("td").text().split("\\s")));
		 billInfoValues.replaceAll(String::trim);
		 System.out.println(billInfoValues);
		 
		String subTotal=billInfoValues.get(billInfoKeys.indexOf("Subtotal"));
		String tip="0";
		if(billInfoValues.contains("Delivery fee")){
			tip=billInfoValues.get(billInfoKeys.indexOf("Delivery fee"));
		};
		
		String orderInfo=doc.getElementById("DCOM-LOGO-AND-MERCHANT-INFO").select("tr").select("td").last().text();
		String orderId=orderInfo.substring(orderInfo.indexOf("Order"),orderInfo.length()).replace("Order", "").trim();    
		
		String zipcode=address.substring(address.lastIndexOf(' '), address.length()).trim();
		String firstName=fullName.split(" ")[0];
		String lastName=fullName.replace(firstName, "");
		 System.out.println("fullName :: "+fullName);
		 System.out.println("Address :: "+address);
		 System.out.println("Phone :: "+phone);
		 System.out.println("quantity :: "+quantity);
		 System.out.println("subTotal :: "+subTotal);
		 System.out.println("tip :: "+tip);
		 System.out.println("zipcode :: "+zipcode);
		 System.out.println("orderId :: "+orderId);
		 
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
	
	public  void deliveryLogic(String result){
		
		String nameAndAddressAndPhone=result.substring(result.indexOf("Deliver ASAP Prepaid (Do not collect payment)"), result.indexOf("(Order placed:")).replace("Deliver ASAP Prepaid (Do not collect payment)", "").trim();
		String phonevalue = nameAndAddressAndPhone.substring(nameAndAddressAndPhone.length() - 12).trim();
		String nameAndAddress=nameAndAddressAndPhone.replace(phonevalue, "").trim();
		String fullName= nameAndAddress.substring(0, nameAndAddress.indexOf("(")).trim();
		String address= nameAndAddress.substring(nameAndAddress.indexOf(")"), nameAndAddress.length()-1).replace(")", "").trim();
		
		List<String> keyDriverTip=Arrays.asList(result.substring(result.indexOf("Subtotal:"), result.indexOf("Merchant receives:")).trim().split(":"));
		keyDriverTip.replaceAll(String::trim);
		List<String> ValueDriverTip=Arrays.asList(result.substring(result.indexOf("Merchant receives:"), result.indexOf("Confirmation Code:")).replace("Merchant receives:", "").trim().split("\\s"));
		String subtotal=ValueDriverTip.get(keyDriverTip.indexOf("Subtotal"));
		String driverTip="0";

		if(keyDriverTip.contains("Delivery fee")){
			driverTip=ValueDriverTip.get(keyDriverTip.indexOf("Delivery fee"));
		};
		
		System.out.println("fullName :: "+fullName);
		System.out.println("Address :: "+address);
		System.out.println("Phone :: "+phonevalue);
		System.out.println("subtotal :: "+subtotal);
		System.out.println("driverTip :: "+driverTip);

		//System.out.println("quantity :: "+quantity);
	}

}