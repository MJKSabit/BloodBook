package com.memoryleak.bloodbank.repository;

import com.memoryleak.bloodbank.model.GeneralUser;
import com.memoryleak.bloodbank.model.GeneralUserToPost;
import com.memoryleak.bloodbank.model.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostForUserRepository extends CrudRepository<GeneralUserToPost, Long> {

    List<GeneralUserToPost> getAllByUser(GeneralUser user);

}
