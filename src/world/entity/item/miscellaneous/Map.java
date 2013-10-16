package world.entity.item.miscellaneous;

import java.util.Set;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import world.StatModification;
import world.entity.item.Item;
import world.entity.item.ItemInfo;
import world.entity.item.ItemType;
import world.entity.item.Stat;

/**
 * Basic map item.
 * 
 * @author Garming Sam 300198721
 * 
 */
public class Map extends Item {

	@Override
	public void getPreloadAssets(Set<String> assets) {
	}

	@Override
	protected void makeMesh(AssetManager assetManager) {
		geometry = new Geometry ("useless blue token", new Box(4f,4f,4f));
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Diffuse", new ColorRGBA(0,0,1,1));
        geometry.setMaterial(mat);
	}

	@Override
	protected String getImage() {
		return "Interface/map.png";
	}

	@Override
	protected StatModification makeItemStats() {
		Stat keys[] = new Stat[] {Stat.ARMOUR,Stat.DAMAGE};
		float values[] = new float[] {5, 25};
		return new StatModification(keys, values);
	}

	@Override
	protected ItemInfo makeItemInfo() {
		Stat keys[] = new Stat[] {Stat.NAME};
		String values[] = new String[] {"Map"};
		return new ItemInfo(keys, values);
	}

	@Override
	public ItemType getType() {
		return ItemType.MAP;
	}
}
