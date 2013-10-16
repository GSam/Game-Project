package GUI.Factories;

import java.util.HashMap;

import world.entity.item.Item;
import world.entity.item.ItemDisplayTuple;
import world.entity.item.ItemType;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.HoverEffectBuilder;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.dragndrop.builder.DraggableBuilder;
import de.lessvoid.nifty.elements.Element;

/**
 * Generates GUI representations of images and binds them
 * to the given GUI screen position
 * 
 * @author Craig Forret
 */
public class InventoryItemFactory {
	
	static HashMap<ItemType,String> imageMap = new HashMap<ItemType,String>();
	
	static{
		imageMap.put(ItemType.TECHNO,"TestItem.png");
		imageMap.put(ItemType.SCOTTISH, "ScottsMoonBoots.png");
		imageMap.put(ItemType.GARMINGISH, "GarmingsNetwork.png");
		imageMap.put(ItemType.POTION, "HealthVial.png");
		imageMap.put(ItemType.MAP, "map.png");
		imageMap.put(ItemType.SHOTGUN, "shotgun.png");
		imageMap.put(ItemType.SNIPER, "sniper.png");
		imageMap.put(ItemType.WALLET, "wallet.png");
		imageMap.put(ItemType.GIZMO, "gizmo.png");
		imageMap.put(ItemType.KEY, "key.png");
		imageMap.put(ItemType.TORCH, "torch.png");
	}

	/**Creates a GUI item based on the item's details, and binds it to the parent element
	 * given. Adds 'hoverstats' panel. The bound element is a Draggable with panel and 
	 * image sub-elements.
	 * 
	 * @param parent - Element to bind to
	 * @param item - Item to represent
	 * @param nifty - Main nifty class
	 */
	public static void createItem (Element parent, Item item, Nifty nifty){
		
		//Set up draggable as panel to hold item
		DraggableBuilder drag = new DraggableBuilder("ItemVal"+item.getId());
		drag.childLayoutCenter();
		drag.width(parent.getWidth()+"");
		drag.height(parent.getHeight()+"");
		drag.visibleToMouse(true);
		
		//Set image for item
		ImageBuilder itemImage = new ImageBuilder();
		itemImage.filename("Interface/"+imageMap.get(item.getType()));
		itemImage.width(""+(int)(parent.getWidth()*1.1));
		itemImage.height(""+(int)(parent.getHeight()*1.1));
		itemImage.childLayoutVertical();
		drag.image(itemImage);
		
		addHoverPanel(drag,item,nifty);
		
		drag.build(nifty,nifty.getScreen("hud"), parent);
		drag.renderOrder(1000);
	}
	
	
	/**
	 * Adds slot elements to a given Draggable for a container
	 * @param drag - the Draggable object to build slots upon
	 * @param item - the item with the id associated to the container slot
	 * @param nifty - the main nifty element
	 */
	public static void addContainerSlots(DraggableBuilder drag, Item item, Nifty nifty){
		
		int screenWidth = nifty.getRenderEngine().getWidth();
		int screenHeight = nifty.getRenderEngine().getHeight();
		
		PanelBuilder superPan = new PanelBuilder("container-"+item.getId());
		superPan.childLayoutAbsolute();
		
		//build the hover stats
		PanelBuilder pan = new PanelBuilder();
		pan.backgroundColor("#ffff");
		
		//set properties for popup panel
		pan.childLayoutAbsolute();
		
		pan.width(""+(int)(screenWidth*0.1));
		pan.height(""+(int)(screenHeight*0.25));
		pan.x(""+-(int)(screenWidth*0.2));
		pan.y(""+-(int)(screenWidth*0.2));
		
		superPan.panel(pan);
		superPan.renderOrder(1000);
		superPan.visible(true);	//don't show popup on creation
		drag.panel(superPan);
		
	}
	
	/**
	 * Adds the mouse-over hover panel to the given Draggable. Hover panel displays 
	 * item stats.
	 * 
	 * @param drag - the Draggable object to build the hover stats upon
	 * @param item - the Item with stats to display
	 * @param nifty - the main Nifty class
	 */
	public static void addHoverPanel(DraggableBuilder drag, Item item, Nifty nifty){
		//set hover effects for the popup stats
		HoverEffectBuilder onHover = new HoverEffectBuilder("show");
		onHover.effectParameter("targetElement", "hoverstats-"+item.getId());
		HoverEffectBuilder offHover = new HoverEffectBuilder("hide");
		offHover.effectParameter("targetElement", "hoverstats-"+item.getId());
		drag.onHoverEffect(onHover);
		drag.onEndHoverEffect(offHover);
		
		//add elements to the base draggable
		drag.visible(true);
		drag.x(""+0);
		drag.y(""+0);
		
		PanelBuilder superPan = new PanelBuilder("hoverstats-"+item.getId());
		superPan.childLayoutAbsolute();
		
		//build the hover stats
		PanelBuilder pan = new PanelBuilder();
		pan.backgroundColor("#969a");
	
		addItemStats(pan,item,nifty);
		
		//set properties for popup panel
		pan.childLayoutAbsolute();
		pan.width("200");
		pan.height("100");
		pan.x("-150");
		pan.y("-100");

		superPan.panel(pan);
		superPan.renderOrder(1000);
		superPan.visible(false);	//don't show popup on creation
		drag.panel(superPan);
	}
	
	/**
	 * Adds particular stats to a hover panel given an item 
	 * 
	 * @param panel - the panel to add stats to
	 * @param item - the item with stats to add
	 * @param nifty - the main Nifty class
	 */
	public static void addItemStats(PanelBuilder panel, Item item, Nifty nifty){
		
		ImageBuilder im = new ImageBuilder();
		im.childLayoutVertical();
		
		//set image for text
		im.y("50");
		im.x("0");
		
		//build image stats on text
		im.backgroundColor("#0002");
		TextBuilder tb = new TextBuilder();
		String stats = " ";
		
		for (ItemDisplayTuple p : item) {
			stats += p.stat+": "+p.string+"\n ";	
		}
		
		//set text properties
		addText(im, stats);
		tb.font("Interface/Fonts/ItemText.fnt");
		tb.wrap(true);
		tb.textHAlignLeft();
		im.text(tb);
		panel.image(im);
		
	}
	
	/**Private helper method for adding text**/
	private static void addText(ImageBuilder im, String text){
		TextBuilder tb = new TextBuilder();
		tb.text(text);
		tb.font("Interface/Fonts/ItemText.fnt");
		tb.wrap(true);
		tb.textHAlignLeft();
		im.text(tb);
	}
}
