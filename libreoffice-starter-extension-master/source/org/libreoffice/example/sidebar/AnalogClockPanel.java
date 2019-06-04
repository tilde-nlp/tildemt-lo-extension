package org.libreoffice.example.sidebar;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.sun.star.awt.Size;
import com.sun.star.awt.XWindow;
import com.sun.star.geometry.AffineMatrix2D;
import com.sun.star.geometry.RealBezierSegment2D;
import com.sun.star.geometry.RealPoint2D;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.rendering.CompositeOperation;
import com.sun.star.rendering.RenderState;
import com.sun.star.rendering.ViewState;
import com.sun.star.rendering.XCanvas;
import com.sun.star.rendering.XSpriteCanvas;
import com.sun.star.ui.LayoutSize;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

/** Implementation of a sidebar panel that shows an analog clock.
 *  The focus of this class lies on showing how to implement a
 *  sidebar panel that uses a canvas to display its content.
 */
public class AnalogClockPanel
	extends PanelBase

{
	AnalogClockPanel (
			final XWindow xParentWindow,
			final XComponentContext xContext,
			final XCanvas xCanvas)
	{
		mxCanvas = xCanvas;
		mnCurrentSecond = -1;

		// Prepare some frequently used values for the canvas.
		maViewState = new ViewState(new AffineMatrix2D(1,0,0, 0,1,0), null);
		maRenderState = new RenderState(new AffineMatrix2D(1,0,0, 0,1,0), null, new double[]{1,0,0,0.5}, CompositeOperation.OVER);

		// Let our base class create a content window.
		Initialize(xParentWindow, xContext);

		// Create a new timer that is called once twice per second.
		// A duration of 1 second between calls may seem more
		// straightforward, but when the timer is not absolutely precise
		// then it may happen that the seconds hand is not updated for
		// almost two seconds.
		maTimer = new Timer();
		maTimer.scheduleAtFixedRate(
				new TimerTask()
				{
					@Override public void run()
					{
						final Calendar aCalendar = Calendar.getInstance();
						final int nSecond = aCalendar.get(Calendar.SECOND);
						if (mnCurrentSecond != nSecond)
						{
							mnCurrentHour = aCalendar.get(Calendar.HOUR);
							mnCurrentMinute = aCalendar.get(Calendar.MINUTE);
							mnCurrentSecond = nSecond;
							PaintCurrentTime();
						}

					}
				},
				500,
				500);
	}




	/** Callback from our base class that tells us that the panel size has changed.
	 */
	@Override
	protected void Layout (final Size aWindowSize)
	{
		// The little layouting that we do (calculating center and radius of the clock)
		// is done every time when the hands are painted (usually every second).

		// Remember the size and trigger a repaint.
		maWindowSize = aWindowSize;
		PaintCurrentTime();
	}




	/** Paint the clock face and hour, minute, and second hand onto an XCanvas object.
	 */
	private void PaintCurrentTime ()
	{
		// Leave early when we have not (yet) been properly initialized.
		if (mxCanvas==null || maWindowSize==null)
			return;
		if (maWindowSize.Height == 0)
			return;
		if (mnCurrentSecond < 0)
			return;

		// Setup center and radius of the clock face (and origin of the hands).
		final double nCenterX = maWindowSize.Width / 2.0;
		final double nCenterY = maWindowSize.Height / 2.0;
		double nRadius = Math.min(nCenterX, nCenterY);

		// Leave a little space on the outside of the clock face.
		if (nRadius > 50)
			nRadius -= 10;
		else if (nRadius > 10)
			nRadius -= 5;

		// Paint the new panel content, i.e the current time.
		try
		{
			mxCanvas.clear();

			// Paint the clock face.
			//SetColor(maRenderState, maFaceColor);
			DrawShape (
					new Ellipse2D.Double(nCenterX-nRadius, nCenterY-nRadius, 2*nRadius, 2*nRadius));

			// Paint hands.
			PaintHand(nCenterX, nCenterY, nRadius, maHourHandColor, mnCurrentHour, Calendar.HOUR);
			PaintHand(nCenterX, nCenterY, nRadius, maMinuteHandColor, mnCurrentMinute, Calendar.MINUTE);
			PaintHand(nCenterX, nCenterY, nRadius, maSecondHandColor, mnCurrentSecond, Calendar.SECOND);

			// Make the updated clock visible.
			XSpriteCanvas xSpriteCanvas = UnoRuntime.queryInterface(XSpriteCanvas.class, mxCanvas);
			if (xSpriteCanvas != null)
				xSpriteCanvas.updateScreen(true);
		}
		catch (IllegalArgumentException e)
		{
			System.out.println(e.getCause());
		}
	}




	/** Paint a clock hand from the given center point.
	 *  @param nValueType
	 *  	One of Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR defines the range of nValue.
	 *      It is [0,60) for seconds and minutes and [0,12) for hours.
	 */
	private void PaintHand (
			final double nCenterX,
			final double nCenterY,
			final double nRadius,
			final Color aColor,
			final double nValue,
			final int nValueType)
	{
		SetColor(maRenderState, aColor);

		// Relative time value in the [0,1] interval.
		final double nRelativeTime;
		// Length of the hand.
		final double nLocalRadius;
		switch(nValueType)
		{
			case Calendar.SECOND:
				nRelativeTime = nValue/60;
				nLocalRadius = nRadius * 1.05;
				break;

			case Calendar.MINUTE:
				nRelativeTime = nValue/60;
				nLocalRadius = nRadius * 0.9;
				break;

			case Calendar.HOUR:
				nRelativeTime = nValue/12;
				nLocalRadius = nRadius * 0.8;
				break;

			default:
				return;
		}

		// Convert relative time to angle.
		final double nAngle = Math.PI * (2 - 2*nRelativeTime + 1);

		// Paint the clock hand.
		try
		{
			DrawShape(
				new Line2D.Double(
						nCenterX,
						nCenterY,
						nCenterX+Math.sin(nAngle)*nLocalRadius,
						nCenterY+Math.cos(nAngle)*nLocalRadius));
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
	}




	/** Store the given color in the given render state.
	 */
	private void SetColor (
			final RenderState aRenderState,
			final Color aColor)
	{
		aRenderState.DeviceColor = new double[]{
				aColor.getRed()/255.0,
				aColor.getGreen()/255.0,
				aColor.getBlue()/255.0,
				aColor.getAlpha()/255.0
		};
	}




	/** Callback from the sidebar layouter.
	 *  The returned size structure states that
	 *  - the minimum height is a fixed value
	 *  - the maximum height is at least the minimum size or, preferably, the width of the panel.
	 *  - the preferred height is the same as the maximum height.
	 */
	@Override
	public LayoutSize getHeightForWidth (final int nWidth)
	{
		final int nMinHeight = 20;
		final int nMaxHeight = Math.max(nWidth, nMinHeight);
		return new LayoutSize(nMinHeight, nMaxHeight, nMaxHeight);
	}




	/** Convert a Java Shape object into a series of canvas draw commands.
	 *  This takes advantage of the fact that every Java Swing shape can be converted
	 *  into a series of outline parts such as lines and bezier curves.
	 *  This allows us to paint every Java shape while only implementing a few outline primitives.
	 */
	private void DrawShape (
			final Shape aShape)
				throws IllegalArgumentException
	{
		final PathIterator aPathIterator = aShape.getPathIterator(null);
		final double[] aCoordinates = new double[6];
		double nX = 0;
		double nY = 0;
		while ( ! aPathIterator.isDone())
		{
			switch (aPathIterator.currentSegment(aCoordinates))
			{
				case PathIterator.SEG_CLOSE:
					break;

				case PathIterator.SEG_CUBICTO:
					// It looks like the canvas bezier curve definition is broken.
					// We have to swap the second control point and the end point of the curve.
					mxCanvas.drawBezier(
							new RealBezierSegment2D(
									nX,nY,
									aCoordinates[0], aCoordinates[1],
									aCoordinates[4], aCoordinates[5]),
							new RealPoint2D(aCoordinates[2], aCoordinates[3]),
							maViewState,
							maRenderState);
					nX = aCoordinates[4];
					nY = aCoordinates[5];
					break;

				case PathIterator.SEG_LINETO:
					mxCanvas.drawLine(
							new RealPoint2D(nX,nY),
							new RealPoint2D(aCoordinates[0], aCoordinates[1]),
							maViewState,
							maRenderState);
					nX = aCoordinates[0];
					nY = aCoordinates[1];
					break;

				case PathIterator.SEG_MOVETO:
					nX = aCoordinates[0];
					nY = aCoordinates[1];
					break;

				case PathIterator.SEG_QUADTO:
					mxCanvas.drawBezier(
							new RealBezierSegment2D(
									nX,nY,
									aCoordinates[0], aCoordinates[1],
									aCoordinates[0], aCoordinates[1]),
							new RealPoint2D(aCoordinates[2], aCoordinates[3]),
							maViewState,
							maRenderState);
					nX = aCoordinates[2];
					nY = aCoordinates[3];
					break;

				default:
					break;
			}

			aPathIterator.next();
		}
	}

	@Override
	public void dispose ()
	{
		maTimer.cancel();
		super.dispose();
	}

	private final XCanvas mxCanvas;
	private Size maWindowSize;
	private final Timer maTimer;
	private final ViewState maViewState;
	private final RenderState maRenderState;
	private int mnCurrentHour;
	private int mnCurrentMinute;
	private int mnCurrentSecond;
	private static Color maFaceColor = Color.GRAY;
	private static Color maHourHandColor = Color.BLACK;
	private static Color maMinuteHandColor = Color.BLACK;
	private static Color maSecondHandColor = Color.RED;
	@Override
	public int getMinimalWidth() {
		// TODO Auto-generated method stub
		return 0;
	}
}
