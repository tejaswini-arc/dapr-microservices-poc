package com.io.dapr.payment_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentNotificationRequest {

    private String orderId;
    private String customer;
    private String email;
    private String seat;
    private BigDecimal amount;
}
