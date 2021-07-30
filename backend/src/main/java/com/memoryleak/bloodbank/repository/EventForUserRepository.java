package com.memoryleak.bloodbank.repository;

import com.memoryleak.bloodbank.model.Event;
import com.memoryleak.bloodbank.model.GeneralUser;
import com.memoryleak.bloodbank.model.GeneralUserToEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface EventForUserRepository extends PagingAndSortingRepository<GeneralUserToEvent, Long> {

    @Query("SELECT gute.event FROM GeneralUserToEvent gute WHERE gute.user = :g_user")
    Slice<Event> findAllEventForUser(@Param("g_user")GeneralUser user, Pageable pageable);

}
