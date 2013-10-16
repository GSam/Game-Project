package world.entity.item.equippable;

import world.StatModification;
import world.entity.item.Item;
import world.entity.item.ItemInfo;

import com.jme3.asset.AssetManager;

/**
 * AbstractEquippable is an implementation of an Equippable. Its main function
 * currently is as a marker class for EquippableFactory.
 * 
 * @author Tony 300242775
 */
public abstract class AbstractEquippable extends Item implements Equippable {

	@Override
	public abstract EquipType getEquipType();

	@Override
	protected abstract void makeMesh(AssetManager assetManager);

	@Override
	protected abstract String getImage();

	@Override
	protected abstract StatModification makeItemStats();

	@Override
	protected abstract ItemInfo makeItemInfo();
}
