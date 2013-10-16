package GUI.CustomEffects;

import world.Player;
import world.entity.item.Stat;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.effects.EffectImpl;
import de.lessvoid.nifty.effects.EffectProperties;
import de.lessvoid.nifty.effects.Falloff;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.render.NiftyRenderEngine;
import GUI.CustomEffects.GuiEffectType;

/**
 * Custom effect used to change the values of the orbs
 * on the HUD screen base panel
 * 
 * @author Craig
 */
public class OrbChanger implements EffectImpl {
	private Element element;
	private Player play;
	protected GuiEffectType type;
	protected Nifty nifty;
	
	/**Called when activating the effect
	 * @param nifty - the main nifty calling class
	 * @param element - the element triggering the effect
	 * @param parameter - the parameters passed in for the effect 
	 *  **/
	@Override
	public void activate(final Nifty nifty, final Element element, final EffectProperties parameter) {
		this.element = element;
		this.nifty = nifty;
	}
	
	/**Called when executing the effect
	 * @param element - the element triggering the effect
	 * @param normalisedTime - the the effect rendering time
	 * @param falloff - used for hover effects by EffectImpl
	 * @param r - the NiftyRenderEngine
	 *  **/
	@Override
	public void execute(final Element element, final float normalizedTime, final Falloff falloff, final NiftyRenderEngine r) {
		//changes the orb image to reflect the player's current stats
		int orbVal = calculateImagePos();
		NiftyImage image = null;
		if (this.type == GuiEffectType.HEALTHORBCHANGE){
			 image = nifty.createImage("Interface/HealthOrb/Orb"+orbVal+".png",false);
		}
		else{
			 image = nifty.createImage("Interface/EnergyOrb/Energy"+orbVal+".png",false);
		}
		if (image != null)
			changeElementImage(image);
	}
	
	/**Helper method for calculating the correct integer value representing the players 
	 * statistics for a given orb (health or energy)
	 * @return the integer value of the orb to be displayed
	 */
	protected int calculateImagePos(){
		for (Element el : element.getElements()){
			if (el instanceof EffectSource){
				if (((EffectSource)el).getType() == GuiEffectType.HEALTHORBCHANGE | ((EffectSource)el).getType() == GuiEffectType.ENERGYORBCHANGE ){
					play = (Player)((EffectSource) el).getActor();
					this.type = ((EffectSource)el).getType();
				}
			}
		}
		if (this.type == GuiEffectType.HEALTHORBCHANGE){
			float energy = play.getStats().getStat(Stat.HEALTH);
			float maxEnergy = play.getStats().getStat(Stat.MAXHEALTH);
			int retVal = 10-(int)((energy/maxEnergy)*10);
			if(retVal > 10) return 10;
			else if (retVal < 0) return 0;
			return retVal;
		}
		else{
			float health = play.getStats().getStat(Stat.ENERGY);
			float maxHealth = play.getStats().getStat(Stat.MAXENERGY);
			int retVal = 10-(int)((health/maxHealth)*10);
			if(retVal > 10) return 10;
			else if (retVal < 0) return 0;
			return retVal;
		}
	}
	
	@Override
	public void deactivate() {
	}
	
	/**Helper method for changing the image of an orb
	 */
	protected void changeElementImage(final NiftyImage image) {
		ImageRenderer imageRenderer = element.getRenderer(ImageRenderer.class);
		if (imageRenderer == null) {
			return;
		}
		imageRenderer.setImage(image);
	}
}