package com.evf.mail.config;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class MailConfiguration {
 
 @Value("${mail.username}")
 private String username;
 
 @Value("${mail.password}")
 private String password;
    @Bean
    public Store getMailSender() {
    	Session session = Session.getDefaultInstance(new Properties( ));
	    Store store=null;;
		try {
			store = session.getStore("imaps");
			store.connect("imap.googlemail.com", 993, username, password);
			
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return store;
    }
    
}