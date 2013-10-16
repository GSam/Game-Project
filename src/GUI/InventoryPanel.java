package GUI;

import java.util.ArrayList;

import GUI.Factories.BaseComponentFactory;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.dragndrop.builder.DroppableBuilder;

/**
 * @author Craig Forret
 * 
 * Generates the Inventory panel for the HUD screen
 * 
 */
public class InventoryPanel {
	
	private ArrayList<String> dropSlots;
	private int screenWidth;
	private int screenHeight;

	public InventoryPanel(ArrayList<String> dropSlots, Nifty nifty){
		this.dropSlots = dropSlots;
		this.screenWidth = nifty.getRenderEngine().getWidth();
		this.screenHeight = nifty.getRenderEngine().getHeight();
	}
	
	/** Returns a PanelBuilder to build the Inventory panel with the appropriate elements
	 * and drop slots
	 * 
	 * @return	The PanelBuilder with the Inventory panel elements to build
	 */
	public PanelBuilder makeInventory(){
		
		//create panel and set location and size
		PanelBuilder invent = new PanelBuilder("InventoryPanel");
		invent.childLayoutVertical();
		invent.x(""+(int)(screenWidth*.70));
		invent.y(""+(int)(screenHeight*.1));
		invent.width(""+(int)(screenWidth*.25));
		invent.height("70%");
		
		//create the inventory panel image and set size
		ImageBuilder im = new ImageBuilder("InventoryPaneliamge");
		im.alignRight();
		im.width("100%");
		im.height("100%");
		im.filename("Interface/InventoryPanel2.png");
		im.childLayoutAbsolute();
		
		//add the drop slots to the inventory panel;s
		addDropSlots(im);
		invent.image(im);
		
		//set non-visible to begin with
		invent.visible(false);
		
		return invent;
	}
	
	/**Construct the drop slots for the Inventory Panel
	 * 
	 * @param im - the ImageBuilder toconstruct the drop slots on
	 */
	private void addDropSlots(ImageBuilder im){
		
		int slotHeight = (int)(screenHeight*0.116);
		int slotWidth =  (int)(screenWidth*0.058);
		int y = (int)(slotHeight*1.1);
		
		//Create the 4x4 drop slots for the InventoryPanel using the BaseComponentFactory
		for (int i = 0; i < 4; i++){	
			int x = (int)(slotWidth*0.2);
			for (int a = 0; a < 4; a++){
				DroppableBuilder drop = new DroppableBuilder("InventPos"+i+"-"+a);
				BaseComponentFactory.itemSlot(drop, "InventPos"+i+"-"+a, "Interface/InventItemPos.png", "Interface/InventItemPosGlow.png",
						""+x, ""+y, ""+slotWidth, ""+slotHeight);
				im.control(drop);
				dropSlots.add("InventPos"+i+"-"+a);
				x+= slotWidth ;
			}
			y+=slotHeight;	
		}	
	}
}
