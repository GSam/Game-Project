package world;

import world.entity.item.Item;

/**
 * @author Alex Campbell 300252131
 */
public interface InventoryObserver {
	public boolean addItem(Item item);
	public boolean addItemInPos(Item item, String position);
}
