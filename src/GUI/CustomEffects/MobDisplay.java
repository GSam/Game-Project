package GUI.CustomEffects;

import world.entity.item.Stat;
import world.entity.mob.Mob;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.effects.EffectImpl;
import de.lessvoid.nifty.effects.EffectProperties;
import de.lessvoid.nifty.effects.Falloff;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.render.NiftyRenderEngine;
import de.lessvoid.nifty.tools.Color;

/**
 * Custom effect used to change MobDisplay images, names,
 * and health bar for the MobDisplay on the HUDscreen
 * @author Craig
 */

public class MobDisplay implements EffectImpl {
	private Color color = new Color("#ff0000");;
	private Element element;
	private Mob mob;
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
	public void execute(
			final Element element,
			final float normalizedTime,
			final Falloff falloff,
			final NiftyRenderEngine r) {

		//Get the mob image and positioning for the image
		String mobImage = getMobImage();
		int screenWidth = nifty.getRenderEngine().getWidth();
		int screenHeight = nifty.getRenderEngine().getHeight();
		int xPos = (int)(screenWidth*0.05);
		int yPos = (int)(screenHeight*0.16);
		int height = (int)(screenHeight*0.02);
		
		//determine the width of the mob health bar by determining
		//the mob's maxhealth and health
		float mobHealth = mob.getStats().getStat(Stat.HEALTH);
		float mobMaxHealth = mob.getStats().getStat(Stat.MAXHEALTH);
		int width = (int)((mobHealth/mobMaxHealth)*(screenWidth*0.22));
		
		//render the mob health bar
		r.setColor(color);
		r.renderQuad(xPos,yPos,width,height);
		
		//get the mob text element and set the name of the mob
		Element mobText = nifty.getScreen("hud").findElementByName("MobText");
		mobText.getRenderer(TextRenderer.class).setText(mob.getName());
		
		//set the mob's image 
		NiftyImage image = nifty.createImage("Interface/"+mobImage,false);
		
		changeElementImage(image);
	}

	public void deactivate() {}
	
	private void changeElementImage(final NiftyImage image) {
		ImageRenderer imageRenderer = element.getRenderer(ImageRenderer.class);
		if (imageRenderer == null) {
			return;
		}
		imageRenderer.setImage(image);
	}
	
	/**Helper method for finding the Actor to display **/
	private String getMobImage(){
		//find the appropriate EffectSource element and Actor to display
		String mobImage = null;
		for (Element el : element.getElements()){
			if (el instanceof EffectSource){
				if (((EffectSource)el).getType() == GuiEffectType.MOBCHANGE){
					mob = (Mob)((EffectSource) el).getActor();
					mobImage = mob.getImage();;
				}
			}
		}
		return mobImage;
	}
}