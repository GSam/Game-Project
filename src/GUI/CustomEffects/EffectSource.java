package GUI.CustomEffects;

import world.Actor;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.FocusHandler;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ElementRenderer;
import de.lessvoid.nifty.loaderv2.types.ElementType;
import de.lessvoid.nifty.spi.time.TimeProvider;


/**
 * @author Craig
 * 
 * Stores Actor values to be used within GuiEffects
 * 
 */
public class EffectSource extends Element{
	private Actor actor;
	private GuiEffectType type;
	
	public EffectSource(GuiEffectType type, Nifty newNifty, ElementType newElementType, String newId,
			Element newParent, FocusHandler newFocusHandler,
			boolean newVisibleToMouseEvents, TimeProvider newTimeProvider,
			ElementRenderer[] newElementRenderer) {
		super(newNifty, newElementType, newId, newParent, newFocusHandler,
				newVisibleToMouseEvents, newTimeProvider, newElementRenderer);
		
		this.type = type;
	}

	public GuiEffectType getType(){
		return this.type;
	}
	public void setActor(Actor actor){
		this.actor = actor;
	}
	public Actor getActor(){
		return this.actor;
	}
}
