package com.memoryleak.bloodbank.repository;

import com.memoryleak.bloodbank.model.GeneralUser;
import com.memoryleak.bloodbank.model.GeneralUserToPost;
import com.memoryleak.bloodbank.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostForUserRepository extends PagingAndSortingRepository<GeneralUserToPost, Long> {


//    @Query("SELECT gs.post FROM GeneralUserToPost gs WHERE GeneralUserToPost.user = :gs_user order by gs.id desc")
//    List<Post> getPostForUser(@Param("gs_user") GeneralUser user, Pageable pageable);

    Slice<GeneralUserToPost> findAllByUser(GeneralUser user, Pageable pageable);

}
