package GUI.Managers;

import java.util.HashSet;

import world.Inventory;
import world.Player;
import world.PlayerInventoryObserver;
import world.entity.item.Item;
import world.entity.item.Stat;
import GUI.Factories.InventoryItemFactory;
import GUI.ScreenControllers.HudScreenController;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;


/** This InventoryManager is entrance point to the screen controllers. It is to be used to add new items to
 * the inventory of the HUD screen. It acts as a listener for changes to the PlayerInventory and calls to open and close
 * chests.
 * @author Craig Forret
 */

public class InventoryManager implements PlayerInventoryObserver {


	private HudScreenController hudControl;
	private Player player;
	private Nifty nifty;
	private ScreenManager screenManager;
	private Inventory worldChest;
	
	public InventoryManager(HudScreenController itemControl, Nifty nifty, ScreenManager screenManager){
		this.hudControl = itemControl;
		this.nifty = nifty;
		this.screenManager = screenManager;
	}

	/**Adds an item representation to the HUD screen
	 * @param - the item to add
	 */
	public boolean addItem(Item item) {
		for (String position: hudControl.getPositions()){
			if(!itemInPosition(position,null) && position.startsWith("InventPos")){
				return addItemInPos(item, position);
			}
		}
		return false;
	}
	
	/**Adds an item to a specific inventory position. Uses the InventoryItem factory to construct
	 * and bind the item.
	 * @param item - the Item to add
	 * @param position - the id of the GUI element to bind to
	 * @return true if added, false otherwise
	 */
	public boolean addItemInPos(Item item, String position){
		Element el = hudControl.getNifty().getScreen(hudControl.getScreenName()).findElementByName(position);
		InventoryItemFactory.createItem(el,item,hudControl.getNifty());
		item.setInventoryPosition(position);
		return true;
	}

	/**Adds an item to a specific chest given the prefix for the location of the item on the HUD
	 * 
	 * @param item - the item to add
	 * @param posPrefix
	 * @return true if added, false otherwise 
	 */
	public boolean addChestItem(Item item, String posPrefix) {
		for (String position: hudControl.getPositions()){
			if(!itemInPosition(position,item.getInventory()) && position.startsWith(posPrefix)){
				Element el = hudControl.getNifty().getScreen(hudControl.getScreenName()).findElementByName(position);
				InventoryItemFactory.createItem(el,item,hudControl.getNifty());
				item.setInventoryPosition(position);
				return true;
			}
		}
		return false;
	}
	
	/**Displays a chest given the inventory containing the items within that chest
	 * @param invent - the Inventory to display in the chest HUD area
	 */
	public boolean displayChest(Inventory invent){
		this.worldChest = invent;
		HashSet<String> posSet = new HashSet<String>();
		for (Item item : invent){
			if (item.getInventoryPosition()!=null){
				if (posSet.contains(item.getInventoryPosition())){
					addChestItem(item,"Chest");
				} 
				else
				addItemInPos(item,item.getInventoryPosition());
			}
			else{
				addChestItem(item,"Chest");
			}
			posSet.add(item.getInventoryPosition());
		}
		screenManager.getHudScreenController().showWorldChest();
		return true;
	}
	
	/**Loads a container into the HUD screen container area on given
	 * the Inventory containing the container's items.
	 * 
	 * @param container
	 * @return true if loaded, false otherwise
	 */
	public boolean loadContainer(Inventory container){
		if (container.getItems().size() > 4) throw new AssertionError("Container has too many items");
		HashSet<String> posSet = new HashSet<String>();
		for (Item item : container){
			if (item.getInventoryPosition()!=null){
				if (posSet.contains(item.getInventoryPosition())){
					addChestItem(item,"Cont");
				} 
				else
				addItemInPos(item,item.getInventoryPosition());
			}
			else{
				addChestItem(item,"Cont");
			}
			posSet.add(item.getInventoryPosition());
		}
		return true;
	}
	
	/**Hides a given container, and removes the GUI elements associated to its item
	 * contents
	 * @param container- the container 
	 * @return
	 */
	public boolean hideContainer(Inventory container){
		for (Item item : container){
			Element toRemove = nifty.getCurrentScreen().findElementByName("ItemVal"+item.getId());
			if (toRemove == null) throw new AssertionError("Trying to remove container element"
					+ "that does not exist within GUI");
			cleanRemove(toRemove);
		}
		return true;
	}
	
	/**Hides an open world chest 
	 */
	public boolean hideChest(){

		for (Item item : worldChest){
			Element toRemove = nifty.getCurrentScreen().findElementByName("ItemVal"+item.getId());
			cleanRemove(toRemove);
		}
		screenManager.getHudScreenController().hideWorldChest();
		return true;
	}
	
	/**Calls the associated player to equip a given item
	 * 
	 * @param item- the item to equip
	 * @return false if the item is null, true otherwise
	 */
	public boolean equip(Item item){
		if(item!=null){
			player.equip(item);
			return true;
		}
		return false;
	}
	
	/**Unequips a given item
	 * 
	 * @param item the item to unequip
	 */
	public void unequip(Item item){
		if(item != null){
			player.unequip(item);
		}
	}
	
	/** Removes the representation of the argument item from the GUI.
	 * The inventory position of the item is set to null. If being 
	 * removed from a chest, the item needs to be removed from its inventory also.
	 * 
	 * @param item the item to remove from the gui.
	 */
	public void removeItem(Item item){
		Element toRemove = nifty.getScreen("hud").findElementByName("ItemVal"+item.getId());
		screenManager.getHudScreenController().cleanRemove(toRemove);
		item.setInventoryPosition(null);
	}
	
	/**Calls drop on the associated player inventory for a given item
	 * 
	 * @param item the item to drop
	 */
	public void dropItem(Item item){
		item.setInventoryPosition(null);
		player.getContainerInventory().dropItem(item);
	}
	
	/**Sets the player field for this Inventory Manager
	 * 
	 * @param player the player to set
	 */
	public void setPlayer(Player player) {
		if(this.player != null) {
			this.player.removeInventoryObserver(this);
			for(Item i : this.player.getContainerInventory())
				removeItem(i);
		}
		this.player = player;
		if(player != null) {
			player.addInventoryObserver(this);
			for(Item i : player.getContainerInventory())
				if(i.getInventoryPosition() != null)
					addItemInPos(i, i.getInventoryPosition());
				else
					addItem(i);
		}
	}
	
	/** Returns the set player*/
	public Player getPlayer(){
		return this.player;
	}
	
	/**Returns any world chest currently open*/
	public Inventory getOpenWorldChest(){
		return this.worldChest;
	}
	
	/**Processes an element and appopriately removes it from
	 * its nifty bindings
	 * @param element - the element to remove
	 */
	public void cleanRemove(Element element){
		for(Element el : element.getElements()){
			nifty.removeElement(nifty.getCurrentScreen(),el);
		}
		nifty.removeElement(nifty.getScreen("hud"), element);
		nifty.executeEndOfFrameElementActions();
	}
	
	/**Helper method to determine whether an item is in a given position
	 * used to avoid 'stacking' in inventories
	 * @param position - the position to check
	 * @param invent - any associated inventory
	 * @true if an item exists in position already
	 */
	private boolean itemInPosition(String position, Inventory invent){
		if (position.startsWith("Invent")||position.startsWith("Equip")){
			for (Item it : player.getContainerInventory()){
				if(it.getInventoryPosition() != null && it.getInventoryPosition().equals(position)){
					return true;
				}
			}
		}
		else if (position.startsWith("Cont") && invent != null){
			for (Item it : invent){
				if(it.getInventoryPosition() != null && it.getInventoryPosition().equals(position)){
					return true;
				}
			}
		}
		else if (position.startsWith("Chest") && worldChest != null){
			for (Item it : worldChest){
				if(it.getInventoryPosition() != null && it.getInventoryPosition().equals(position)){
					return true;
				}
			}
		}
		return false;
	}

	
}
