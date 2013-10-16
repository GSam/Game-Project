package GUI;

import java.util.ArrayList;

import world.entity.item.equippable.EquipType;
import GUI.Factories.BaseComponentFactory;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.dragndrop.builder.DroppableBuilder;

/**
 * @author Craig Forret
 * 
 * Sets up the equipment panel for the Head's up screen
 * 
 */
public class CharacterEquipPanel {
	
	private ArrayList<String> dropSlots;
	private int screenWidth;
	private int screenHeight;
	
	public CharacterEquipPanel(ArrayList<String> dropSlots, Nifty nifty){
		this.screenWidth = nifty.getRenderEngine().getWidth();
		this.screenHeight = nifty.getRenderEngine().getHeight();
		this.dropSlots = dropSlots;
	}
	
	/** Builds returns the PanelBuilder with the EquipmentPanel elements 
	 * to be built by the HudScreen
	 * @return
	 */
	public PanelBuilder makePanel(){
		
		//Create the panel and set location
		PanelBuilder equip = new PanelBuilder("CharEquip");
		equip.x(""+(int)(screenWidth*.05));
		equip.y(""+(int)(screenHeight*.1));
		equip.width(""+(int)(screenWidth*.35));
		equip.height(""+(int)(screenHeight*.72));
		equip.childLayoutVertical();
		equip.visible(false);
		
		//Create the equip panel image and set location
		ImageBuilder im = new ImageBuilder("CharEquipVisuals");
		im.alignRight();
		im.width("100%");
		im.height("100%");
		im.filename("Interface/CharEquipv3.png");
		im.childLayoutAbsolute();
		
		
		//set the text and positioning of text elements on the equip image
		im.text(makeText("CharDamage","DAMAGE", (int)(screenWidth*0.035),(int)(screenHeight*0.6), true));
		im.text(makeText("CharArmour","ARMOUR", (int)(screenWidth*0.035),(int)(screenHeight*0.625), true));
		im.text(makeText("CharEnergy","ENERGY", (int)(screenWidth*0.035),(int)(screenHeight*0.65), true));
		im.text(makeText("CharHealth","HEALTH", (int)(screenWidth*0.17),(int)(screenHeight*0.6), true));
		im.text(makeText("CharMaxHealth","MAX HEALTH", (int)(screenWidth*0.17),(int)(screenHeight*0.625), true));
		im.text(makeText("CharMaxEnergy","MAX ENERGY", (int)(screenWidth*0.17),(int)(screenHeight*0.65), true));
		
		//establish the equipment 'shake' effect
		im.onCustomEffect(new EffectBuilder("shake"){{
			customKey("shaker");
			effectParameter("distance","2");
			effectParameter("global","false");
			length(500);
		}});

		//generate the drag drop slots for the equip positions
		makeEquipSlots(im);
		equip.image(im);
		
		return equip;
	}
	
	
	/**Helper method: Returns appropriate text for the stat slot
	 * 
	 * **/
	private TextBuilder makeText(String name, String type, int x, int y, boolean left){
		TextBuilder equipText = new TextBuilder(name);
		equipText.font("Interface/Fonts/CharEquip.fnt");
		if(left){
			equipText.textHAlignLeft();
		}
		equipText.alignLeft();
		equipText.x(""+x);
		equipText.y(""+y);
		equipText.text(type);
		return equipText;
	}
	
	/**Adds the DroppableBuilder elements to the main equip image 
	 * 
	 * @param im the ImageBuilder to add the Droppable slots to
	 */
	private void makeEquipSlots(ImageBuilder im){
		
		int equipCount = 1;
		
		//head slot
		String slotName = "EquipPos"+EquipType.HEAD+equipCount++;
		dropSlots.add(slotName);
		DroppableBuilder drop = new DroppableBuilder(slotName);
		BaseComponentFactory.itemSlot(drop, slotName, "Interface/EquipPanelHead.png", "Interface/EquipPanelHeadGlow.png", ""+(int)(screenWidth*0.125), 
				""+(int)(screenHeight*0.042), ""+(int)(screenWidth*0.085), ""+(int)(screenHeight*0.089));
		im.control(drop);
		
		//shoulder slot
		slotName = "EquipPos"+EquipType.CHEST+equipCount++;
		dropSlots.add(slotName);
		drop = new DroppableBuilder(slotName);
		BaseComponentFactory.itemSlot(drop, slotName, "Interface/EquipPanelChest.png", "Interface/EquipPanelChestGlow.png", ""+(int)(screenWidth*0.018), 
				""+(int)(screenHeight*0.162), ""+(int)(screenWidth*0.085), ""+(int)(screenHeight*0.089));
		im.control(drop);
		
		//weapon slot
		slotName = "EquipPos"+EquipType.WEAPON+equipCount++;
		dropSlots.add(slotName);
		drop = new DroppableBuilder(slotName);
		BaseComponentFactory.itemSlot(drop, slotName, "Interface/EquipPanelWeapon.png", "Interface/EquipPanelWeaponGlow.png", ""+(int)(screenWidth*0.018), 
				""+(int)(screenHeight*0.285), ""+(int)(screenWidth*0.085), ""+(int)(screenHeight*0.089));
		im.control(drop);
		
		//leg slot
		slotName = "EquipPos"+EquipType.LEGS+equipCount++;
		dropSlots.add(slotName);
		drop = new DroppableBuilder(slotName);
		BaseComponentFactory.itemSlot(drop, slotName, "Interface/EquipPanelLegs.png", "Interface/EquipPanelLegsGlow.png", ""+(int)(screenWidth*0.018), 
				""+(int)(screenHeight*0.429), ""+(int)(screenWidth*0.085), ""+(int)(screenHeight*0.089));
		im.control(drop);
		
		//right side
		//neck slot
		slotName = "EquipPos"+EquipType.NECK+equipCount++;
		dropSlots.add(slotName);
		drop = new DroppableBuilder(slotName);
		BaseComponentFactory.itemSlot(drop, slotName,"Interface/EquipPanelNeck.png", "Interface/EquipPanelNeckGlow.png", ""+(int)(screenWidth*0.248), 
				""+(int)(screenHeight*0.149), ""+(int)(screenWidth*0.085), ""+(int)(screenHeight*0.089));
		im.control(drop);
		
		//belt slot
		slotName = "EquipPos"+EquipType.BELT+equipCount++;
		dropSlots.add(slotName);
		drop = new DroppableBuilder(slotName);
		BaseComponentFactory.itemSlot(drop, slotName,"Interface/EquipPanelBelt.png", "Interface/EquipPanelBeltGlow.png", ""+(int)(screenWidth*0.248), 
				""+(int)(screenHeight*0.285), ""+(int)(screenWidth*0.085), ""+(int)(screenHeight*0.089));
		im.control(drop);
		
		//boot slot
		slotName = "EquipPos"+EquipType.BOOTS+equipCount++;
		dropSlots.add(slotName);
		drop = new DroppableBuilder(slotName);
		BaseComponentFactory.itemSlot(drop, slotName,"Interface/EquipPanelBoots.png", "Interface/EquipPanelBootsGlow.png", ""+(int)(screenWidth*0.248), 
				""+(int)(screenHeight*0.429), ""+(int)(screenWidth*0.085), ""+(int)(screenHeight*0.089));
		im.control(drop);
	}

}
