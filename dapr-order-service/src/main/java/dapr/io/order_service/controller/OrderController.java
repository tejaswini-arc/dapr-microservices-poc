package dapr.io.order_service.controller;

import dapr.io.order_service.model.BookingRequest;
import dapr.io.order_service.model.OrderResponse;
import dapr.io.order_service.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService orderService) {this.orderService = orderService;}


    // Create a new booking. POST /api/bookings
    @PostMapping
    public ResponseEntity<OrderResponse> createBooking(@RequestBody BookingRequest request) {
        OrderResponse order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }



    /*// Dapr → Order Service  Dapr calls this endpoint when ticket.booked is consumed from Kafka.
      @PostMapping("/events/book-ticket")
    public ResponseEntity<Void> handleTicketBookedEvent(@RequestBody TicketBookedEvent event) {
        orderService.processTicketBookedEvent(event);
        return ResponseEntity.ok().build();
    }*/

     // Get booking status.
     @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getBooking(@PathVariable String orderId) {
        OrderResponse order = orderService.getOrder(orderId);
         //Order does not exist.
        if (order == null) {
            return ResponseEntity.notFound().build();        }
        // Return existing order.
         return ResponseEntity.ok(order);
    }
}