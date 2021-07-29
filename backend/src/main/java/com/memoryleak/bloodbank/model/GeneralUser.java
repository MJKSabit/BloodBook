package com.memoryleak.bloodbank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.memoryleak.bloodbank.config.View;

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
//    @JsonView(View.ExtendedPublic.class)
    private User user;

    @Column(nullable = false)
//    @JsonView(View.ExtendedPublic.class)
    private String name;

    @Column(nullable = true)
//    @JsonView(View.ExtendedPublic.class)
    private String imageURL;

    @Column(nullable = false)
    @JsonView(View.ExtendedPublic.class)
    private String bloodGroup;

    @Column(name = "last_donation")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(View.Private.class)
    private Date lastDonation;

    @JsonView(View.Private.class)
    @Column(nullable = false)
    private boolean isActiveDonor;

    @Column
    @JsonView(View.Private.class)
    private String facebook;

    @JsonView(View.ExtendedPublic.class)
    @Column(nullable = false)
    private String about = "";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public Date getLastDonation() {
        return lastDonation;
    }

    public void setLastDonation(Date lastDonation) {
        this.lastDonation = lastDonation;
    }

    @JsonView(View.Private.class)
    public boolean isActiveDonor() {
        return isActiveDonor;
    }

    public void setActiveDonor(boolean activeDonor) {
        isActiveDonor = activeDonor;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

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
