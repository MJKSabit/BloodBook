package com.memoryleak.bloodbank.repository;

import com.memoryleak.bloodbank.model.BloodBank;
import com.memoryleak.bloodbank.model.BloodBankBloodCount;
import org.hibernate.boot.model.source.spi.Sortable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BloodBankBloodCountRepository extends PagingAndSortingRepository<BloodBankBloodCount, Long> {
    List<BloodBankBloodCount> findAllByBloodBank(BloodBank bloodBank, Sort sortable);
}
