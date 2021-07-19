package com.memoryleak.bloodbank.repository;

import com.memoryleak.bloodbank.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByUsernameIgnoreCase(String username);

    User findUserByEmailIgnoreCase(String email);
}
