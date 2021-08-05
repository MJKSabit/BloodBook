package com.memoryleak.bloodbank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.memoryleak.bloodbank.config.View;
import org.hibernate.proxy.HibernateProxyHelper;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "general_user", indexes = {
        @Index(name = "g_user_bg__act__ld", columnList = "blood_group, is_active_donor, last_donation"),
        @Index(name = "g_user_u", columnList = "user_id", unique = true)
})
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

    @Column(nullable = false, name = "blood_group")
    @JsonView(View.ExtendedPublic.class)
    private String bloodGroup;

    @Column(name = "last_donation")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(View.Private.class)
    private Date lastDonation;

    @JsonView(View.Private.class)
    @Column(nullable = false, name = "is_active_donor")
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
    @JsonProperty(value="isActiveDonor")
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
        if (o == null || id == null) return false;
        Class<?> objClass = HibernateProxyHelper.getClassWithoutInitializingProxy(o);
        if (this.getClass() != objClass) return false;

        return user.equals(((GeneralUser) o).getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}
