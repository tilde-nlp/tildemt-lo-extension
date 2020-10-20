package com.tilde.mt.lotranslator.models;

import com.sun.star.lang.Locale;

/**
 * This is information about user selected text in document
 * @author guntars.puzulis
 *
 */
public class SelectedText {
	public String Text;
	public Locale Locale;
	
	@Override
    public String toString() {
		return String.format("Selected text [Locale: %s, Text: %s]", Locale.Language, Text);
	}
}
