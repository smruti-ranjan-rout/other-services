package org.egov.mr.consumer;

import java.util.HashMap;

import org.egov.mr.service.PaymentUpdateService;
import org.egov.mr.service.notification.PaymentNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReceiptConsumer {

    private PaymentUpdateService paymentUpdateService;

    private PaymentNotificationService paymentNotificationService;


    @Autowired
    public ReceiptConsumer(PaymentUpdateService paymentUpdateService , PaymentNotificationService paymentNotificationService) {
        this.paymentUpdateService = paymentUpdateService;
        this.paymentNotificationService = paymentNotificationService ;
    }



    @KafkaListener(topics = {"${kafka.topics.receipt.create}"})
    public void listenPayments(final HashMap<String, Object> record) {
        paymentUpdateService.process(record);
        paymentNotificationService.process(record);
    }
}
