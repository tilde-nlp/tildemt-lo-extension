package org.libreoffice.example.sidebar;

import java.util.HashMap;
import java.util.Map;

import org.libreoffice.example.dialog.ActionOne;

import com.sun.star.beans.PropertyValue;
import com.sun.star.frame.DispatchDescriptor;
import com.sun.star.frame.FeatureStateEvent;
import com.sun.star.frame.XDispatch;
import com.sun.star.frame.XDispatchProvider;
import com.sun.star.frame.XStatusListener;
import com.sun.star.lib.uno.helper.ComponentBase;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.URL;

/** We have to provide a protocol handler only so that we can show an options dialog.
 *  In the panel description in Sidebar.xcu there is a field "DefaultMenuCommand".
 *  Its value is a UNO command name that is executed when the user clicks on the
 *  "more options" button in the panel title bar.  We need the protocol handler to
 *  provide a new command "ShowAnalogClockOptionsDialog" that, when executed, shows
 *  the Java dialog implemented by AnalogClockOptionsDialog.
 */
public class ProtocolHandler
    extends ComponentBase
    implements XDispatchProvider, XDispatch
{
    public final static String __serviceName = "org.libreoffice.example.sidebar.ProtocolHandler";
    final static String msProtocol = "org.libreoffice.example.sidebar";
    final static String msShowCommand = "ShowDetailsDialog";


    public ProtocolHandler (final XComponentContext xContext)
    {
        maListeners = new HashMap<URL,XStatusListener>();
        System.out.println(">>> started ProtocolHandler");
    }




    //----- Implementation of UNO interface XDispatchProvider -----

    @Override
    public XDispatch queryDispatch (
        final URL aURL,
        final String sTarget,
        final int nFlags) throws RuntimeException
    {
    	// Check the given URL to make sure that it
    	// a) has the right protocol and
    	// b) has the one command name that is supported.
        if ( ! aURL.Complete.startsWith(msProtocol))
            return null;
        else if (aURL.Complete.endsWith(msShowCommand))
            return this;
        else
            return null;
    }




    /** We only support one command but still have to implement this method.
     */
    @Override
    public com.sun.star.frame.XDispatch[] queryDispatches(
        final DispatchDescriptor[] aRequests) throws RuntimeException
    {
        final int nDispatchCount = aRequests.length;
        final XDispatch[] aDispatches = new XDispatch[nDispatchCount];
        for (int nIndex=0; nIndex<nDispatchCount; ++nIndex)
            aDispatches[nIndex] = queryDispatch(
                aRequests[nIndex].FeatureURL,
                aRequests[nIndex].FrameName,
                aRequests[nIndex].SearchFlags);
        return aDispatches;
    }




    //----- Implementation of UNO interface XDispatch -----

    @Override
	public void addStatusListener (
        final XStatusListener xListener,
        final URL aURL)
    {
        maListeners.put(aURL, xListener);
        xListener.statusChanged(new FeatureStateEvent(
            this,
            aURL,
            "Feature Descriptor",
            true,
            false,
            false));
    }




    @Override
	public void removeStatusListener (
            final XStatusListener xListener,
            final URL aURL)
    {
    	maListeners.remove(xListener);
    }




    @Override
	public void dispatch (
        final URL aURL,
        final PropertyValue[] aArguments)
    {
    	ActionOne actionOneDialog = new ActionOne(null);//TODO: get context instead of null
		if (aURL.Complete.endsWith(msShowCommand))
    		actionOneDialog.show();

    }




    private Map<URL,XStatusListener> maListeners;
}