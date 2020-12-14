package com.tilde.mt.lotranslator.models;

public class TildeMTError {
	public String ErrorCode;
	public String ErrorMessage;
	
	public String toErrorMessage() {
		return String.format("Error code: %s, Error message: %s", ErrorCode, ErrorMessage);
	}
	
	public Boolean hasError() {
		if((ErrorCode == null || ErrorCode.trim().equals("") || ErrorCode.trim().equals("0")) && (ErrorMessage == null || ErrorMessage.trim().equals(""))) {
			return false;
		}
		return true;
	}
}
