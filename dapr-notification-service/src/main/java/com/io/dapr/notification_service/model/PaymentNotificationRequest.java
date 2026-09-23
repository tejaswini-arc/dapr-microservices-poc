package com.io.dapr.notification_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentNotificationRequest {

    private String orderId;

    private String customerName;

    private String customerEmail;

    private String itemName;

    private String seatNumber;

    private BigDecimal amount;

    private String transactionId;

    private String paymentStatus;

}


