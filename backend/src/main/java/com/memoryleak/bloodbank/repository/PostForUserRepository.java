package com.memoryleak.bloodbank.repository;

import com.memoryleak.bloodbank.model.GeneralUser;
import com.memoryleak.bloodbank.model.GeneralUserToPost;
import com.memoryleak.bloodbank.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostForUserRepository extends PagingAndSortingRepository<GeneralUserToPost, Long> {

    @Query("SELECT gutp.post FROM GeneralUserToPost gutp WHERE gutp.user = :g_user")
    Slice<Post> findAllPostForUser(@Param("g_user") GeneralUser user, Pageable pageable);

    List<GeneralUserToPost> findAllByPost(Post post);

}
