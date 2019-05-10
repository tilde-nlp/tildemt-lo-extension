package org.libreoffice.example.sidebar;

import org.libreoffice.example.dialog.ActionOne;
import org.libreoffice.example.helper.DialogHelper;

import com.sun.star.comp.loader.FactoryHelper;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.lang.XSingleServiceFactory;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.uno.XComponentContext;

public final class SidebarImpl extends WeakBase
   implements com.sun.star.lang.XServiceInfo,
              com.sun.star.task.XJobExecutor
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = SidebarImpl.class.getName();
    private static final String[] m_serviceNames = {
        "org.libreoffice.example.sidebar.PanelFactory" };


    public SidebarImpl( XComponentContext context )
    {
    	System.out.println(">> Sidebar constructor <<");
        m_xContext = context;
    };

    //JavaLoader calls both following methods
    public static XSingleComponentFactory __getComponentFactory( String sImplementationName ) {
    	System.out.println(">> __getComponentFactory ON <<");
    	System.out.println("m_implementationName:\t" + m_implementationName);
    	System.out.println("sImplementationName:\t" + sImplementationName);
        XSingleComponentFactory xFactory = null;

        if ( sImplementationName.equals( m_implementationName ) )
            xFactory = Factory.createComponentFactory(PanelFactory.class, m_serviceNames);
        return xFactory;
    }

    public static boolean __writeRegistryServiceInfo( XRegistryKey xKey ) {
    	System.out.println(">> __writeRegistryServiceInfo ON <<");
        return Factory.writeRegistryServiceInfo(m_implementationName,
                                                m_serviceNames,
                                                xKey);
//        boolean bResult = true;
//        try
//        {
//            System.out.println("writing registry service info for WorkbenchPanelFactory");
//
//            bResult &= FactoryHelper.writeRegistryServiceInfo(
//            	PanelFactory.class.getName(),
//                PanelFactory.__serviceName,
//                xKey);
//
//            bResult &= FactoryHelper.writeRegistryServiceInfo(
//                    ProtocolHandler.class.getName(),
//                    ProtocolHandler.__serviceName,
//                    xKey);
//
//            System.out.println("    success");
//        }
//        catch (java.lang.Exception e)
//        {
//        	e.printStackTrace();
//        }
//
//        return bResult;
    }
    public static XSingleServiceFactory __getServiceFactory(
            final String sImplementationName,
            final XMultiServiceFactory xFactory,
            final XRegistryKey xKey)
        {
            XSingleServiceFactory xResult = null;
            System.out.println("looking up service factory for "+sImplementationName);
            if (sImplementationName.equals(PanelFactory.class.getName()))
            {
    	        xResult = FactoryHelper.getServiceFactory(
    	            	PanelFactory.class,
    	                PanelFactory.__serviceName,
    	                xFactory,
    	                xKey);
            }
            else if (sImplementationName.equals(ProtocolHandler.class.getName()))
            {
                xResult = FactoryHelper.getServiceFactory(
                    ProtocolHandler.class,
                    ProtocolHandler.__serviceName,
                    xFactory,
                    xKey);
            }
            System.out.println("    returning "+xResult);

            return xResult;
        }

    // com.sun.star.lang.XServiceInfo:
    @Override
	public String getImplementationName() {
    	System.out.println(">> getImplementationName ON <<");
         return m_implementationName;
    }

    @Override
	public boolean supportsService( String sService ) {
    	System.out.println(">> supportsService ON <<");
        int len = m_serviceNames.length;

        for( int i=0; i < len; i++) {
            if (sService.equals(m_serviceNames[i]))
                return true;
        }
        return false;
    }

    @Override
	public String[] getSupportedServiceNames() {
    	System.out.println(">> getSupportedServiceNames ON <<");
        return m_serviceNames;
    }

    // com.sun.star.task.XJobExecutor:

    @Override
	public void trigger(String action)
    {
    	System.out.println(">>> SidebarImpl trigger ON <<<");
    	switch (action) {
    	case "actionOne":
    		ActionOne actionOneDialog = new ActionOne(m_xContext);
    		actionOneDialog.show();
    		System.out.println("Dialog done");
    		break;
    	default:
    		DialogHelper.showErrorMessage(m_xContext, null, "Unknown action: " + action);
    	}

    }

}
