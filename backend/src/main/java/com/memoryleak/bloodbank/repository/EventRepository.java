package com.memoryleak.bloodbank.repository;

import com.memoryleak.bloodbank.model.BloodBank;
import com.memoryleak.bloodbank.model.Event;
import com.memoryleak.bloodbank.model.GeneralUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface EventRepository extends PagingAndSortingRepository<Event, Long> {

    Slice<Event> findByUser(BloodBank user, Pageable pageable);

}
