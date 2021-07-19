package com.memoryleak.bloodbank.repository;

import com.memoryleak.bloodbank.model.GeneralUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GeneralUserRepository extends CrudRepository<GeneralUser, Long> {
    GeneralUser findGeneralUserByUserUsername(String username);
}
