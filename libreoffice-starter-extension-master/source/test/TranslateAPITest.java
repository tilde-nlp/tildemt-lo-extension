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
		TranslateAPI api = null;
		try {
			api = new TranslateAPI();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String json = "{\"confidentWordAlignment\":[],\"countSentences\":3,\"countTokens\":48,\"originalSentenceRanges\":[],\"phraseAlignment\":[],\"qualityEstimate\":0,\"sourceWordRanges\":[],\"targetWordRanges\":[],\"translation\":\"Hello\",\"translationSentenceRanges\":[],\"wordAlignment\":[]}";
		String translation = api.getTranslationFromJSON(json);
		String translated = "Hello";
		assertEquals(translated, translation);
	}

	@Test
	@DisplayName("Translation from empty response JSON")
	void testGetTranslationFromEmptyJSON() {
		TranslateAPI api = null;
		try {
			api = new TranslateAPI();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String json = "{}";
		String translation = api.getTranslationFromJSON(json);
		assertEquals("", translation);
	}

	@Test
	@DisplayName("Special characters in response JSON")
	void testGetTranslationFromCharJSON() {
		TranslateAPI api = null;
		try {
			api = new TranslateAPI();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String json = "{\"confidentWordAlignment\":[],\"countSentences\":3,\"countTokens\":48,\"originalSentenceRanges\":[],\"phraseAlignment\":[],\"qualityEstimate\":0,\"sourceWordRanges\":[],\"targetWordRanges\":[],\"translation\":\"�?ĀģĢÊýë\",\"translationSentenceRanges\":[],\"wordAlignment\":[]}";
		String translation = api.getTranslationFromJSON(json);
		assertEquals("�?ĀģĢÊýë", translation);
	}

}
