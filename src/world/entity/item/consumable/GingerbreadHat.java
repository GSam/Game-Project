package world.entity.item.consumable;

import java.util.Set;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import world.Actor;
import world.StatModification;
import world.WorldType;
import world.effects.StatChange;
import world.entity.item.ItemInfo;
import world.entity.item.RightClickable;
import world.entity.item.Stat;
import world.entity.item.equippable.AbstractEquippable;
import world.entity.item.equippable.EquipType;

public class GingerbreadHat extends AbstractEquippable implements RightClickable {

	@Override
	public void getPreloadAssets(Set<String> assets) {
	}

	@Override
	public EquipType getEquipType() {
		return EquipType.HEAD;
	}

	@Override
	protected void makeMesh(AssetManager assetManager) {
		geometry = new Geometry ("simple consumable", new Box(4, 4, 4));
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        geometry.setMaterial(mat);
	}

	@Override
	protected String getImage() {
		return null;
	}

	@Override
	protected StatModification makeItemStats() {
		Stat[] stats = new Stat[] {Stat.HEALTH};
		float[] values = new float[] {100};
		return new StatModification (stats, values);
	}

	@Override
	protected ItemInfo makeItemInfo() {
		Stat[] stats = new Stat[] {Stat.NAME};
		String[] values = new String[] {"A Gingerbread Hat"};
		return new ItemInfo (stats, values);
	}

	@Override
	public void rightClick() {
		StatChange effect = new StatChange (getItemStats());
		((Actor)getInventory().getOwner()).applyEffect(effect);
		world.makeLocalEffect(effect);

		getInventory().removeItem(this);
		if(world.getWorldType() != WorldType.CLIENT) world.removeEntity(this);
	}
}
