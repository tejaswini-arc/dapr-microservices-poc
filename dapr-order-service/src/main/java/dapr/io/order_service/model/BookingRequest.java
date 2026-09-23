package dapr.io.order_service.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {

    private String ticketType;
    private String itemName;
    private String seatNumber;
    private Double amount;
    private String customerName;
    private String customerEmail;
}