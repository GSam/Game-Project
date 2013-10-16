package world.entity.item.container;

import java.io.IOException;

import world.Container;
import world.Inventory;
import world.StatModification;
import world.entity.item.Item;
import world.entity.item.ItemInfo;
import world.entity.item.ItemType;
import world.entity.item.RightClickable;
import world.entity.item.Stat;

import com.jme3.asset.AssetManager;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;

/**
 * AbstractContainerItem represents an inventory item that is also a container, for example
 * a Wallet.
 * 
 * @author Tony 300242775
 */
public abstract class AbstractContainerItem extends Item implements RightClickable, Container {

	/**
	 * The inventory of this AbstractContainerItem
	 */
	protected Inventory containerInventory;
	
	public AbstractContainerItem () {
		containerInventory = new Inventory (this,16);
	}
	
	@Override
	public void rightClick() {
		// TODO hook goes here.
	}
	
	@Override
	public Inventory getContainerInventory(){
		return this.containerInventory;
	}

	@Override
	protected abstract void makeMesh(AssetManager assetManager);

	@Override
	protected abstract String getImage();

	@Override
	protected StatModification makeItemStats() {
		Stat keys[] = new Stat[] {};
		float values[] = new float[] {};
		return new StatModification(keys, values);
	}

	@Override
	protected abstract ItemInfo makeItemInfo();
	
	
	@Override
	public void write(JmeExporter arg0) throws IOException {
		super.write(arg0);
		arg0.getCapsule(this).write(containerInventory, "inventory", null);
	}
	
	@Override
	public void read(JmeImporter arg0) throws IOException {
		super.read(arg0);
		containerInventory = (Inventory)arg0.getCapsule(this).readSavable("inventory", null);
	}

	@Override
	public ItemType getType() {
		return ItemType.WALLET;
	}
}
