package com.api.bandlogs_manager;

import org.mockito.Mock;
import static org.mockito.BDDMockito.*;

import com.api.bandlogs_manager.entities.Song;

import com.api.bandlogs_manager.dtos.TonalityDTO;

import com.api.bandlogs_manager.enums.Pitch;

import com.api.bandlogs_manager.services.TestSongService;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class BandlogsManagerApplicationTests {
	@Mock
	private Song song;

	@InjectMocks TestSongService songService;
	
	@Test
	void songTransportationTest() {
		final List<String> progression = List
			.of("C;", "F;", "G;7", "C;Maj7", "E;9", "A;7", "D;m", "C;");
		
		// given
		given(song.getSongId()).willReturn(7);
		given(song.getTitle()).willReturn("Amapola");
		given(song.getPitch()).willReturn(Pitch.C);
		given(song.getTonalitySuffix()).willReturn("");
		given(song.getProgression()).willReturn(progression);
		
		final TonalityDTO newTonality = TonalityDTO.builder()
				.pitch(Pitch.G_SHARP)
				.suffix("")
				.build();
		final Song expectedSong = new Song(
			7,
			"Amapola",
			newTonality.pitch,
			"",
			List.of("G_SHARP;", "C_SHARP;", "D_SHARP;7", "G_SHARP;Maj7", "C;9", "F;7", "A_SHARP;m", "G_SHARP;")
		);

		// when
		final Song transportedSong = this.songService.transportSong(newTonality);

		// then
		Assertions.assertEquals(expectedSong, transportedSong, "prueba fallida: en la progresión no coincidieron los acordes actuales con los transportados");
	}
}
