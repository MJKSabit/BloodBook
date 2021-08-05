package com.memoryleak.bloodbank.repository;

import com.memoryleak.bloodbank.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    User findUserByUsernameIgnoreCase(String username);

    int countUserByEmailOrUsernameIgnoreCase(String email, String username);

    Slice<User> findUserByActiveAndBannedAndRole(boolean active, boolean banned, String role, Pageable pageable);
}
