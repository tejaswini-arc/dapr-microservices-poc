package dapr.io.order_service.service;

import dapr.io.order_service.model.BookingRequest;
import dapr.io.order_service.model.OrderResponse;
import dapr.io.order_service.model.BookTicketEvent;
import org.springframework.stereotype.Service;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {

     // Temporary in-memory order storage.

    private final Map<String, OrderResponse> orders =  new ConcurrentHashMap<>();

     // Spring HTTP client. We use this to communicate with the Dapr sidecar over HTTP.     *
    private final RestClient restClient;
    private static final String DAPR_HTTP_PORT = "3502";  // Dapr sidecar HTTP port.Our Order Service Dapr sidecar will expose this port. */
    private static final String PUBSUB_NAME = "pubsub-kafka"; // Dapr Pub/Sub component name.
    private static final String TOPIC_NAME = "book.ticket";      //Kafka topic.

    public OrderService(RestClient.Builder restClientBuilder) {
       this.restClient = restClientBuilder.baseUrl("http://localhost:" + DAPR_HTTP_PORT ) .build();
    }

    /*
     * Create a new booking.
     */
    public OrderResponse createOrder(BookingRequest request) {
      // Generate Order ID.
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        // Create Order Response.
        OrderResponse order = new OrderResponse();
        order.setOrderId(orderId);
        order.setStatus("CREATED");          // Initial order status.
        order.setTicketType(request.getTicketType());
        order.setItemName(request.getItemName());
        order.setSeatNumber(request.getSeatNumber());
        order.setAmount(request.getAmount());
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        orders.put(orderId,order); //Store order.

         // Build business event.
        BookTicketEvent event = new BookTicketEvent(TOPIC_NAME,orderId,request.getTicketType(),
                        request.getItemName(),request.getSeatNumber(),request.getAmount(),request.getCustomerName(),request.getCustomerEmail());

         //Publish event to Kafka through Dapr Pub/Sub.
      publishTicketBookedEvent(event);

        //Return order to browser.
        return order;
    }



    // Publish ticket.booked event Using Dapr HTTP API.
    private void publishTicketBookedEvent(BookTicketEvent event) {
        String url = "/v1.0/publish/"+ PUBSUB_NAME + "/" + TOPIC_NAME;
        restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(event)
                .retrieve()
                .toBodilessEntity();
        System.out.println("========================================");
        System.out.println("Published event: ticket.booked");
        System.out.println("Order ID: "+ event.getOrderId());
        System.out.println( "========================================");
    }

     // Get existing order.
    public OrderResponse getOrder( String orderId) {
        return orders.get(orderId);
    }

    /*//  This method will be called by Dapr when Kafka sends ticket booked back to the Order Service.

    public void processTicketBookedEvent(TicketBookedEvent event) {
        System.out.println( "========================================");
        System.out.println("Received ticket.booked event");
        System.out.println("Order ID: " + event.getOrderId());
        System.out.println("Customer: "+ event.getCustomerName());
        System.out.println("Movie/Route: " + event.getItemName());
        System.out.println("Seat: " + event.getSeatNumber());
        System.out.println("Amount: ₹"+ event.getAmount());
        System.out.println("========================================");


        *//*
         * For now we only demonstrate that the event was received.Payment processing will be added in the next stage.
         *//*
    }*/
}