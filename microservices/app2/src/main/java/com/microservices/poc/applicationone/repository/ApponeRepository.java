package com.microservices.poc.applicationone.repository;

import com.microservices.poc.applicationone.domain.Appone;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Appone entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ApponeRepository extends JpaRepository<Appone, Long> {
    @Query("select appone from Appone appone where appone.user.login = ?#{principal.preferredUsername}")
    List<Appone> findByUserIsCurrentUser();
}
