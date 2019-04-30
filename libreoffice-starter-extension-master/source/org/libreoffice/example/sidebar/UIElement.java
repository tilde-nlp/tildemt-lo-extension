package org.libreoffice.example.sidebar;

import com.sun.star.frame.XFrame;
import com.sun.star.ui.UIElementType;
import com.sun.star.ui.XUIElement;

/** A simple implementation of the XUIElement interface.
 */
public class UIElement
	implements XUIElement
{
	public UIElement (
			final String sResourceURL,
			final PanelBase aPanel)
	{
		mxFrame = null;
		msResourceURL = sResourceURL;
		maPanel = aPanel;
	}




	@Override
	public XFrame getFrame ()
	{
		return mxFrame;
	}




	@Override
	public Object getRealInterface ()
	{
		return maPanel;
	}




	@Override
	public String getResourceURL ()
	{
		return msResourceURL;
	}




	@Override
	public short getType()
	{
		return UIElementType.TOOLPANEL;
	}




	private final XFrame mxFrame;
	private final String msResourceURL;
	private final PanelBase maPanel;
}
