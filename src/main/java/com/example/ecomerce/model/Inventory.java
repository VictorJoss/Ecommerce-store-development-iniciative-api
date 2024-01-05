package com.example.ecomerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Inventory of a product that available for purchase.
 */
@Entity
@Table(name = "inventory")
public class Inventory {

    /** Unique id for the inventory. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    /** The product this inventory is of. */
    @JsonIgnore
    @OneToOne(optional = false, orphanRemoval = true)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;
    /** The quantity in stock. */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Gets the quantity.
     * @return The quantity.
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     * @param quantity The quantity.
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the product.
     * @return The product.
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Sets the product.
     * @param product The product.
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Gets the id.
     * @return The id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id.
     * @param id The id.
     */
    public void setId(Long id) {
        this.id = id;
    }

}
