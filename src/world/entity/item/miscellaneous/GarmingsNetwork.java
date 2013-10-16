package world.entity.item.miscellaneous;

import java.util.Set;

import world.StatModification;
import world.entity.item.Item;
import world.entity.item.ItemInfo;
import world.entity.item.ItemType;
import world.entity.item.Stat;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * @author Tony
 */
public class GarmingsNetwork extends Item {
	
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
	public int getItemWeight() {
		return 7;
	}

	@Override
	protected String getImage() {
		return "no craig, no image for you";
	}
	
	@Override
	public String getName() {
		return "";
	}
	
	@Override
	public ItemType getType() {
		return ItemType.GARMINGISH;
	}

	@Override
	protected StatModification makeItemStats() {
		Stat keys[] = new Stat[] {Stat.ARMOUR,Stat.DAMAGE};
		float values[] = new float[] {55, 50};
		return new StatModification(keys, values);
	}

	@Override
	protected ItemInfo makeItemInfo() {
		Stat keys[] = new Stat[] { Stat.DESCRIPTION,Stat.NAME,};
		String values[] = new String[] {"You just broke it.","Garming's Network"};
		return new ItemInfo(keys, values);
	}
}