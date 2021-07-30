package com.memoryleak.bloodbank.repository;

import com.memoryleak.bloodbank.model.GeneralUser;
import com.memoryleak.bloodbank.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PostRepository extends PagingAndSortingRepository<Post, Long> {

    Slice<Post> findByUser(GeneralUser user, Pageable pageable);

    Slice<Post> findByBloodGroup(String bloodGroup, Pageable pageable);

    Post findPostById(long id);
}
