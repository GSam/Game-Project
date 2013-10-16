package world.entity.item.miscellaneous;

import java.util.Set;

import world.StatModification;
import world.entity.item.Item;
import world.entity.item.ItemInfo;
import world.entity.item.Stat;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * Treasure is an otherwise standard inventory Item that needs to be collected in order
 * to win the game.
 *  
 * @author Tony 300242775
 */
public class Treasure extends Item {
	private String meshPath;
	private String imagePath;
	
	public Treasure () {}
	
	/**
	 * @param meshPath the path to this treasures's in-world mesh
	 * @param infoPath the path to this treasure's inventory image
	 * @param infoStats an array of defined info stats
	 * @param infoValues the associated values of those info stats
	 */
	public Treasure (String meshPath, String imagePath, Stat[] infoStats, String[] infoVals) {
		this.info = new ItemInfo (infoStats, infoVals);
		this.meshPath = meshPath;
		this.imagePath = imagePath;
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
		return imagePath;
	}

	@Override
	protected StatModification makeItemStats() {
		return new StatModification (new Stat[]{}, new float[]{});
	}

	@Override
	protected ItemInfo makeItemInfo() {
		return this.info;
	}

}
