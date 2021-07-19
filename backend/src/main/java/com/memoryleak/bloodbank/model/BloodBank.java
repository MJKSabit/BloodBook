package com.memoryleak.bloodbank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "bloodbank_user")
public class BloodBank implements Serializable {

    @Id
    @OneToOne
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String imageURL;

    @Column(nullable = false)
    private String about = "";

    @Column(nullable = false)
    private String location;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BloodBank bloodBank = (BloodBank) o;
        return user.equals(bloodBank.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}
