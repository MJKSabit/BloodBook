package com.memoryleak.bloodbank.repository;

import com.memoryleak.bloodbank.model.BloodBank;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BloodBankRepository extends PagingAndSortingRepository<BloodBank, Long> {

    static final double LAT_LONG_RADIUS_SQUARE = (30.0 / 111) * (30.0 / 111); // 15 km

    @Query("SELECT bc.bloodBank FROM BloodBankBloodCount bc where " +
            "bc.bloodGroup = :bloodgroup and bc.inStock > 0 and " +
            "(bc.bloodBank.user.location.latitude-:lat)*(bc.bloodBank.user.location.latitude-:lat) + " +
            "(bc.bloodBank.user.location.longitude-:lng)*(bc.bloodBank.user.location.longitude-:lng) <= "
            +LAT_LONG_RADIUS_SQUARE+" ")
    List<BloodBank> getBloodBankMatchingRequirements(
            @Param("bloodgroup") String bloodGroup,
            @Param("lat") double latitude,
            @Param("lng") double longitude
    );

    BloodBank findGeneralUserByUserUsernameIgnoreCase(String bloodbank);
}
