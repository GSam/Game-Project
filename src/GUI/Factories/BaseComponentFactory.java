package GUI.Factories;

import de.lessvoid.nifty.builder.HoverEffectBuilder;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.controls.dragndrop.builder.DroppableBuilder;

/**
 * Sets up base item slots for screenbuilding
 * 
 * @author Craig Forret
 */
public class BaseComponentFactory{

	/**Creates an item slot based on a Droppable control
	 * Sets the appropriate image, configures layouts, and adds generic
	 * hover effect
	 * **/
	public static void itemSlot(DroppableBuilder panel, String name, String inactive, String active, String x, String y, String width, String height){
		
		//set the droppable spot at the correct location and give appropriate widht/height
		panel.x(x);
		panel.y(y);
		panel.width(width);
		panel.height(height);
		panel.childLayoutCenter();
		
		//set the image and appropriate dimensions
		ImageBuilder im = new ImageBuilder("Image"+name);
		im.width(width);
		im.height(height);
		im.childLayoutCenter();
		im.filename(inactive);
		
		//create basic hover effect
		HoverEffectBuilder onHover = new HoverEffectBuilder("changeImage");
		onHover.effectParameter("active", active);
		onHover.effectParameter("inactive", inactive);
		im.onHoverEffect(onHover);

		panel.image(im);

	}
	

}
