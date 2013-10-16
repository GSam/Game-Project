package network.interfaces;

import world.Inventory;
import world.entity.item.Item;

/**
 * Interface for interaction between the GUI and the network code.
 * 
 * @author Garming Sam 300198721
 * 
 */
public interface GUIObserver {

	/**
	 * Called on an item drop within the GUI.
	 * 
	 * @param i
	 *            item to drop
	 */
	public void onDropItem(Item i);

	/**
	 * Called on an item transfer within the GUI
	 * 
	 * @param inventory
	 *            inventory to move from
	 * @param chest
	 *            inventory to move to
	 * @param toMove
	 *            item to move
	 */
	public void onItemTransfer(Inventory inventory, Inventory chest, Item toMove);

	/**
	 * Called by the GUI when a chat message is sent.
	 * 
	 * @param text
	 *            text to send
	 */
	public void onChatMessage(String text);

	/**
	 * Called when a chest is accessed to allow chest network messages to
	 * disallow two players to manipulate a chest at the same time.
	 * 
	 * @param inventory
	 *            chest
	 * @param open
	 *            whether it is opened or closed
	 */
	public void onChestAccess(Inventory inventory, boolean open);

	/**
	 * Called when an item is right clicked.
	 * 
	 * @param i
	 *            item
	 */
	public void onRightClicked(Item i);

	/**
	 * Called when the game actually begins by the player.
	 */
	public void onStartGame();

}
