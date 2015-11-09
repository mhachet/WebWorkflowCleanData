package fr.bird.bloom.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BloomConfigTest {

	@Test
	public void should_read_property(){
		assertThat(BloomConfig.getProperty("db.user")).isEqualToIgnoringCase("bloom");
	}
}