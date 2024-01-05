package com.example.ecomerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Address for the user to be billed/delivered to.
 */
@Entity
@Table(name = "address")
public class Address {

    /** Unique id for the address. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    /** The first line of address. */
    @Column(name = "address_line_1", nullable = false, length = 512)
    private String addressLine1;
    /** The second line of address. */
    @Column(name = "address_line_2", length = 512)
    private String addressLine2;
    /** The city of the address. */
    @Column(name = "city", nullable = false)
    private String city;
    /** The country of the address. */
    @Column(name = "country", nullable = false, length = 75)
    private String country;
    /** The user the address is associated with. */
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private LocalUser user;

    /**
     * Gets the country.
     * @return The country.
     */
    public LocalUser getUser() {
        return user;
    }

    /**
     * Sets the country.
     * @param user The country.
     */
    public void setUser(LocalUser user) {
        this.user = user;
    }

    /**
     * Gets the country.
     * @return The country.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country.
     * @param country The country.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the city.
     * @return The city.
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city.
     * @param city The city.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the second line of address.
     * @return The second line of address.
     */
    public String getAddressLine2() {
        return addressLine2;
    }

    /**
     * Sets the second line of address.
     * @param addressLine2 The second line of address.
     */
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    /**
     * Gets the first line of address.
     * @return The first line of address.
     */
    public String getAddressLine1() {
        return addressLine1;
    }

    /**
     * Sets the first line of address.
     * @param addressLine1 The first line of address.
     */
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    /**
     * Gets the unique id.
     * @return The unique id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique id.
     * @param id The unique id.
     */
    public void setId(Long id) {
        this.id = id;
    }

}