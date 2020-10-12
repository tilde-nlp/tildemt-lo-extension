package com.tilde.mt.lotranslator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

/**
 * This dialog is shown, if UserID cannot be found or is invalid.
 * User is asked to go to web site where UserID can be copied.
 * When user pastes the ID in the text box and presses the button,
 * this class checks whether it is valid.
 *   If it is, then data file and variable in TildeTranslatorImpl are updated.
 *   If not, user cannot use translation service.
 *
 * @author arta.zena
 */

public class Configuration {
	private static final Logger logger = new Logger(Configuration.class.getName());
	private static final String configurationFile = System.getProperty("user.home") + File.separator + ".tildeConfiguration.json";
	
	private static String systemID = null;

	private static Boolean Exists() {
		File f = new File(configurationFile);
		if(f.exists() && !f.isDirectory()) { 
		    return true;
		}
		return false;
	}
	
	public static void Write(LetsMTConfiguration config) {
		try (Writer writer = new FileWriter(configurationFile)) {
		    Gson gson = new GsonBuilder().create();
		    gson.toJson(config, writer);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static LetsMTConfiguration Read() {
		logger.info("Reading configuration...");
		logger.info(String.format("Configuration file: %s", configurationFile));
		
		LetsMTConfiguration config = new LetsMTConfiguration();
		if(Exists()) {
			try {
				Gson gson = new Gson();
				JsonReader reader = new JsonReader(new FileReader(configurationFile));
				config = gson.fromJson(reader, LetsMTConfiguration.class);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		else {
			logger.warn("configuration file does not exist, creating example configuration");
			
			Write(config);
		}
		
		return config;
	}
	
	public static void setSystemID(String systemID) {
		logger.info(String.format("Set active system id: %s", systemID));
		Configuration.systemID = systemID;
	}
	
	public static String getSystemID() {
		logger.info(String.format("Active system id: %s", Configuration.systemID));
		return Configuration.systemID;
	}
}
