package org.libreoffice.example.comp;

import org.libreoffice.example.dialog.ActionOne;
import org.libreoffice.example.dialog.ActionTwoAndThree;
import org.libreoffice.example.dialog.ConfigID;
import org.libreoffice.example.helper.DialogHelper;
import org.libreoffice.example.helper.LetsMT.SystemListM;

import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.uno.XComponentContext;

/**
 * This class is the entry point of the project.
 * The method "trigger" activates, when user
 * interacts with the extension.
 * (look in RegistrationHandler.classes)
 *
 * @author arta.zena
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
    private static SystemListM systemList = null;
    private static String systemID = null;

    /**
     * @param context
     */
    public TildeTranslatorImpl( XComponentContext context )
    {
        m_xContext = context;
    };

    /**
     * Create object for services (for panel factory)
     * @param sImplementationName
     * @return
     */
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
     * @param id	a String containing valid Client ID
     */
    public void setClientID(String id) {
    	clientID = id;
    }

    /**
     * @return	returns current client ID
     */
    public static String getClientID() {
    	return clientID;
    }

    public static void setSystemList (SystemListM sysList) {
    	systemList = sysList;
    }

    public static SystemListM getSystemList() {
    	return systemList;
    }

    public static void setSystemID(String id) {
    	systemID = id;
    }

    public static String getSystemID() {
    	return systemID;
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

	/**
	 * interface XJobExecutor
	 * trigger event to start registered jobs
	 * Jobs are registered in configuration and will be started by
	 * executor automaticly, if they are registered for triggered event.
	 *
	 * trigger() sets client ID and if it is valid then
	 * direct action to the correct class.
	 *
	 * @param action	describe the event for which jobs can be registered and should be started
	 */
	@Override
	public void trigger(String action)
	{
		// if client ID is not set, show configuration dialog
		if (clientID == null) {
			ConfigID configID = new ConfigID(m_xContext);
			configID.configureID();
			// get(set)SystemListM
		}
		// if setting the valid ID is succesful, performs asked action
		if (clientID != null) {
	    	switch (action) {
	    		// call the translation dialog
		    	case "actionOne":
		    		ActionOne actionOneDialog = new ActionOne(m_xContext);
		    		actionOneDialog.show();
		    		break;
		    	// call translaton appending action
		    	case "actionTwo":
		    		ActionTwoAndThree actionTwo = new ActionTwoAndThree(m_xContext);
		    		try {
						actionTwo.appendAction();
					} catch (Exception e) {
						e.printStackTrace();
					}
		    		break;
		    	// call replacing action
		    	case "actionThree":
;		    		ActionTwoAndThree actionThree = new ActionTwoAndThree(m_xContext);
		    		try {
						actionThree.replaceAction();
					} catch (Exception e) {
						e.printStackTrace();
					}
		    		break;
		    	// prompt if non-defined action has been called
		    	default:
		    		DialogHelper.showErrorMessage(m_xContext, null, "Unknown action: " + action);
		    	}
		}
	}

}
