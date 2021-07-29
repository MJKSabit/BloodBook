package com.memoryleak.bloodbank.repository;

import com.memoryleak.bloodbank.model.GeneralUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface GeneralUserRepository extends CrudRepository<GeneralUser, Long> {
    static final double LAT_LONG_RADIUS_SQUARE = (10.0 / 111) * (10.0 / 111); // 10 km

    GeneralUser findGeneralUserByUserUsernameIgnoreCase(String username);

    @Query("SELECT gu FROM GeneralUser gu where " +
            "gu.bloodGroup = :bloodgroup and " +
            "gu.isActiveDonor = TRUE and " +
            "gu.lastDonation <= :donationDate and " +
            "(gu.user.location.latitude-:lat)*(gu.user.location.latitude-:lat) + " +
            "(gu.user.location.longitude-:lng)*(gu.user.location.longitude-:lng) <= "
            +LAT_LONG_RADIUS_SQUARE+" ")
    List<GeneralUser> getMatchPostRequirement(
            @Param("bloodgroup") String bloodGroup,
            @Param("donationDate") Date lastDonationThreshold,
            @Param("lat") double latitude,
            @Param("lng") double longitude
    );
}
