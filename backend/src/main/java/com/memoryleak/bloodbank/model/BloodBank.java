package com.memoryleak.bloodbank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.memoryleak.bloodbank.config.View;
import org.hibernate.proxy.HibernateProxyHelper;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "bloodbank_user", indexes = {
        @Index(name = "bb_to_user", columnList = "user_id", unique = true)
})
public class BloodBank implements Serializable {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String imageURL;

    @Column(nullable = false)
    @JsonView(View.ExtendedPublic.class)
    private String about = "";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || id == null) return false;
        Class<?> objClass = HibernateProxyHelper.getClassWithoutInitializingProxy(o);
        if (this.getClass() != objClass) return false;

        return user.equals(((BloodBank) o).getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

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

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
