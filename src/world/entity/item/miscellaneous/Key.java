package world.entity.item.miscellaneous;

import java.io.IOException;
import java.util.Set;

import world.StatModification;
import world.entity.item.Item;
import world.entity.item.ItemInfo;
import world.entity.item.ItemType;
import world.entity.item.Stat;

import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * A Key is a standard inventory item that can be used to unlock Lockables.
 * 
 * @author Tony 300242775
 */
public class Key extends Item {
	private String keyName;
	private String meshPath;
	
	public Key() {}

	/**
	 * @param meshPath a valid path to the mesh this Key should use
	 * @param infoStats an array of information Stats to describe this key
	 * @param infoValues the associated information with infoStats
	 */
	public Key(String meshPath, Stat[] infoStats, String[] infoValues) {
		this.keyName = name;
		this.info = new ItemInfo(infoStats, infoValues);
		this.meshPath = meshPath;
	}

	/**
	 * @param mesh a valid path to the mesh this Key should use
	 * @param name the name of this key
	 */
	public Key(String mesh, String name) {
		this.keyName = name;
		this.meshPath = mesh;
	}

	@Override
	public void getPreloadAssets(Set<String> assets) {
		if(meshPath != null)
			assets.add(meshPath);
	}

	@Override
	protected void makeMesh(AssetManager assetManager) {
		if (meshPath == null) {
			geometry = new Geometry("a key", new Box(4f, 4f, 4f));
			Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			mat.setColor("Color", new ColorRGBA(1, 0, 0, 1));
			geometry.setMaterial(mat);
			
		} else {
			geometry = assetManager.loadModel(meshPath);
			geometry.setLocalScale(5);
		}
	}

	@Override
	protected String getImage() {
		return null;
	}

	@Override
	protected StatModification makeItemStats() {
		Stat keys[] = new Stat[] {};
		float values[] = new float[] {};
		return new StatModification(keys, values);
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
		keyName = c.readString("keyName", null);
		info = (ItemInfo)c.readSavable("info", null);
	}
	
	@Override
	public void write(JmeExporter arg0) throws IOException {
		super.write(arg0);
		OutputCapsule c = arg0.getCapsule(this);
		c.write(meshPath, "meshPath", null);
		c.write(keyName, "keyName", null);
		c.write(info, "info", null);
	}
	
	@Override
	public ItemType getType() {
		return ItemType.KEY;
	}
}
