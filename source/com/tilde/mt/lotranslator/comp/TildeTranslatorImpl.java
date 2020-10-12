package com.tilde.mt.lotranslator.comp;

import java.util.logging.Logger;

import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.uno.XComponentContext;
import com.tilde.mt.lotranslator.Configuration;
import com.tilde.mt.lotranslator.LetsMTConfiguration;
import com.tilde.mt.lotranslator.TildeMTAPIClient;
import com.tilde.mt.lotranslator.dialog.ActionTranslate;
import com.tilde.mt.lotranslator.dialog.ConfigDialog;
import com.tilde.mt.lotranslator.helper.DialogHelper;

public final class TildeTranslatorImpl extends WeakBase
   implements com.sun.star.lang.XServiceInfo,
              com.sun.star.task.XJobExecutor
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = TildeTranslatorImpl.class.getName();
    private static final String[] m_serviceNames = {"com.tilde.mt.lotranslator.tildetranslator" };
    
    private static final Logger logger = Logger.getLogger(TildeTranslatorImpl.class.getName());

    /**
     * @param context
     */
    public TildeTranslatorImpl( XComponentContext context )
    {
        m_xContext = context;
        logger.info("Tilde Translator init");
    };

    /**
     * Create object for services (for panel factory)
     * @param sImplementationName
     * @return
     */
    public static XSingleComponentFactory __getComponentFactory( String sImplementationName ) {
        XSingleComponentFactory xFactory = null;

        if (sImplementationName.equals(m_implementationName)) {
        	xFactory = Factory.createComponentFactory(TildeTranslatorImpl.class, m_serviceNames);
        }
        return xFactory;
    }

    /** Register panel factory */
    public static boolean __writeRegistryServiceInfo( XRegistryKey xRegistryKey ) {
        return Factory.writeRegistryServiceInfo(m_implementationName, m_serviceNames, xRegistryKey);
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
	 * executor automatically, if they are registered for triggered event.
	 *
	 * trigger() sets client ID and if it is valid then
	 * direct action to the correct class.
	 *
	 * @param action	describe the event for which jobs can be registered and should be started
	 */
	@Override
	public void trigger(String action)
	{
		logger.info(String.format("Action: %s", action));
		
		// if client ID is not set, show configuration dialog
		
		LetsMTConfiguration config = Configuration.Read();
		TildeMTAPIClient client = new TildeMTAPIClient(config.ClientID);
		if(client.GetSystemList() == null) {
	        ConfigDialog configDialog = new ConfigDialog(this.m_xContext);
	        configDialog.show();
		}
		else {
			switch (action) {
	    		// call the translation dialog
		    	case "actionTranslate":
		    		ActionTranslate actionOneDialog = new ActionTranslate(m_xContext, client);
		    		actionOneDialog.show();
		    		break;
		    	// call translaton appending action
		    	/*case "actionTwo":
		    		ActionTwoAndThree actionTwo = new ActionTwoAndThree(m_xContext);
		    		try {
						actionTwo.appendAction();
					} catch (Exception e) {
						e.printStackTrace();
					}
		    		break;
		    	// call replacing action
		    	case "actionThree":
		    		ActionTwoAndThree actionThree = new ActionTwoAndThree(m_xContext);
		    		try {
						actionThree.replaceAction();
					} catch (Exception e) {
						e.printStackTrace();
					}
		    		break;*/
		    	default:
		    		DialogHelper.showErrorMessage(m_xContext, null, "Unknown action: " + action);
	    	}
		}
	}
}
