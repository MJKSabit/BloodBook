package com.memoryleak.bloodbank.repository;

import com.memoryleak.bloodbank.model.BloodBank;
import com.memoryleak.bloodbank.model.BloodBankBloodCount;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BloodBankBloodCountRepository extends CrudRepository<BloodBankBloodCount, Long> {
    List<BloodBankBloodCount> findAllByBloodBank(BloodBank bloodBank);
}
