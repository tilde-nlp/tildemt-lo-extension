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


public final class StarterProjectImpl extends WeakBase
   implements com.sun.star.lang.XServiceInfo,
              com.sun.star.task.XJobExecutor
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = StarterProjectImpl.class.getName();
    private static final String[] m_serviceNames = {
        "org.libreoffice.example.StarterProject" };
    private static String clientID = null;

    private ActionOne m_dialog;
    private ActionOne getDialog() {
    	if (m_dialog == null) {
    		return new ActionOne(m_xContext);
    	}
    	else {
    		return m_dialog;
    	}
    }

    private ConfigID m_config_dialog;
    private ConfigID getConfigDialog() {
    	if (m_dialog == null) {
    		return new ConfigID(m_xContext);
    	}
    	else {
    		return m_config_dialog;
    	}
    }

    public StarterProjectImpl( XComponentContext context )
    {
    	System.out.println("~~~~~ StarterProjectImpl constructor");
        m_xContext = context;
    };

    private void setClientID() throws IOException {
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
    				ConfigID conf = getConfigDialog();
    				conf.show();
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	} else {
    		Boolean isCreated = dataFile.createNewFile();
        	System.out.println("isCreated\t" + isCreated);
        	if(isCreated) {
	        	ConfigID conf = getConfigDialog();
	        	conf.show();
        	}
    	}
    }

    public StarterProjectImpl( XComponentContext context, ActionOne dialog )
    {
        m_xContext = context;
        m_dialog = dialog;
    };

    public static XSingleComponentFactory __getComponentFactory( String sImplementationName ) {
        XSingleComponentFactory xFactory = null;

        if ( sImplementationName.equals( m_implementationName ) )
            xFactory = Factory.createComponentFactory(StarterProjectImpl.class, m_serviceNames);
        return xFactory;
    }

    public static boolean __writeRegistryServiceInfo( XRegistryKey xRegistryKey ) {
        return Factory.writeRegistryServiceInfo(m_implementationName,
                                                m_serviceNames,
                                                xRegistryKey);
    }

    // com.sun.star.lang.XServiceInfo:
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

    // com.sun.star.task.XJobExecutor:


//TODO dialogs are not showing up anymore
    @Override
	public void trigger(String action)
    {
    	System.out.println("~~~~~ trigger");
//    	if (clientID == null) {
//    		try {
//				setClientID();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//    	}
//    	if (clientID != null) {
        	switch (action) {
        	case "actionOne":
        		ActionOne actionOneDialog = getDialog();
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
//    	}
    }

}
