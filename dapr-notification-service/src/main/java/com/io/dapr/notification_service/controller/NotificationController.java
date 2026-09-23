package com.io.dapr.notification_service.controller;
import com.io.dapr.notification_service.model.PaymentNotificationRequest;
import com.io.dapr.notification_service.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class NotificationController {

    private static final Logger log =  LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService notificationService;
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    @PostMapping("/notifications")
    public ResponseEntity<String> sendNotification(@RequestBody PaymentNotificationRequest request) {
        String response =notificationService.sendBookingConfirmation(request);
        return ResponseEntity.ok(response);
    }
}



