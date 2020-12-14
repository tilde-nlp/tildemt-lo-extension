package com.tilde.mt.lotranslator.helper;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class LocaleHelper {
	
	private static Map<String, String> map = null;
	
	/**
	 * create map with languages and corresponding countries 
	 */
    private static void lazyInit() {
    	if(map == null) {
    		map = new TreeMap<String, String>();
    		
	    	java.util.Locale[] locales = java.util.Locale.getAvailableLocales();
	        for (java.util.Locale locale : locales) {
	            if ((locale.getDisplayCountry() != null) && (!"".equals(locale.getDisplayCountry()))) {
	                map.put(locale.getLanguage(), locale.getCountry());
	            }
	        }
    	}
    }
    
	public static String getCountryCode(String code) {
		lazyInit();
		
		java.util.Locale locale = java.util.Locale.forLanguageTag(code);
        if ("".equals(locale.getCountry())) {
            locale = new Locale(code, map.get(code));
        }
        return locale.getCountry();
    }
	
	public static com.sun.star.lang.Locale makeLibreLocale(String languageCode) {
		return new com.sun.star.lang.Locale(languageCode, getCountryCode(languageCode), "");
	}
}
