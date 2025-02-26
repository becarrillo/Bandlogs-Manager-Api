package com.api.bandlogs_manager.repository;

import com.api.bandlogs_manager.entities.Band;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@Repository
public interface BandRepository extends JpaRepository<Band, Short> {
    Band findByName(String name);
}
