package com.io.dapr.payment_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookTicketEvent {

    private String eventType;
    private String orderId;
    private String ticketType;
    private String itemName;
    private String seatNumber;
    private Double amount;
    private String customerName;
    private String customerEmail;
}
