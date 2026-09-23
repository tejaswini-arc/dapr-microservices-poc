package com.io.dapr.payment_service.service;

import com.io.dapr.payment_service.model.BookTicketEvent;
import com.io.dapr.payment_service.model.NotificationRequest;
import com.io.dapr.payment_service.model.PaymentNotificationRequest;
import com.io.dapr.payment_service.model.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public PaymentService(ObjectMapper objectMapper, RestClient restClient) {
        this.objectMapper = objectMapper;
        this.restClient = restClient;
    }

        // Processes book.ticket event received through Dapr Pub/Sub.
    public PaymentResponse processBookTicketEvent(String rawPayload) {
        log.info("BOOK.TICKET EVENT RECEIVED BY PAYMENT SERVICE");
        log.info("Raw payload received: {}", rawPayload);
        try {
            // 1. Read Dapr CloudEvent envelope
            JsonNode cloudEventNode = objectMapper.readTree(rawPayload);
            // 2. Extract actual payload from "data"
            JsonNode dataNode = cloudEventNode.get("data");
            if (dataNode == null) {
                throw new IllegalArgumentException("CloudEvent does not contain 'data'");
            }
            // 3. Convert "data" into BookTicketEvent
            BookTicketEvent eventData =objectMapper.treeToValue(dataNode,BookTicketEvent.class);
            // 4. Log booking information
            log.info("Parsed book.ticket event:");
            log.info("Order ID : {}", eventData.getOrderId());
            log.info("Customer : {}", eventData.getCustomerName());
            log.info("Email    : {}", eventData.getCustomerEmail());
            log.info("Item     : {}", eventData.getItemName());
            log.info("Seat     : {}", eventData.getSeatNumber());
            log.info("Amount   : {}", eventData.getAmount());

            // 5. Process payment
            log.info("Payment processing started for orderId={}",eventData.getOrderId());
            String transactionId ="TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String paymentStatus = "PAYMENT_SUCCESS";
            log.info("Payment processing completed for orderId={}",eventData.getOrderId());
            log.info("Transaction ID : {}", transactionId);
            log.info("Payment Status : {}", paymentStatus);

            // 6. Create PaymentResponse
            PaymentResponse paymentResponse = new PaymentResponse(eventData.getOrderId(),transactionId,paymentStatus);

            // 7. AFTER PAYMENT invoke Notification Service

            if ("PAYMENT_SUCCESS".equals(paymentStatus)) {
                log.info("Payment successful. Invoking Notification Service for orderId={}",eventData.getOrderId());
                sendPaymentConfirmation(eventData,paymentResponse);
            }
            // 8. Return payment response
            return paymentResponse;
        } catch (Exception e) {
            log.error( "Failed to process book.ticket event",e);
            throw new RuntimeException("Payment processing failed",e);
        }
    }



    private void sendPaymentConfirmation(BookTicketEvent eventData,PaymentResponse paymentResponse) {
        try {
            // 1. Build notification request
                        NotificationRequest notificationRequest = new NotificationRequest(
                            eventData.getOrderId(),
                            eventData.getCustomerName(),
                            eventData.getCustomerEmail(),
                            eventData.getItemName(),
                            eventData.getSeatNumber(),
                            eventData.getAmount(),
                            // Payment information
                            paymentResponse.getTransactionId(),
                            paymentResponse.getStatus()
                    );
            // 2. Log EXACT request being sent to notification-service

            log.info("=================================================");
            log.info("PAYMENT -> NOTIFICATION SERVICE");
            log.info("=================================================");
            log.info("Order ID       : {}",notificationRequest.getOrderId());
            log.info("Customer       : {}",notificationRequest.getCustomerName());
            log.info("Email          : {}",notificationRequest.getCustomerEmail());
            log.info("Item           : {}",notificationRequest.getItemName());
            log.info("Seat           : {}",notificationRequest.getSeatNumber());
            log.info("Amount         : {}",notificationRequest.getAmount());
            log.info( "Transaction ID : {}",notificationRequest.getTransactionId());
            log.info("Payment Status : {}", notificationRequest.getPaymentStatus());


            // 3. Invoke Notification Service through Dapr
            String response =restClient.post().uri(
                                    "/v1.0/invoke/" +
                                            "notification-service" +
                                            "/method/api/notifications"
                            )
                            .body(notificationRequest)
                            .retrieve()
                            .body(String.class);

            // 4. Log successful response
            log.info("Notification Service invoked successfully");
            log.info("Notification Service response: {}",response);
            log.info("=================================================");

        } catch (Exception e) {
            log.error("Failed to invoke Notification Service for orderId={}",eventData.getOrderId(),e);
        }
    }


}