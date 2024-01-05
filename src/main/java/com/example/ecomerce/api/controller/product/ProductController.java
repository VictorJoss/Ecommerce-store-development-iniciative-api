package com.example.ecomerce.api.controller.product;

import com.example.ecomerce.model.Product;
import com.example.ecomerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller to handle the creation, updating & viewing of products.
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {

    /** The Product Service. */
    private ProductService productService;

    /**
     * Constructor for spring injection.
     * @param productService
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Gets the list of products available.
     * @return The list of products.
     */
    @Operation(summary = "Get all products", description = "Gets all products.")
    @GetMapping
    public List<Product> getProducts(){
        return productService.getProducts();
    }
}
