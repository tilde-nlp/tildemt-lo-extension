package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.libreoffice.example.comp.StarterProjectImpl;
import org.mockito.junit.MockitoJUnitRunner;
@RunWith(MockitoJUnitRunner.class)
class StarterProjectImplTest {

	@Test
//	@DisplayName("test1")
	void testSupportsService() {
		StarterProjectImpl spi = new StarterProjectImpl(null);
		String sService = "org.libreoffice.example.StarterProject";
		assertEquals(true, spi.supportsService(sService));
	}

	@Test
	void testTrigger() {
//		StarterProjectImpl mStart = Mockito.mock(StarterProjectImpl.class);
//		mStart.trigger("actionOne");

		fail("Not yet implemented");
//		assertEquals(true, true);
	}




}
