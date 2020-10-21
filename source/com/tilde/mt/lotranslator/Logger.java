package com.tilde.mt.lotranslator;

/**
 * Console logging helper
 * @author guntars.puzulis
 *
 */
public class Logger {
	private String name;
	
	public Logger(String name) {
		this.name = name;
	}
	
	public void info(String message) {
		System.out.println(String.format("INFO [%s] : %s", name, message));
	}
	
	public void warn(String message) {
		System.out.println(String.format("WARN [%s] : %s", name, message));	
	}
	
	public void error(String message) {
		System.out.println(String.format("ERROR [%s] : %s", name, message));
	}
}
