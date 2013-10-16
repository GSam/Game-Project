package GUI;

import java.util.ArrayList;

import GUI.Factories.BaseComponentFactory;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.dragndrop.builder.DroppableBuilder;

/**
 * @author Craig
 * 
 * Sets up the WorldChest screen for the HUD display. This screen
 * is displayed when a chest in the world environment is opened, and is dynamically 
 * populated by the contents of its inventory.
 * 
 */
public class WorldChestPanel {
	
	private ArrayList<String> dropSlots;
	private int screenWidth;
	private int screenHeight;
	
	public WorldChestPanel(ArrayList<String> dropSlots, Nifty nifty){
		this.dropSlots = dropSlots;
		this.screenWidth = nifty.getRenderEngine().getWidth();
		this.screenHeight = nifty.getRenderEngine().getHeight();
	}
	
	/**Creates the PanelBuilder used to build the world chest panel element
	 * on the HUD screen.
	 */
	public PanelBuilder makeWorldChest(){
		
		//create chest and set position and dimensions
		PanelBuilder chest = new PanelBuilder("WorldChest");
		chest.childLayoutVertical();
		chest.x(""+(int)(screenWidth*.40));
		chest.y(""+(int)(screenHeight*.1));
		chest.width(""+(int)(screenWidth*.30));
		chest.height("70%");
		
		//create image for hud screen and set dimensions
		ImageBuilder im = new ImageBuilder("WorldChestImagePanel");
		im.alignRight();
		im.width("100%");
		im.height("100%");
		im.filename("Interface/WorldChest.png");
		im.childLayoutAbsolute();
		
		//add the 4x45 drop slots for the worldchest
		addDropSlots(im);

		chest.image(im);

		//set initial state of world chest panel to closed.
		chest.visible(false);
		
		return chest;
	}
	
	/**Helper method for creating the drop slots (positions 
	 * you can drag and drop items on to).
	 * 
	 * @param im - the ImageBuilder building the wolrd chest control
	 */
	private void addDropSlots(ImageBuilder im){
		
		//set up coordinates of the slots and iterate creation
		int slotHeight = (int)(screenHeight*0.116);
		int slotWidth =  (int)(screenWidth*0.058);
		int y = (int)(slotHeight*1);
		for (int i = 0; i < 4; i++){
			int x = (int)(slotWidth*0.6);
			for (int a = 0; a < 4; a++){
				//create droppable slots using the BaseComponentFactory
				DroppableBuilder drop = new DroppableBuilder("ChestPos"+i+"-"+a);
				BaseComponentFactory.itemSlot(drop, "ChestPos"+i+"-"+a,"Interface/InventItemPos.png", "Interface/InventItemPosGlow.png",
						""+x, ""+y, ""+slotWidth, ""+slotHeight);
				im.control(drop);
				dropSlots.add("ChestPos"+i+"-"+a);
				x+= slotWidth ;
			}
			y+=slotHeight;
		}
	}
}
