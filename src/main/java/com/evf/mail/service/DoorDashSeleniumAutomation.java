package com.evf.mail.service;



import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.evf.mail.domain.OrderDeliveryEntity;

@Service
public class DoorDashSeleniumAutomation {
	
	
	private final Logger log = LoggerFactory.getLogger(DoorDashSeleniumAutomation.class);
	
	@Value("${key.name}")
	String userName;
	
	@Value("${key.value}")
	String password;
	public Boolean  doordashAutomation(OrderDeliveryEntity entity) throws InterruptedException {
		WebDriver driver=null;
		try{
			Resource   resource = new ClassPathResource("chromedriver.exe");
			System.setProperty("webdriver.chrome.driver", resource.getFile().getPath());
			driver=new ChromeDriver();
			driver.navigate().to("https://identity.doordash.com/auth?scope=*&response_type=code&redirect_uri=https%3A%2F%2Fwww.doordash.com%2Fdrive%2Fportal%2Fauth&state=%2Fdrive%2Fportal&prompt=none&client_id=1649316525849964797");  
		    waitForSeconds(5);
		    // Click on the search text box and send value  
		    driver.findElement(By.id("FieldWrapper-0")).sendKeys(userName);
		    driver.findElement(By.id("FieldWrapper-1")).sendKeys(password); 
		    driver.findElement(By.id("login-submit-button")).click();
		    waitForSeconds(15);
		    WebElement element=driver.findElement(By.xpath("//input[@class='_2BUB1nxRcbQTE9kL7JAY3F']")); 
		    element.sendKeys(entity.getAddress());
		    waitForSeconds(5);
		    element.sendKeys(Keys.DOWN);
		    element.sendKeys(Keys.RETURN);
		    if(entity.getApartment()!=null && !entity.getApartment().isEmpty() && entity.getApartment().length()>0){
		    driver.findElement(By.name("dropoffAddressSubpremise")).sendKeys(entity.getApartment());
		    }
		    driver.findElement(By.name("firstName")).sendKeys(entity.getFirstName());
		    driver.findElement(By.name("lastName")).sendKeys(entity.getLastName());
		    driver.findElement(By.name("businessName")).sendKeys(entity.getRegionCode());
		    driver.findElement(By.xpath("//input[@class='sc-iELTvK cbAqzK']")).sendKeys(entity.getPhone());
		    driver.findElement(By.name("orderSize")).sendKeys(entity.getSubTotal().replace("$", ""));
		    driver.findElement(By.name("driverTip")).sendKeys(entity.getTip().replace("$", ""));
		    driver.findElement(By.name("numItems")).sendKeys(entity.getQuantity().toString());
		    driver.findElement(By.xpath("//input[@name='paymentMethod']//following-sibling::div[contains(text(),'Invoice')]")).click();
		    
		   // driver.quit();
		}catch(Exception e){
			e.printStackTrace();
			log.error("Exception in doordashAutomation ::"+ ExceptionUtils.getStackTrace(e));;
			driver.quit();
			return false;
		}
	    return true;

	}
	public static void waitForSeconds(long sec) throws InterruptedException{
		Thread.sleep(sec *1000);
	}


}
