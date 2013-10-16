package GUI;

import java.util.ArrayList;

import GUI.Factories.BaseComponentFactory;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.dragndrop.builder.DroppableBuilder;


/** Sets up the panel for item containers (such as wallets)
 * This has a fixed location on screen and is dynamically filled 
 * by the contents of a Container's inventory when opened.
 * 
 * @author Craig Forret
 */
public class ContainerPanel {
	

	private ArrayList<String> dropSlots;
	private int screenWidth;
	private int screenHeight;

	public ContainerPanel(ArrayList<String> dropSlots, Nifty nifty){
		this.dropSlots = dropSlots;
		this.screenWidth = nifty.getRenderEngine().getWidth();
		this.screenHeight = nifty.getRenderEngine().getHeight();
	}
	
	
	/** Returns a PanelBuilder to build with the appropriate elements
	 * and drag drop slots
	 * 
	 * @return	The PanelBuilder with the Container elements to build
	 */
	public PanelBuilder makeContainer(){
		//set up container panel and position
		PanelBuilder invent = new PanelBuilder("ContainerPanel");
		invent.childLayoutOverlay();
		invent.x(""+(int)(screenWidth*.57));
		invent.y(""+(int)(screenHeight*.3));
		invent.width(""+(int)(screenWidth*.13));
		invent.height("30%");
		
		//set up container image and size
		ImageBuilder im = new ImageBuilder();
		im.alignRight();
		im.width("100%");
		im.height("100%");
		im.filename("Interface/Container.png");
		im.childLayoutAbsolute();
		
		//add container item slots
		addDropSlots(im);
		
		//set container text
		TextBuilder tb = new TextBuilder("ContainerText");
		tb.text("");
		tb.font("Interface/Fonts/ItemText.fnt");
		tb.wrap(true);
		tb.width(""+(int)(screenWidth*.13));
		tb.x(""+0);
		tb.y(""+(int)(screenHeight*.02));
		
		//add text to image, set non-visible before displayed
		im.text(tb);
		invent.image(im);
		invent.visible(false);
		
		return invent;
	}
	
	/** Helper method to add drop slots to the Container panel
	 * 
	 * @param im - the image to add the drop slots to
	 */
	private void addDropSlots(ImageBuilder im){
		
		int slotHeight = (int)(screenHeight*0.116);
		int slotWidth =  (int)(screenWidth*0.058);
		
		//adds slots in a 2x2 arrangement using the BaseComponentFactory
		int y = (int)(slotHeight*0.5);
		for (int i = 0; i < 2; i++){
			int x = (int)(slotWidth*0.12);
			for (int a = 0; a < 2; a++){
				String name = "ContPos"+i+"-"+a;
				DroppableBuilder drop = new DroppableBuilder(name);
				BaseComponentFactory.itemSlot(drop, name, "Interface/InventItemPos.png", "Interface/InventItemPosGlow.png",
						""+x, ""+y, ""+slotWidth, ""+slotHeight);
				im.control(drop);
				dropSlots.add(name);
				x+= slotWidth ;
			}
			y+=slotHeight;
		}
	}

}