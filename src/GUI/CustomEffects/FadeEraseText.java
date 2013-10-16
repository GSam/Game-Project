package GUI.CustomEffects;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.effects.EffectImpl;
import de.lessvoid.nifty.effects.EffectProperties;
import de.lessvoid.nifty.effects.Falloff;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyRenderEngine;
import de.lessvoid.nifty.tools.Alpha;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.LinearInterpolator;

/**
 * @author Craig
 * 
 * Effect implementation that displays, then fades text, and finally
 * removes the text from the text element
 * s
 */
public class FadeEraseText implements EffectImpl {

		private Element el;
		private Alpha start = Alpha.ZERO;
		private Alpha end = Alpha.FULL;
		private LinearInterpolator interpolator;
		
		@Override
		/**Called when activating the effect
		 * @param nifty - the main nifty calling class
		 * @param element - the element triggering the effect
		 * @param parameter - the parameters passed in for the effect 
		 *  **/
		public void activate(final Nifty nifty, final Element element, final EffectProperties parameter) {
			this.el = element;
			if (parameter.getProperty("startColor") != null) {
				start = new Alpha(new Color(parameter.getProperty("startColor", "#000000ff")).getAlpha());
			}
			if (parameter.getProperty("endColor") != null) {
				end = new Alpha(new Color(parameter.getProperty("endColor", "#ffffffff")).getAlpha());
			}
			if (parameter.getProperty("start") != null) {
				start = new Alpha(parameter.getProperty("start"));
			}
			if (parameter.getProperty("end") != null) {
				end = new Alpha(parameter.getProperty("end"));
			}
			interpolator = parameter.getInterpolator();
		}

		@Override
		/**Called when executing the effect
		 * @param element - the element triggering the effect
		 * @param normalisedTime - the the effect rendering time
		 * @param falloff - used for hover effects by EffectImpl
		 * @param r - the NiftyRenderEngine
		 *  **/
		public void execute(
				final Element element,
				final float normalisedTime,
				final Falloff falloff,
				final NiftyRenderEngine r) {
			if (interpolator != null) {
				r.setColorAlpha(interpolator.getValue(normalisedTime));
			} else {
				Alpha a = start.linear(end, normalisedTime);
				r.setColorAlpha(a.getAlpha());
			}
		}

		@Override
		public void deactivate() {
			el.getRenderer(TextRenderer.class).setText("");
		}

}
