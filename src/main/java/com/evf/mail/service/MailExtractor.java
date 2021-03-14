package com.evf.mail.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.evf.mail.config.MailConfiguration;
import com.evf.mail.domain.OrderDeliveryEntity;
import com.evf.mail.repository.OrderDeliveryRepository;
import com.evf.mail.tempaltes.Drizly;

@Component
public class MailExtractor {
	
	@Autowired
	MailConfiguration mailConfiguration;
	
	@Autowired
	OrderDeliveryRepository orderDeliveryRepository;

	private final List<String> orderfrom=new ArrayList<>();
	
	private MailExtractor(){
		
	    orderfrom.add("Drizly");
	    orderfrom.add("Mercato");
	    orderfrom.add("Jon - BeerRightNow.com");
	    orderfrom.add("support@delivery.com");
	    orderfrom.add("Minibar Delivery");
	    orderfrom.add("orders@eat.grubhub.com");
	    orderfrom.add("orders@sharebite.com");
	}
	
	@Scheduled(fixedDelay = 300000, initialDelay = 1000)
	public void fixedDelaySch() {
		try {
		Store store=mailConfiguration.getMailSender();
		  Folder inbox = store.getFolder( "Notes" );
		 inbox.open( Folder.READ_ONLY );
		  // Fetch unseen messages from inbox folder
		    Message[] messages = inbox.search(
		        new FlagTerm(new Flags(Flags.Flag.SEEN), true));
		    for(Message message:messages){
		    String from=message.getFrom()[0].toString().split("<")[0].trim();
		    System.out.println(from);
		   String result= getTextFromMessage(message);
		   Document doc=Jsoup.parse(result);
		    if(from.equalsIgnoreCase("Drizly")){
		    	OrderDeliveryEntity orderDeliveryEntity=Drizly.getDrizlyDetails(doc);
		    	orderDeliveryEntity.setOrderFrom(from);
		    	orderDeliveryRepository.save(orderDeliveryEntity);
		    }
		    }
		   
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String getTextFromMessage(Message message) throws MessagingException, IOException {
	    String result = "";
	    if (message.isMimeType("text/plain")) {
	        result = message.getContent().toString();
	    } else if (message.isMimeType("multipart/*")) {
	        MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
	        result = getTextFromMimeMultipart(mimeMultipart);
	    }else if (message.isMimeType("text/html")) {
            String html = (String) message.getContent();
            result=html;
           // result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
        }
	    return result;
	}

	private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart)  throws MessagingException, IOException{
	    String result = "";
	    int count = mimeMultipart.getCount();
	    System.out.println("count=="+count);
	    for (int i = 0; i < count; i++) {
	        BodyPart bodyPart = mimeMultipart.getBodyPart(i);
	        if (bodyPart.isMimeType("text/plain")) {
	            result = result + "\n" + bodyPart.getContent();
	           // break; // without break same text appears twice in my tests
	        } else if (bodyPart.isMimeType("text/html")) {
	            String html = (String) bodyPart.getContent();
	            result = html;
	        } else if (bodyPart.getContent() instanceof MimeMultipart){
	            result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
	        }
	    }
	    return result;
	}
}
