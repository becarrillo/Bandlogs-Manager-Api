package com.api.bandlogs_manager.abstracts;

import com.api.bandlogs_manager.enums.Pitch;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public abstract class Chord {
    public Pitch pitch;

    /** e.g.: 'Minor', 'Maj7', 'add9' or another anything the band requires to set */
    public String suffix;
    
    public abstract void transport(int comparison);
}
