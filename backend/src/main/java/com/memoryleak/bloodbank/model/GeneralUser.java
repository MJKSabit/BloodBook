package com.memoryleak.bloodbank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "general_user")
public class GeneralUser implements Serializable {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String imageURL;

    @Column(nullable = false)
    private String bloodGroup;

    @Column(name = "last_donation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastDonation;

    @Column(nullable = false)
    private boolean isActiveDonor;

    @Column
    private String facebook;

    @Column(nullable = false)
    private String about = "";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneralUser that = (GeneralUser) o;
        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}
