
import java.util.List;

import com.api.bandlogs_manager.dtos.TonalityDTO;
import com.api.bandlogs_manager.entities.Song;
import com.api.bandlogs_manager.enums.Pitch;

public class TestSongService {
    private Song song;    

    public TestSongService(Song song) {
        this.song = song;
    }

    public Song transportSong(TonalityDTO newTonality) {
        final int originPitchOrdinal = song.getPitch().ordinal();
        final int destPitchOrdinal = newTonality.pitch.ordinal();

        int semitones;
        if (destPitchOrdinal < originPitchOrdinal) { // Calculate the difference between chord enums ordinal
            semitones = Math.negateExact(originPitchOrdinal - destPitchOrdinal);
        } else {
            semitones = destPitchOrdinal - originPitchOrdinal;
        }

        final List<String> progression = song.getProgression();
        for (int i=0; i<progression.size(); i++) {
            TonalityDTO tonality;

            final String[] progressionSplit = progression.get(i).split(";");
            if (progressionSplit.length<2) {
                tonality = TonalityDTO.builder()
                        .pitch(Pitch.valueOf(progressionSplit[0]))// set pitch that is just before ';' chord string separator
                        .suffix("")  // set pitch to an empty string because originally chord doesn't have suffix
                        .build();
            } else {
                tonality = TonalityDTO.builder()
                        .pitch(Pitch.valueOf(progressionSplit[0]))// set pitch that is just before ';' chord string separator
                        .suffix(progressionSplit[1])  // set pitch that is just after '-' chord string separator
                        .build();
            }

            tonality.transport(semitones);  // set pitch to current as new song tonality comparison requires
            progression.set(i, tonality.pitch.toString().concat(";"+tonality.suffix));
        }

        song.setPitch(newTonality.pitch);
        song.setProgression(progression);
        return song;
    }
}
