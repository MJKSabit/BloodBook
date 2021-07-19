package com.memoryleak.bloodbank.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "general_user_id")
    private GeneralUser user;

    @Column(nullable = false, name = "post_info")
    private String info;

    @Column(nullable = false, name = "post_bg")
    private String bloodGroup;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date posted = new Date();

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date needed;

    @OneToOne
    @JoinColumn(name = "location_id")
    private Location location;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GeneralUser getUser() {
        return user;
    }

    public void setUser(GeneralUser user) {
        this.user = user;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public Date getPosted() {
        return posted;
    }

    public void setPosted(Date posted) {
        this.posted = posted;
    }

    public Date getNeeded() {
        return needed;
    }

    public void setNeeded(Date needed) {
        this.needed = needed;
    }
}
