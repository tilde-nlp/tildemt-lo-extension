package test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.libreoffice.example.helper.TranslateAPI;

class TranslateAPITest {

	@Test
	@DisplayName("Translation from full response JSON")
	void testGetTranslationFromFullJSON() {
		TranslateAPI api1 = null;
		try {
			api1 = new TranslateAPI();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String json = "{\"confidentWordAlignment\":[],\"countSentences\":3,\"countTokens\":48,\"originalSentenceRanges\":[],\"phraseAlignment\":[],\"qualityEstimate\":0,\"sourceWordRanges\":[],\"targetWordRanges\":[],\"translation\":\"Hello\",\"translationSentenceRanges\":[],\"wordAlignment\":[]}";
		String translation = api1.getTranslationFromJSON(json);
		assertEquals("Hello", translation);
	}

	@Test
	@DisplayName("Translation from empty response JSON")
	void testGetTranslationFromEmptyJSON() {
		TranslateAPI api1 = null;
		try {
			api1 = new TranslateAPI();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String json = "{}";
		String translation = api1.getTranslationFromJSON(json);
		assertEquals("", translation);
	}

	@Test
	@DisplayName("Special characters in response JSON")
	void testGetTranslationFromCharJSON() {
		TranslateAPI api1 = null;
		try {
			api1 = new TranslateAPI();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String json = "{\"confidentWordAlignment\":[],\"countSentences\":3,\"countTokens\":48,\"originalSentenceRanges\":[],\"phraseAlignment\":[],\"qualityEstimate\":0,\"sourceWordRanges\":[],\"targetWordRanges\":[],\"translation\":\"āĀģĢÊýë\",\"translationSentenceRanges\":[],\"wordAlignment\":[]}";
		String translation = api1.getTranslationFromJSON(json);
		assertEquals("āĀģĢÊýë", translation);
	}

}
