package com.evf.mail.util;

public final class  PhoneFormatter {
	
	private final  String phoneNumber; 
	
	public PhoneFormatter(String phoneNumber){
		this.phoneNumber=phoneNumber;
	}

	public String getPhoneNumber() {
		StringBuffer finalPhoneNumber=new StringBuffer();
		for(int i=0;i<phoneNumber.length();i++){
			if(i==0){
				finalPhoneNumber.append("(");
			}else if(i==3){
				finalPhoneNumber.append(") ");
			}else if(i==6){
				finalPhoneNumber.append("-");
			}
			finalPhoneNumber.append(phoneNumber.charAt(i));
		}
		return finalPhoneNumber.toString();
	}
	
	
	
	

}
