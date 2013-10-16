package world.entity.item.equippable;

import java.io.IOException;
import java.util.Set;

import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import world.StatModification;
import world.entity.item.ItemInfo;
import world.entity.item.Stat;

/**
 * SimpleEquippable is a general class for Equippables with no function other than to
 * provide a direct, permanent-until-unequipped stat change to the Player on equip.
 * 
 * This class should not be instantiated directly, instead instances of it should be
 * supplied by EquippableFactory.
 * 
 * @author Tony 300242775
 */
public class SimpleEquippable extends AbstractEquippable {
	private String meshPath;
	private EquipType slot;
	
	public SimpleEquippable () {}
	
	/**
	 * @param meshPath the path to this item's in-world mesh
	 * @param slot the EquipType of this SimpleEquippable
	 * @param infoStats an array of defined info stats
	 * @param infoValues the associated values of those info stats
	 * @param statStats an array of stats to apply a change to
	 * @param statValues the associated values to change the equippers stats by
	 */
	public SimpleEquippable (String meshPath, EquipType slot, Stat[] infoStats, String[] infoValues, Stat[] statStats, float[] statValues) {
		this.info = new ItemInfo(infoStats, infoValues);
		this.stats = new StatModification(statStats, statValues);
		this.meshPath = meshPath;
		this.slot = slot;
	}
	
	@Override
	public EquipType getEquipType() {
		return slot;
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
			geometry = new Geometry ("simple equippable", new Box(4, 4, 4));
	        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
	        geometry.setMaterial(mat);
		}
	}

	@Override
	protected String getImage() {
		return null;
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
		slot = EquipType.values()[c.readInt("slot", 1123)];
	}
	
	@Override
	public void write(JmeExporter arg0) throws IOException {
		super.write(arg0);
		OutputCapsule c = arg0.getCapsule(this);
		
		c.write(meshPath, "meshPath", null);
		c.write(info, "itemInfo", null);
		c.write(stats, "itemStats", null);
		c.write(slot.ordinal(), "slot", 1123);
	}
}
