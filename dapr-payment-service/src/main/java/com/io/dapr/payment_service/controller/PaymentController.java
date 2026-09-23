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

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

   // Dapr calls this endpoint when book.ticket is received from Kafka.
     @PostMapping("/events/book-ticket")
    public ResponseEntity<String> receiveBookTicket(@RequestBody String payload) {
        log.info("Received book.ticket event from Dapr");
        paymentService.processBookTicketEvent(payload);
        return ResponseEntity.ok("SUCCESS");
    }
}