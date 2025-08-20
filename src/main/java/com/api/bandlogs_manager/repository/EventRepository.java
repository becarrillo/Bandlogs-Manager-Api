package com.api.bandlogs_manager.repository;

import com.api.bandlogs_manager.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import java.util.List;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@Repository
public interface EventRepository extends JpaRepository<Event, String> {
    List<Event> findByDate(LocalDate date);
}
