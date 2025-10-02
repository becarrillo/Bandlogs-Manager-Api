package com.api.bandlogs_manager.repository;

import com.api.bandlogs_manager.entities.Band;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@Repository
public interface BandRepository extends JpaRepository<Band, Short> {
    List<Band> findByDirector(String director);

    // Search band by name to uppercase and trim
    @Query("SELECT b FROM Band b WHERE LOWER(TRIM(b.name)) LIKE %:name%")
    List<Band> findByNameContaining(@Param("name") String name);
}
