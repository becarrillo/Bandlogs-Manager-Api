package com.api.bandlogs_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.bandlogs_manager.entities.Song;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {
}
