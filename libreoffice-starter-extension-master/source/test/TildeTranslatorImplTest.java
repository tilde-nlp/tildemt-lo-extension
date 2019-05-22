package test;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.libreoffice.example.comp.TildeTranslatorImpl;

class TildeTranslatorImplTest {

	@Test
	void testSupportsService() {
		TildeTranslatorImpl spi = new TildeTranslatorImpl(null);
		String sService = "org.libreoffice.example.TildeTranslator";
		assertEquals(true, spi.supportsService(sService));
	}

}
