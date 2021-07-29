package com.memoryleak.bloodbank.service;

import com.memoryleak.bloodbank.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    PostRepository repository;


}
