package com.memoryleak.bloodbank.controller.data;

import com.memoryleak.bloodbank.model.BloodBank;
import com.memoryleak.bloodbank.model.Post;

import java.util.List;

public class PostDetails {
    Post post;
    List<BloodBank> bloodBanks;

    public PostDetails(Post post) {
        this.post = post;
    }

    public PostDetails(Post post, List<BloodBank> bloodBanks) {
        this.post = post;
        this.bloodBanks = bloodBanks;
    }

    public PostDetails() {
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public List<BloodBank> getBloodBanks() {
        return bloodBanks;
    }

    public void setBloodBanks(List<BloodBank> bloodBanks) {
        this.bloodBanks = bloodBanks;
    }
}
