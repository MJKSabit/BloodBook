package com.memoryleak.bloodbank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "blood_count", indexes = {
        @Index(name = "bbb_count_bb", columnList = "bb_id"),
        @Index(name = "bbb_count_stock", columnList = "in_stock"),
        @Index(name = "bbb_count_bb__stock", columnList = "bb_id, in_stock", unique = true)
})
public class BloodBankBloodCount {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="bb_id", nullable = false)
    private BloodBank bloodBank;

    @Column(name = "bloodgroup", nullable = false)
    private String bloodGroup;

    @Column(name = "in_stock", nullable = false)
    private int inStock = 0;

    public BloodBankBloodCount(BloodBank bloodBank, String bloodGroup) {
        this.bloodBank = bloodBank;
        this.bloodGroup = bloodGroup;
    }

    public BloodBankBloodCount() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BloodBank getBloodBank() {
        return bloodBank;
    }

    public void setBloodBank(BloodBank bloodBank) {
        this.bloodBank = bloodBank;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public int getInStock() {
        return inStock;
    }

    public void setInStock(int inStock) {
        this.inStock = inStock;
    }
}
