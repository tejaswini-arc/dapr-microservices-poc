package com.io.dapr.notification_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {
    private String orderId;
    private String customerName;
    private String customerEmail;
    private String itemName;
    private String seatNumber;
    private Double amount;
    private String transactionId;
    private String paymentStatus;

}
