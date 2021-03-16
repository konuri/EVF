package com.evf.mail.service;

import com.evf.mail.domain.OrderDeliveryEntity;
import org.jsoup.nodes.Document;

public interface MailContentExtraction {
    OrderDeliveryEntity extractContent(Document doc);
}
