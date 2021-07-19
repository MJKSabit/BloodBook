package com.memoryleak.bloodbank.model;

import javax.persistence.*;

@Entity
@Table(name = "bb_blood_count")
public class BloodBankBloodCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private BloodBank bloodBank;

    @Column(name = "bloodgroup", nullable = false)
    private String bloodGroup;

    @Column(name = "in_stock")
    private int inStock;
}
