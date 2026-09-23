package com.io.dapr.notification_service.service;

import com.io.dapr.notification_service.model.PaymentNotificationRequest;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailBindingService {

    private static final Logger log =
            LoggerFactory.getLogger(EmailBindingService.class);

    /*
     * ============================================================
     * DAPR EMAIL OUTPUT BINDING CONFIGURATION
     * ============================================================
     *
     * This name MUST match:
     *
     * metadata:
     *   name: email-binding
     *
     * in your Dapr SMTP component YAML.
     */
    private static final String BINDING_NAME = "email-binding";

    /*
     * SMTP binding supports the "create" operation.
     */
    private static final String BINDING_OPERATION = "create";

    /*
     * Dapr client used to communicate with the local
     * notification-service Dapr sidecar.
     */
    private final DaprClient daprClient;

    public EmailBindingService() {

        this.daprClient =
                new DaprClientBuilder()
                        .build();

        log.info("DaprClient initialized for EmailBindingService");
    }

    /**
     * Sends booking confirmation email using
     * Dapr SMTP Output Binding.
     *
     * Flow:
     *
     * Payment Service
     *       |
     *       | Dapr Service Invocation
     *       v
     * Notification Service
     *       |
     *       | Dapr Output Binding
     *       v
     * SMTP Server
     *       |
     *       v
     * Customer Email
     */
    public void sendBookingConfirmationEmail(
            PaymentNotificationRequest request) {

        log.info("=================================================");
        log.info("DAPR EMAIL OUTPUT BINDING");
        log.info("=================================================");

        // ---------------------------------------------------------
        // 1. Validate request
        // ---------------------------------------------------------

        if (request == null) {

            throw new IllegalArgumentException(
                    "Payment notification request cannot be null"
            );
        }

        if (request.getCustomerEmail() == null ||
                request.getCustomerEmail().isBlank()) {

            throw new IllegalArgumentException(
                    "Customer email cannot be null or empty"
            );
        }

        // ---------------------------------------------------------
        // 2. Log booking information
        // ---------------------------------------------------------

        log.info(
                "Order ID       : {}",
                request.getOrderId()
        );

        log.info(
                "Customer       : {}",
                request.getCustomerName()
        );

        log.info(
                "Email          : {}",
                request.getCustomerEmail()
        );

        log.info(
                "Item           : {}",
                request.getItemName()
        );

        log.info(
                "Seat           : {}",
                request.getSeatNumber()
        );

        log.info(
                "Amount         : {}",
                request.getAmount()
        );

        log.info(
                "Transaction ID : {}",
                request.getTransactionId()
        );

        log.info(
                "Payment Status : {}",
                request.getPaymentStatus()
        );

        // ---------------------------------------------------------
        // 3. Build HTML email body
        // ---------------------------------------------------------

        String emailBody =
                buildEmailBody(request);

        log.info(
                "Email body generated successfully"
        );

        // ---------------------------------------------------------
        // 4. Convert HTML String to UTF-8 bytes
        // ---------------------------------------------------------
        //
        // Dapr Java SDK supports invoking an output binding
        // using byte[] payload.
        //
        // The HTML content itself becomes the SMTP email body.
        //
        // ---------------------------------------------------------

        byte[] emailData =
                emailBody.getBytes(StandardCharsets.UTF_8);

        // ---------------------------------------------------------
        // 5. Build binding metadata
        // ---------------------------------------------------------
        //
        // IMPORTANT:
        //
        // emailTo  -> recipient
        // subject  -> email subject
        //
        // emailFrom should normally be configured in the
        // Dapr SMTP component YAML.
        //
        // Do NOT add "contentType" here because it is not an
        // SMTP binding metadata field.
        //
        // ---------------------------------------------------------

        Map<String, String> metadata =
                new HashMap<>();

        metadata.put(
                "emailTo",
                request.getCustomerEmail()
        );

        metadata.put(
                "subject",
                "Booking Confirmation - "
                        + request.getOrderId()
        );

        // ---------------------------------------------------------
        // 6. Invoke Dapr SMTP output binding
        // ---------------------------------------------------------

        try {

            log.info(
                    "Invoking Dapr SMTP output binding..."
            );

            log.info(
                    "Binding Name      : {}",
                    BINDING_NAME
            );

            log.info(
                    "Binding Operation : {}",
                    BINDING_OPERATION
            );

            log.info(
                    "Email To          : {}",
                    request.getCustomerEmail()
            );

            log.info(
                    "Email Subject     : Booking Confirmation - {}",
                    request.getOrderId()
            );

            log.info(
                    "Email Body Size   : {} bytes",
                    emailData.length
            );

            /*
             * Dapr Java SDK:
             *
             * invokeBinding(
             *     bindingName,
             *     operation,
             *     data,
             *     metadata
             * )
             *
             * The HTML email body is sent as byte[].
             */
            daprClient.invokeBinding(
                    BINDING_NAME,
                    BINDING_OPERATION,
                    emailData,
                    metadata
            ).block();

            // -----------------------------------------------------
            // 7. Success
            // -----------------------------------------------------

            log.info(
                    "Email successfully submitted to Dapr SMTP binding"
            );

            log.info(
                    "Email recipient : {}",
                    request.getCustomerEmail()
            );

            log.info(
                    "Order ID        : {}",
                    request.getOrderId()
            );

            log.info("=================================================");

        } catch (Exception e) {

            log.error(
                    "================================================="
            );

            log.error(
                    "FAILED TO SEND EMAIL USING DAPR SMTP BINDING"
            );

            log.error(
                    "Order ID        : {}",
                    request.getOrderId()
            );

            log.error(
                    "Email recipient : {}",
                    request.getCustomerEmail()
            );

            log.error(
                    "=================================================",
                    e
            );

            throw new RuntimeException(
                    "Email binding invocation failed",
                    e
            );
        }
    }

    /**
     * Builds the booking confirmation email.
     *
     * The returned content is HTML.
     */
    private String buildEmailBody(
            PaymentNotificationRequest request) {

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Booking Confirmation</title>
                </head>

                <body style="
                    font-family: Arial, Helvetica, sans-serif;
                    line-height: 1.5;
                    color: #333333;
                    margin: 0;
                    padding: 20px;
                    background-color: #f5f5f5;
                ">

                    <div style="
                        max-width: 650px;
                        margin: 0 auto;
                        background-color: #ffffff;
                        padding: 25px;
                        border-radius: 8px;
                    ">

                        <h2 style="
                            color: #2e7d32;
                            margin-top: 0;
                        ">
                            Booking Confirmed
                        </h2>

                        <p>
                            Dear <strong>%s</strong>,
                        </p>

                        <p>
                            Your booking has been successfully confirmed.
                        </p>

                        <h3>
                            Booking Details
                        </h3>

                        <table
                            border="1"
                            cellpadding="10"
                            cellspacing="0"
                            width="100%%"
                            style="
                                border-collapse: collapse;
                                border: 1px solid #cccccc;
                                background-color: #ffffff;
                            "
                        >

                            <tr>
                                <td style="
                                    font-weight: bold;
                                    width: 35%%;
                                    background-color: #f2f2f2;
                                ">
                                    Order ID
                                </td>

                                <td>
                                    %s
                                </td>
                            </tr>

                            <tr>
                                <td style="
                                    font-weight: bold;
                                    background-color: #f2f2f2;
                                ">
                                    Item
                                </td>

                                <td>
                                    %s
                                </td>
                            </tr>

                            <tr>
                                <td style="
                                    font-weight: bold;
                                    background-color: #f2f2f2;
                                ">
                                    Seat
                                </td>

                                <td>
                                    %s
                                </td>
                            </tr>

                            <tr>
                                <td style="
                                    font-weight: bold;
                                    background-color: #f2f2f2;
                                ">
                                    Amount
                                </td>

                                <td>
                                    ₹%s
                                </td>
                            </tr>

                            <tr>
                                <td style="
                                    font-weight: bold;
                                    background-color: #f2f2f2;
                                ">
                                    Transaction ID
                                </td>

                                <td>
                                    %s
                                </td>
                            </tr>

                            <tr>
                                <td style="
                                    font-weight: bold;
                                    background-color: #f2f2f2;
                                ">
                                    Payment Status
                                </td>

                                <td>
                                    %s
                                </td>
                            </tr>

                            <tr>
                                <td style="
                                    font-weight: bold;
                                    background-color: #f2f2f2;
                                ">
                                    Booking Status
                                </td>

                                <td>
                                    <strong>CONFIRMED</strong>
                                </td>
                            </tr>

                        </table>

                        <br>

                        <p>
                            Thank you for booking with us.
                        </p>

                        <p>
                            Regards,<br>
                            <strong>XYZ Ticket Booking.com</strong>
                        </p>

                    </div>

                </body>
                </html>
                """.formatted(
                safe(request.getCustomerName()),
                safe(request.getOrderId()),
                safe(request.getItemName()),
                safe(request.getSeatNumber()),
                safe(request.getAmount()),
                safe(request.getTransactionId()),
                safe(request.getPaymentStatus())
        );
    }

    /**
     * Prevents null values from appearing as the literal
     * "null" in the email.
     */
    private String safe(Object value) {

        return value == null
                ? ""
                : String.valueOf(value);
    }
}