package org.libreoffice.example.comp;

import org.libreoffice.example.dialog.ActionOne;
import org.libreoffice.example.dialog.ActionTwoAndThree;
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

    private ActionOne m_dialog;

    private ActionOne getDialog() {
    	if (m_dialog == null) {
    		return new ActionOne(m_xContext);
    	}
    	else {
    		return m_dialog;
    	}
    }

    public StarterProjectImpl( XComponentContext context )
    {
        m_xContext = context;
    };

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



    @Override
	public void trigger(String action)
    {
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

    }

}
