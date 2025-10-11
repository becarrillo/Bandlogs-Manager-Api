package com.api.bandlogs_manager;

import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.api.bandlogs_manager.entities.Song;

import com.api.bandlogs_manager.dtos.TonalityDTO;

import com.api.bandlogs_manager.enums.Pitch;

import com.api.bandlogs_manager.services.SongService;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
class BandlogsManagerApplicationTests {
	@Mock
	private Song song;

	@InjectMocks SongService songService;
	
	@Test
	void songTransposeTest() {
		final List<String> progression = List
			.of("C;", "F;", "G;7", "C;Maj7", "E;9", "A;7", "D;m", "C;");
		when(song.getSongId()).thenReturn(7);
		when(song.getTitle()).thenReturn("Amapola");
		when(song.getPitch()).thenReturn(Pitch.C);
		when(song.getTonalitySuffix()).thenReturn("");
		when(song.getProgression()).thenReturn(progression);
		
		final Song oldSong = new Song(
			7,
			"Amapola",
			Pitch.G_SHARP,
			"",
			List.of("G_SHARP;", "C_SHARP;", "D_SHARP;7", "G_SHARP;Maj7", "C;9", "F;7", "A_SHARP;m", "G_SHARP;")
		);

		final TonalityDTO newTonality = TonalityDTO.builder()
				.pitch(Pitch.C)
				.suffix(oldSong.getTonalitySuffix())
				.build();
		final Song transportedSong = this.songService.transportSong(oldSong, newTonality);

		Assertions.assertEquals(song, transportedSong, "prueba fallida: en la progresión no coincidieron los acordes actuales con los transportados");
	}
}
