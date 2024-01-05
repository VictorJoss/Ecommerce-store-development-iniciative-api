package com.example.ecomerce.api.controller.order;

import com.example.ecomerce.model.LocalUser;
import com.example.ecomerce.model.WebOrder;
import com.example.ecomerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller to handle requests to create, update and view orders.
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    /** The Order Service. */
    private OrderService orderService;

    /**
     * Constructor for spring injection.
     * @param orderService
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Endpoint to get all orders for a specific user.
     * @param user The user provided by spring security context.
     * @return The list of orders the user had made.
     */
    @Operation(summary = "Get all orders for a user", description = "Gets all orders for a user.")
    @GetMapping
    public List<WebOrder> getOrders(@AuthenticationPrincipal LocalUser user){
        return orderService.getOrders(user);
    }


}
