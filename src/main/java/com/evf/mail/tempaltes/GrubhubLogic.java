package com.evf.mail.tempaltes;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.evf.mail.domain.OrderDeliveryEntity;

public class GrubhubLogic {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	
	public static OrderDeliveryEntity logic(Document doc ){
		
		 String fullName=doc.select("div:contains(Deliver to:)").get(2).getAllElements().get(2).text();
		   String phone="";
		   String address="";
		   String subTotal="";
		   String tip="";
		   Long quantity=null;
		   String zipcode=null;
		   for( Element element : doc.getElementsByAttributeValue("data-section", "diner"))
		   {
			  phone=element.getElementsByAttributeValue("data-field", "phone").text();
			  address=element.getElementsByAttributeValue("data-field", "address1").text();
			  address=address+" "+element.getElementsByAttributeValue("data-field", "address2").text();
			  address=address+" "+element.getElementsByAttributeValue("data-field", "address2").text();
			  address=address+" "+element.getElementsByAttributeValue("data-field", "city").text();
			  address=address+" "+element.getElementsByAttributeValue("data-field", "state").text();
			  address=address+","+element.getElementsByAttributeValue("data-field", "zip").text();
			  zipcode=element.getElementsByAttributeValue("data-field", "zip").text();
		   }
			
		   
		   for( Element element : doc.getElementsByAttributeValue("data-section", "order"))
		   {
			   subTotal=element.getElementsByAttributeValue("data-field", "subtotal").text();
			  tip=element.getElementsByAttributeValue("data-field", "tip").text();
			  
		   }
		   
		   for( Element element : doc.getElementsByAttributeValue("data-section", "menu-item"))
		   {
			   if(quantity==null){
				   quantity=  Long.parseLong(element.getElementsByAttributeValue("data-field", "quantity").text());
			   }else{
				   quantity=quantity+Long.parseLong(element.getElementsByAttributeValue("data-field", "quantity").text()); 
			   }
		   }
		   String firstName=fullName.split(" ")[0];
			String lastName=fullName.replace(firstName, "");
		   System.out.println("Phone :: "+fullName);
		   System.out.println("Phone :: "+phone);
		   System.out.println("Address :: "+address);
		   System.out.println("subTotal :: "+subTotal);
		   System.out.println("tip :: "+tip);
		   System.out.println("quantity :: "+quantity);
		   
		   OrderDeliveryEntity orderDeliveryEntity=new OrderDeliveryEntity();
			orderDeliveryEntity.setFirstName(firstName);
			orderDeliveryEntity.setLastName(lastName);
			orderDeliveryEntity.setAddress(address);
			orderDeliveryEntity.setPhone(phone);
			orderDeliveryEntity.setQuantity(quantity.longValue());
			orderDeliveryEntity.setSubTotal(subTotal);
			orderDeliveryEntity.setTip(tip);
			//orderDeliveryEntity.setOrderId(orderId);
			orderDeliveryEntity.setZipcode(Long.parseLong(zipcode));
			
			return orderDeliveryEntity;
		   
	}

}
