package com.evf.mail.service;

import com.evf.mail.config.MailConfiguration;
import com.evf.mail.domain.OrderDeliveryEntity;
import com.evf.mail.repository.OrderDeliveryRepository;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MailExtractor {
    private final Logger log = LoggerFactory.getLogger(MailExtractor.class);

    @Autowired
    MailConfiguration mailConfiguration;

    @Autowired
    OrderDeliveryRepository orderDeliveryRepository;

    @Autowired
    BeanFactory beanFactory;

    private final Map<String, String> templates = new HashMap<>();

    private MailExtractor() {
        templates.put("Drizly", "Drizly");
        templates.put("Mercato", "Mercato");
        templates.put("Jon - BeerRightNow.com", "BeerRightNow");
        templates.put("support@delivery.com", "delivery");
        templates.put("Minibar Delivery", "Minibar");
        templates.put("orders@eat.grubhub.com", "grubhub");
        templates.put("orders@sharebite.com", "sharebite");
    }

    @Scheduled(fixedDelay = 300000, initialDelay = 1000)
    public void fixedDelaySch() {
        try {
            Store store = mailConfiguration.getMailSender();
            Folder inbox = store.getFolder("Notes");
            inbox.open(Folder.READ_ONLY);
            // Fetch unseen messages from inbox folder
            Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), true));
            for (Message message : messages) {
                String from = message.getFrom()[0].toString().split("<")[0].trim();
                log.info(from);
                String result = getTextFromMessage(message);
                Document doc = Jsoup.parse(result);
                if (templates.containsKey(from)) {
                    try {
                        OrderDeliveryEntity orderDeliveryEntity = beanFactory
                            .getBean(templates.get(from), MailContentExtraction.class)
                            .extractContent(doc);
                        orderDeliveryEntity.setOrderFrom(from);
                        orderDeliveryRepository.save(orderDeliveryEntity);
                    } catch (BeanCreationException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        } else if (message.isMimeType("text/html")) {
            String html = (String) message.getContent();
            result = html;
            // result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
        }
        return result;
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        String result = "";
        int count = mimeMultipart.getCount();
        log.info("count=={}", count);
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                // break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = html;
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }
}
