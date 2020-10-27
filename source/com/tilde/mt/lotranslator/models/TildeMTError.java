package com.tilde.mt.lotranslator.models;

public class TildeMTError {
	public int ErrorCode;
	public String ErrorMessage;
	
	public String toErrorMessage() {
		return String.format("Error code: %s, Error message: %s", ErrorCode, ErrorMessage);
	}
	
	public Boolean hasError() {
		if(ErrorCode == 0 && ErrorMessage.trim().equals("")) {
			return false;
		}
		return true;
	}
}
