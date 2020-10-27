package com.tilde.mt.lotranslator.comp;

import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.uno.XComponentContext;
import com.tilde.mt.lotranslator.Configuration;
import com.tilde.mt.lotranslator.LetsMTConfiguration;
import com.tilde.mt.lotranslator.Logger;
import com.tilde.mt.lotranslator.TildeMTClient;
import com.tilde.mt.lotranslator.dialog.ActionAppend;
import com.tilde.mt.lotranslator.dialog.ActionReplace;
import com.tilde.mt.lotranslator.dialog.ActionTranslate;
import com.tilde.mt.lotranslator.dialog.ActionTranslateDocument;
import com.tilde.mt.lotranslator.dialog.actionAuth;
import com.tilde.mt.lotranslator.helper.DialogHelper;
import com.tilde.mt.lotranslator.models.TildeMTSystemList;
import com.tilde.mt.lotranslator.models.TildeMTUserData;

public final class TildeTranslatorImpl extends WeakBase
   implements com.sun.star.lang.XServiceInfo,
              com.sun.star.task.XJobExecutor
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = TildeTranslatorImpl.class.getName();
    private static final String[] m_serviceNames = { "com.tilde.mt.lotranslator.tildetranslator" };
    
    private static final Logger logger = new Logger(TildeTranslatorImpl.class.getName());

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
            if (sService.equals(m_serviceNames[i])) {
                return true;
            }
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
		TildeMTClient client = new TildeMTClient(config.ClientID);
		TildeMTSystemList systemList = client.GetSystemList();

		String systemID = Configuration.getSystemID();
		actionAuth configDialog = new actionAuth(this.m_xContext);
		
		if(systemList == null || systemList.System == null) {
	        configDialog.show(true);
        }
		else {
			TildeMTUserData userData;
			try{
				userData = client.GetUserData().get();
			}
			catch(Exception ex) {
				DialogHelper.showErrorMessage(m_xContext, null, "Failed to get user data");
				return;
			}
			
			
			if(action.equals("actionTranslate")) {
				new ActionTranslate(m_xContext, client, userData).show();
			}
			else if(action.equals("actionAuth")) {
				configDialog.show(false);
			}
			else {
				if(systemID == null) {
					DialogHelper.showInfoMessage(m_xContext, null, "Please choose MT system and then try again.");	
	    			new ActionTranslate(m_xContext, client, userData).show();
				}
				else {
					if(action.equals("actionAppend")){
		    			new ActionAppend(m_xContext, client, systemID).show();
					}
					else if(action.equals("actionReplace")){
		    			new ActionReplace(m_xContext, client, systemID).show();
					}
					else if(action.equals("actionTranslateDocument")){
			    		new ActionTranslateDocument(m_xContext, client, systemID).show();
					}
					else {
			    		DialogHelper.showErrorMessage(m_xContext, null, "Unknown action: " + action);
			    	}
				}
			}
			
		}
	}
}
