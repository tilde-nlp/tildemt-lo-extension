package org.libreoffice.example.helper;

import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XIndexAccess;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XController;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XFrame;
import com.sun.star.frame.XModel;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.text.XPageCursor;
import com.sun.star.text.XTextContent;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.XReplaceable;

/**
 * Helps getting desktop, components, frames, cursors and
 * other interfaces.
 *
 * @author arta.zena
 */
public class DocumentHelper {

	/** Returns the current XDesktop */
	public static XDesktop getCurrentDesktop(XComponentContext xContext) {
		XMultiComponentFactory xMCF = UnoRuntime.queryInterface(XMultiComponentFactory.class,
				xContext.getServiceManager());
        Object desktop = null;
		try {
			desktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", xContext);
		} catch (Exception e) {
			return null;
		}
        return UnoRuntime.queryInterface(com.sun.star.frame.XDesktop.class, desktop);
	}

	/** Returns the current XComponent */
    public static XComponent getCurrentComponent(XComponentContext xContext) {
        return getCurrentDesktop(xContext).getCurrentComponent();
    }

    /** Returns the current frame */
    public static XFrame getCurrentFrame(XComponentContext xContext) {
    	XModel xModel = UnoRuntime.queryInterface(XModel.class, getCurrentComponent(xContext));
    	return xModel.getCurrentController().getFrame();
    }

    /** Returns the current text document (if any) */
    public static XTextDocument getCurrentDocument(XComponentContext xContext) {
        return UnoRuntime.queryInterface(XTextDocument.class, getCurrentComponent(xContext));
    }

    /** Returns the replaceable text document */
    public static XReplaceable getReplaceableDocument (XTextDocument xTextDocument) {
    	return UnoRuntime.queryInterface(XReplaceable.class, xTextDocument);
    }

    public static XComponentLoader getComponentLoader (XDesktop xDesktop) {
    	return UnoRuntime.queryInterface(com.sun.star.frame.XComponentLoader.class, xDesktop);
    }

    public static XTextDocument getTextDocument(XComponent xComp) {
    	return UnoRuntime.queryInterface(com.sun.star.text.XTextDocument.class, xComp);
    }

    public static XMultiServiceFactory getMSF (XTextDocument xTextDoc) {
    	return UnoRuntime.queryInterface(com.sun.star.lang.XMultiServiceFactory.class, xTextDoc);
    }

    public static XTextContent getContent(Object oGraphic) {
    	return UnoRuntime.queryInterface(com.sun.star.text.XTextContent.class, oGraphic);
	}

	public static XPropertySet getPropertySet(Object oGraphic) {
		return UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, oGraphic);
	}

	public static XTextViewCursorSupplier getCursorSupplier (XController xController) {
		return UnoRuntime.queryInterface(XTextViewCursorSupplier.class, xController);
	}

	public static XPageCursor getPageCursor(XTextViewCursor xTextViewCursor) {
		return UnoRuntime.queryInterface(XPageCursor.class, xTextViewCursor);
	}

	public static XModel getModel(XTextDocument xTextDocument) {
		return UnoRuntime.queryInterface(XModel.class, xTextDocument);
	}
	public static XIndexAccess getIndex () {

		return null;
	}
}
