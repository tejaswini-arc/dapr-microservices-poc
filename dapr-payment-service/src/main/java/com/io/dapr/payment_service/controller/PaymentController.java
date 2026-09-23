package com.io.dapr.payment_service.controller;

import com.io.dapr.payment_service.model.BookTicketEvent;
import com.io.dapr.payment_service.model.PaymentResponse;
import com.io.dapr.payment_service.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger log =LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;

    public PaymentController(
            PaymentService notificationService) {
        this.paymentService = notificationService;
    }

    @PostMapping
    public String makePayment() {

        System.out.println("Payment received");
        String notificationResponse = paymentService.sendNotification();
        System.out.println("Notification response: " + notificationResponse);
        return "Payment processed successfully. "
                + notificationResponse;
    }

    /*
     * Dapr calls this endpoint when
     * book.ticket is received from Kafka.
     *
     * Kafka
     *   ↓
     * Payment Dapr
     *   ↓
     * POST /api/payments/events/book-ticket
     */
    @PostMapping("/events/book-ticket")
    public ResponseEntity<String> receiveBookTicket(@RequestBody String payload) {
        log.info("Received book.ticket event from Dapr");
        paymentService.processBookTicketEvent(payload);
        return ResponseEntity.ok("SUCCESS");
    }
}