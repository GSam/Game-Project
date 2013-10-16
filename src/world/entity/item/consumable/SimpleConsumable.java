package world.entity.item.consumable;

import java.io.IOException;
import java.util.Set;

import world.Actor;
import world.StatModification;
import world.WorldType;
import world.effects.StatChange;
import world.entity.item.ItemInfo;
import world.entity.item.Stat;

import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * SimpleConsumable provides an implementation of AbstractConsumable that applies a
 * direct, permanent stat change to the consumer according to the values passed at
 * construction.
 * 
 * This class should not be instantiated directly, instead instances of it should be
 * supplied by ConsumableFactory.
 * 
 * @author Tony 300242775
 */
public class SimpleConsumable extends AbstractConsumable {
	private String meshPath;

	public SimpleConsumable () {}

	/**
	 * @param meshPath the path to this item's in-world mesh
	 * @param infoStats an array of defined info stats
	 * @param infoValues the associated values of those info stats
	 * @param statStats an array of stats to apply a change to
	 * @param statValues the associated values to change the consumer's stats by
	 */
	public SimpleConsumable (String meshPath, Stat[] infoStats, String[] infoValues, Stat[] statStats, float[] statValues) {
		this.info = new ItemInfo(infoStats, infoValues);
		this.stats = new StatModification(statStats, statValues);
		this.meshPath = meshPath;
	}

	@Override
	public void rightClick() {
		StatChange effect = new StatChange (getItemStats());
		((Actor)getInventory().getOwner()).applyEffect(effect);
		world.makeLocalEffect(effect);

		getInventory().removeItem(this);
		if(world.getWorldType() != WorldType.CLIENT) world.removeEntity(this);
	}
	
	@Override
	public void getPreloadAssets(Set<String> assets) {
		if(meshPath != null)
			assets.add(meshPath);
	}

	@Override
	protected void makeMesh(AssetManager assetManager) {
		if (this.meshPath != null) {
			geometry = assetManager.loadModel(this.meshPath);
			geometry.setLocalScale(5f);
		} else {
			geometry = new Geometry ("simple consumable", new Box(4, 4, 4));
	        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
	        geometry.setMaterial(mat);
		}
	}

	@Override
	protected String getImage() {
		return "none yet";
	}

	@Override
	protected StatModification makeItemStats() {
		return stats;
	}

	@Override
	protected ItemInfo makeItemInfo() {
		return info;
	}
	
	@Override
	public void read(JmeImporter arg0) throws IOException {
		super.read(arg0);
		InputCapsule c = arg0.getCapsule(this);
		
		meshPath = c.readString("meshPath", null);
		info = (ItemInfo)c.readSavable("itemInfo", null);
		stats = (StatModification)c.readSavable("itemStats", null);
	}
	
	@Override
	public void write(JmeExporter arg0) throws IOException {
		super.write(arg0);
		OutputCapsule c = arg0.getCapsule(this);
		
		c.write(meshPath, "meshPath", null);
		c.write(info, "itemInfo", null);
		c.write(stats, "itemStats", null);
	}
}
