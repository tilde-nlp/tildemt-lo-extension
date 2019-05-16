package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.libreoffice.example.comp.TildeTranslatorImpl;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
@RunWith(MockitoJUnitRunner.class)
class TildeTranslatorImplTest {

	@Test
//	@DisplayName("test1")
	void testSupportsService() {
		TildeTranslatorImpl spi = new TildeTranslatorImpl(null);
		String sService = "org.libreoffice.example.TildeTranslator";
		assertEquals(true, spi.supportsService(sService));
	}

	@Test
	void testTrigger() {
		TildeTranslatorImpl mStart = Mockito.mock(TildeTranslatorImpl.class);
//		mStart.trigger("actionOne");
		TildeTranslatorImpl test = new TildeTranslatorImpl(null);
		//...
		fail("Not yet implemented");
	}




}
