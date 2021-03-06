package com.memoryleak.bloodbank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.memoryleak.bloodbank.config.View;
import org.hibernate.proxy.HibernateProxyHelper;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "user_table")
public class User implements Serializable {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @JsonView(View.Private.class)
    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @JsonIgnore
    @Column(nullable = false)
    private Boolean active = false;

    @JsonIgnore
    @Column(nullable = false)
    private Boolean banned = false;

    @JsonIgnore
    @Column(nullable = false)
    private String role;

    @OneToOne
    @JoinColumn(name = "location_id", unique = true)
    @JsonView(View.Private.class)
    private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || id == null) return false;
        Class<?> objClass = HibernateProxyHelper.getClassWithoutInitializingProxy(o);
        if (this.getClass() != objClass) return false;

        return id.equals(((User) o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
