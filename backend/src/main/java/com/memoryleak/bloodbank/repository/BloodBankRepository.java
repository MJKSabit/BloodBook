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

    @Query("SELECT bb FROM BloodBank bb WHERE " +
            "(bb.user.location.latitude-:lat)*(bb.user.location.latitude-:lat) + " +
            "(bb.user.location.longitude-:lng)*(bb.user.location.longitude-:lng) <= "
            +LAT_LONG_RADIUS_SQUARE+" ")
    List<BloodBank> exploreNearbyBloodBank(@Param("lat") double latitude,
                                           @Param("lng") double longitude);

    BloodBank findBloodBankByUserUsernameIgnoreCase(String username);
}
