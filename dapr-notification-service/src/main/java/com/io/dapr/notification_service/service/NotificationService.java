package com.io.dapr.notification_service.service;
import com.io.dapr.notification_service.model.PaymentNotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log =
            LoggerFactory.getLogger(NotificationService.class);

    private final EmailBindingService emailBindingService;

    public NotificationService(EmailBindingService emailBindingService) {
        this.emailBindingService = emailBindingService;
    }

    /**
     * Processes a notification request.
     */
    public String sendNotification(
            PaymentNotificationRequest request) {

        log.info("=================================================");
        log.info("NOTIFICATION SERVICE");
        log.info("=================================================");

        log.info("Notification received");
        log.info("Order ID : {}", request.getOrderId());
        log.info("Customer : {}", request.getCustomerName());
        log.info("Seat     : {}", request.getSeatNumber());
        log.info("Amount   : {}", request.getAmount());

        log.info("Sending booking notification for orderId={}",request.getOrderId() );

        // Actual email/SMS/push notification can be added later.

        log.info("Notification sent successfully for orderId={}",request.getOrderId());
        return "Notification sent successfully";
    }



   /* public String sendBookingConfirmation(
            PaymentNotificationRequest request) {

        log.info("==============================================");
        log.info("BOOKING CONFIRMATION");
        log.info("==============================================");

        log.info("Order ID : {}", request.getOrderId());
        log.info("Customer : {}", request.getCustomer());
        log.info("Email    : {}", request.getEmail());
        log.info("Seat     : {}", request.getSeat());
        log.info("Amount   : {}", request.getAmount());
        emailBindingService.sendBookingConfirmationEmail(request);

        log.info("Booking confirmation email triggered for orderId={}", request.getOrderId());
        return "Booking confirmation email triggered successfully";
    }*/


    public String sendBookingConfirmation(
            PaymentNotificationRequest request) {

        log.info("==============================================");
        log.info("BOOKING CONFIRMATION");
        log.info("==============================================");

        log.info("Order ID : {}", request.getOrderId());
        log.info("Customer : {}", request.getCustomerName());
        log.info("Email    : {}", request.getCustomerEmail());
        log.info("Seat     : {}", request.getSeatNumber());
        log.info("Amount   : {}", request.getAmount());

        emailBindingService.sendBookingConfirmationEmail(request);

        log.info(
                "Booking confirmation email triggered for orderId={}",
                request.getOrderId()
        );

        return "Booking confirmation email triggered successfully";
    }
}
