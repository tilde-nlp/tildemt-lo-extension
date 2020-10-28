package com.tilde.mt.lotranslator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.tilde.mt.lotranslator.models.TildeMTSystem;

public class Configuration {
	/**
	 * This is public client id which is fallback when there are no private clientId set.
	 * This shall be set in build time.
	 */
	private static final String publicClientID = "x-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx";
	
	private static final Logger logger = new Logger(Configuration.class.getName());
	private static final String configurationFile = System.getProperty("user.home") + File.separator + ".tildeConfiguration.json";
	private static final Gson gson = new Gson();
	
	private static TildeMTSystem system = null;

	/**
	 * Configuration file exists
	 * @return
	 */
	private static Boolean Exists() {
		File f = new File(configurationFile);
		if(f.exists() && !f.isDirectory()) { 
		    return true;
		}
		return false;
	}
	
	/**
	 * Write configuration to permanent storage
	 * @param config
	 */
	public static Boolean Write(LetsMTConfiguration config) {
		try (Writer writer = new FileWriter(configurationFile)) {
		    gson.toJson(config, writer);
		    return true;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Read configuration from permanent storage
	 * @return
	 */
	public static LetsMTConfiguration Read() {
		logger.info("Reading configuration...");
		logger.info(String.format("Configuration file: %s", configurationFile));
		Boolean setDefaultConfiguration = false;
		
		LetsMTConfiguration config = new LetsMTConfiguration();
		if(Exists()) {
			try {
				JsonReader reader = new JsonReader(new FileReader(configurationFile));
				config = gson.fromJson(reader, LetsMTConfiguration.class);
			}
			catch(Exception e) {
				e.printStackTrace();
				setDefaultConfiguration = true;
			}
		}
		else {
			logger.warn("configuration file does not exist, creating example configuration");
			
			Write(config);
			
			setDefaultConfiguration = true;
		}
		
		if(setDefaultConfiguration || config.ClientID == null) {
			config.ClientID = publicClientID;
		}
		
		return config;
	}
	
	/**
	 * Check if user is using his private client id.
	 * @return
	 */
	public static Boolean IsPrivateConfiguration() {
		LetsMTConfiguration config = Configuration.Read();
		
		return config.ClientID != Configuration.publicClientID;
	}
	
	/**
	 * Save MT system id for current document session
	 * @param systemID
	 */
	public static void setSystem(TildeMTSystem system) {
		logger.info(String.format("Set active system: %s", system));
		Configuration.system = system;
	}
	
	/**
	 * Read MT system for this document session
	 * @return
	 */
	public static TildeMTSystem getSystem() {
		logger.info(String.format("Active system: %s", Configuration.system));
		return Configuration.system;
	}
}
