package com.indivaragroup.jdt17.spring.rest.api.entity;

import jakarta.persistence.*;
import lombok.Data;

/*
ada 1 entitiy namanya customer ada id,name, email, phone, addres (kota),

tipe data id: uuid format strring
 */

@Entity
@Data
@Table(name = "mst_customer")
public class Customer {
    @Id
    @Column(name = "customer_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String customerId;

    private String name;

    private String email;

    private String phone;

    private String address;
}
