package org.libreoffice.example.comp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.libreoffice.example.dialog.ActionOne;
import org.libreoffice.example.dialog.ActionTwoAndThree;
import org.libreoffice.example.dialog.ConfigID;
import org.libreoffice.example.helper.DialogHelper;

import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.uno.XComponentContext;

/**
 * This class is the entry point of the project
 * (look in RegistrationHandler.classes).
 */

public final class TildeTranslatorImpl extends WeakBase
   implements com.sun.star.lang.XServiceInfo,
              com.sun.star.task.XJobExecutor
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = TildeTranslatorImpl.class.getName();
    private static final String[] m_serviceNames = {
        "org.libreoffice.example.TildeTranslator" };
    private static String clientID = null;

    public TildeTranslatorImpl( XComponentContext context )
    {
        m_xContext = context;
    };

    /** Create object for services (for panel factory) */
    public static XSingleComponentFactory __getComponentFactory( String sImplementationName ) {
        XSingleComponentFactory xFactory = null;

        if ( sImplementationName.equals( m_implementationName ) )
            xFactory = Factory.createComponentFactory(TildeTranslatorImpl.class, m_serviceNames);
        return xFactory;
    }

    /** Register panel factory */
    public static boolean __writeRegistryServiceInfo( XRegistryKey xRegistryKey ) {
        return Factory.writeRegistryServiceInfo(m_implementationName,
                                                m_serviceNames,
                                                xRegistryKey);
    }

	/**
	 * ClientID is stored in a file that is located in users home folder.
	 * If there is no ID in the file or the ID is invalid or the file does not exist,
	 * user gets ID configurationID.
	 * If it is valid, it is set to ClientID variable.
	 */
    private void checkClientIDFromFile() throws IOException {
    	String homeFolder = System.getProperty("user.home");
    	File dataFile = new File(homeFolder + File.separator +"tildeID");
    	System.out.println("exists?\t\t"+ dataFile.isFile());
    	if (dataFile.isFile()) {
    		BufferedReader reader;
    		try {
    			reader = new BufferedReader(new FileReader(dataFile));
    			String line = reader.readLine();
    			System.out.println("the line\t" + line);
    			if(line == null) {
    				ConfigID conf = new ConfigID(m_xContext);
    				conf.show();
    			} else {
    				ConfigID c = new ConfigID(m_xContext);
    				boolean valid = c.checkID(line);
    				if(valid) {
    					setClientID(line);
    				} else {
    					ConfigID conf = new ConfigID(m_xContext);
        				conf.show();
    				}
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	} else {
    		Boolean isCreated = dataFile.createNewFile();
        	System.out.println("isCreated\t" + isCreated);
        	if(isCreated) {
	        	ConfigID conf = new ConfigID(m_xContext);
	        	conf.show();
        	}
    	}
    }

    public void setClientID(String id) {
    	clientID = id;
    }

    public String getClientID() {
    	return clientID;
    }

    @Override
	public String getImplementationName() {
         return m_implementationName;
    }

    @Override
	public boolean supportsService( String sService ) {
        int len = m_serviceNames.length;

        for( int i=0; i < len; i++) {
            if (sService.equals(m_serviceNames[i]))
                return true;
        }
        return false;
    }

    @Override
	public String[] getSupportedServiceNames() {
        return m_serviceNames;
    }

    @Override
	public void trigger(String action)
    {
    	System.out.println("~~~~~ trigger");
    	if (clientID == null) {
    		try {
				checkClientIDFromFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	if (clientID != null) {
    	switch (action) {
    	case "actionOne":
    		ActionOne actionOneDialog = new ActionOne(m_xContext);
    		actionOneDialog.show();
    		break;
    	case "actionTwo":
    		ActionTwoAndThree actionTwo = new ActionTwoAndThree(m_xContext);
    		try {
				actionTwo.insertAction();
			} catch (Exception e) {
				e.printStackTrace();
			}
    		break;
    	case "actionThree":
    		ActionTwoAndThree actionThree = new ActionTwoAndThree(m_xContext);
    		try {
				actionThree.replaceAction();
			} catch (Exception e) {
				e.printStackTrace();
			}
    		break;
    	default:
    		DialogHelper.showErrorMessage(m_xContext, null, "Unknown action: " + action);
        	}
    	}
    }

}
