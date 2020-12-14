package com.tilde.mt.lotranslator.models;

/**
 * As TildeMT API does not return consistent JSON for error and success result, we need some kind of response wrapper
 * @author guntars.puzulis
 *
 * @param <T>
 */
public class ErrorResult<T> {
	public T Result = null;
	public TildeMTError Error = null;
	
	public Boolean hasError() {
		return Error.hasError();
	}
	
	@Override
	public String toString() {
		return String.format("%s [Result: %s, Error: %s]", this.getClass().getSimpleName(), Result, Error);
	}
}
