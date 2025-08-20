package com.api.bandlogs_manager.dtos;

import com.api.bandlogs_manager.abstracts.Chord;
import com.api.bandlogs_manager.enums.Pitch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TonalityDTO extends Chord {
    public Pitch pitch;

    /** e.g.: 'Minor', 'Maj7', 'add9' or another anything the band requires to set */
    public String suffix;
    
    /** 
     * @param comparison is a sum or subtract operation number result between original
     * pitch enum ordinal and new pitch enum ordinal song tonality value
    **/
    @Override
    public void transport(int comparison) {
        Pitch[] values = Pitch.values();
        for (Pitch value : values) {
            int newOrdinal;
            newOrdinal = pitch.ordinal() + comparison;
            if (newOrdinal < 0) // while is subtracted, pitch enum numeric ordinal value reached beyond the first (0)
                newOrdinal = values.length - Math.negateExact(newOrdinal); // then continue subtracting at the last
            if (newOrdinal>=values.length)  // newOrdinal value is currently greather than length of all pitches array
                newOrdinal-=values.length;
            if (value.ordinal()==newOrdinal) {  // values ordinals are equals, then set pitch attribute with value
                pitch = value;
                return;
            }
        }
    }
}
