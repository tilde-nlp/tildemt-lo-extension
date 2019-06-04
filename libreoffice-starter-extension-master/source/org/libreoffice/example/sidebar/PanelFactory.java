package org.libreoffice.example.sidebar;

import com.sun.star.awt.XWindow;
import com.sun.star.beans.PropertyValue;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.rendering.XCanvas;
import com.sun.star.ui.XUIElement;
import com.sun.star.ui.XUIElementFactory;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.XComponentContext;

/**
 * This is the factory that creates the sidebar panel that displays an analog clock.
 */
public class PanelFactory
	implements XUIElementFactory, XServiceInfo
{
	public static final String __serviceName = "org.libreoffice.example.sidebar.PanelFactory";
	private static final String msURLhead = "private:resource/toolpanel/PanelFactory/TranslationPanel";
    private static final String msImplementationName = PanelFactory.class.getName();
    private static final String[] maServiceNames = {__serviceName};


    public static XSingleComponentFactory __getComponentFactory (final String sImplementationName)
    {
    	System.out.println("__getComponentFactory "+sImplementationName);
        if (sImplementationName.equals(msImplementationName))
        	return Factory.createComponentFactory(PanelBase.class, maServiceNames);
        else
        	return null;
    }




    public PanelFactory (final XComponentContext xContext)
    {
    	System.out.println("WorkbenchPanelFactory constructor");
    	mxContext = xContext;
    }




    /** The main factory method has two parts:
     *  - Extract and check some values from the given arguments
     *  - Check the sResourceURL and create a panel for it. aName
     */
	@Override
	public XUIElement createUIElement (
			final String sResourceURL,
			final PropertyValue[] aArgumentList)
			throws NoSuchElementException, IllegalArgumentException
	{
		System.out.println("createUIElement "+sResourceURL);

    	// Reject all resource URLs that don't have the right prefix.
        if ( ! sResourceURL.startsWith(msURLhead))
        {
            throw new NoSuchElementException(sResourceURL, this);
        }

        // Retrieve the parent window and canvas from the given argument list.
        XWindow xParentWindow = null;
        XCanvas xCanvas = null;
        System.out.println("processing "+aArgumentList.length+" arguments");
        for (final PropertyValue aValue : aArgumentList)
        {
        	System.out.println("    "+aValue.Name+" = "+aValue.Value);
            if (aValue.Name.equals("ParentWindow"))
            {
                try
                {
                    xParentWindow = (XWindow)AnyConverter.toObject(XWindow.class, aValue.Value);
                }
                catch (IllegalArgumentException aException)
                {
                	System.out.println(aException.getMessage());
                }
            }
            else if (aValue.Name.equals("Canvas"))
            {
            	xCanvas = (XCanvas)AnyConverter.toObject(XCanvas.class, aValue.Value);
            }
        }
        // Check some arguments.
        if (xParentWindow == null)
        {
            throw new IllegalArgumentException("No parent window provided to the UIElement factory. Cannot create tool panel.", this, (short)1);
        }

        // Create the panel.
        final String sElementName = sResourceURL.substring(msURLhead.length()+1);
        if (sElementName.equals("AnalogClockPanel"))
            return new UIElement(
            		sResourceURL,
            		new AnalogClockPanel(xParentWindow, mxContext, xCanvas));
        else
        	return null;
	}




	@Override
	public String getImplementationName ()
	{
        return msImplementationName;
	}




	@Override
	public String[] getSupportedServiceNames ()
	{
		return maServiceNames;
	}




	@Override
	public boolean supportsService (final String sServiceName)
	{
		for (final String sSupportedServiceName : maServiceNames)
			if (sSupportedServiceName.equals(sServiceName))
                return true;
		return false;
	}




    private final XComponentContext mxContext;
}
