package world.entity.item.consumable;

import world.StatModification;
import world.entity.item.Item;
import world.entity.item.ItemInfo;
import world.entity.item.ItemType;
import world.entity.item.RightClickable;

import com.jme3.asset.AssetManager;

/**
 * AbstractConsumable represents all Consumables in the game, Items that can
 * be consumed for effects to your stats (eg. a health boost).
 * 
 * @author Tony 300242775
 */
public abstract class AbstractConsumable extends Item implements RightClickable {

	@Override
	public abstract void rightClick();

	@Override
	protected abstract void makeMesh(AssetManager assetManager);

	@Override
	protected abstract String getImage();

	@Override
	protected abstract StatModification makeItemStats();

	@Override
	protected abstract ItemInfo makeItemInfo();
	
	@Override
	public ItemType getType() {
		return ItemType.POTION;
	}
}
