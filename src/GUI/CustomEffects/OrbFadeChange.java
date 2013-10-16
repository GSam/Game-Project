package GUI.CustomEffects;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.effects.EffectProperties;
import de.lessvoid.nifty.effects.Falloff;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.render.NiftyRenderEngine;
import de.lessvoid.nifty.tools.Alpha;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.LinearInterpolator;

/**
 * Extends the OrbChanger class and displays a fade effect in addition
 * to changing the orb image according to the Actor's stats. Used 
 * in conjunction with the OrbChanger effect on the base panel to give the effect
 * of the health orb 'draining'
 * 
 * @author Craig Forret
 * 
 */
public class OrbFadeChange extends OrbChanger {

		private Alpha start = Alpha.ZERO;
		private Alpha end = Alpha.FULL;
		private LinearInterpolator interpolator;
		
		/**Called when activating the effect
		 * @param nifty - the main nifty calling class
		 * @param element - the element triggering the effect
		 * @param parameter - the parameters passed in for the effect 
		 *  **/
		@Override
		public void activate(final Nifty nifty, final Element element, final EffectProperties parameter) {
			super.activate(nifty, element, parameter);
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

		/**Called when executing the effect
		 * @param element - the element triggering the effect
		 * @param normalisedTime - the the effect rendering time
		 * @param falloff - used for hover effects by EffectImpl
		 * @param r - the NiftyRenderEngine
		 *  **/
		@Override
		public void execute(
				final Element element,
				final float normalizedTime,
				final Falloff falloff,
				final NiftyRenderEngine r) {
			if (interpolator != null) {
				//fades the orb image based on the interpolator value given the time input
				r.setColorAlpha(interpolator.getValue(normalizedTime));
			} else {
				Alpha a = start.linear(end, normalizedTime);
				r.setColorAlpha(a.getAlpha());
			}
		}

		@Override
		public void deactivate() {
			int orbVal = super.calculateImagePos();
			NiftyImage image = null;
			if(this.type == GuiEffectType.HEALTHORBCHANGE){
				image = nifty.createImage("Interface/HealthOrb/Orb"+orbVal+".png",false);
			}
			else{
				image = nifty.createImage("Interface/EnergyOrb/Energy"+orbVal+".png",false);
			}
			if(image != null)
			super.changeElementImage(image);
		}
		

}
