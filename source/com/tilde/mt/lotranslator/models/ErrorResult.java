package com.tilde.mt.lotranslator.models;

import com.google.gson.Gson;

public class ErrorResult<T> {
	public T Result = null;
	public TildeMTError Error = null;
	
	public ErrorResult() {
		
	}
	
	public ErrorResult(String rawResult, Class<T> templateClass) {
		
		Gson gson = new Gson();
		
		try {
			Result = gson.fromJson(rawResult, templateClass);
		}
		catch(Exception ex) {
			Error = gson.fromJson(rawResult, TildeMTError.class);
		}
	}
	
	public Boolean hasError() {
		if(Error == null || (Error.ErrorCode == 0 && Error.ErrorMessage.trim().equals(""))) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return String.format("%s [Result: %s, Error: %s]", this.getClass().getSimpleName(), Result, Error);
	}
}
