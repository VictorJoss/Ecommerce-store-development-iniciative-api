package com.example.ecomerce.model;

import jakarta.persistence.*;

/**
 * A product available for purchasing.
 */
@Entity
@Table(name = "product")
public class Product {

    /** Unique id for the product. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    /** The name of the product. */
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    /** The short description of the product. */
    @Column(name = "short_description", nullable = false)
    private String shortDescription;
    /** The long description of the product. */
    @Column(name = "long_description")
    private String longDescription;
    /** The price of the product. */
    @Column(name = "price", nullable = false)
    private Double price;
    /** The inventory of the product. */
    @OneToOne(mappedBy = "product", cascade = CascadeType.REMOVE, optional = false, orphanRemoval = true)
    private Inventory inventory;

    /**
     * Gets the inventory.
     * @return The inventory.
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Sets the inventory.
     * @param inventory The inventory.
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Gets the price.
     * @return The price.
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Sets the price.
     * @param price The price.
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Gets the long description.
     * @return The long description.
     */
    public String getLongDescription() {
        return longDescription;
    }

    /**
     * Sets the long description.
     * @param longDescription The long description.
     */
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    /**
     * Gets the short description.
     * @return The short description.
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * Sets the short description.
     * @param shortDescription The short description.
     */
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     * Gets the name.
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name The name.
     */
    public void setName(String name) {
        this.name = name;
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