package world.entity.item.equippable;

import java.util.List;
import java.util.Set;

import world.Actor;
import world.PhysicsUtilities;
import world.StatModification;
import world.PhysicsUtilities.EntityHitResult;
import world.Player;
import world.WorldType;
import world.effects.SimpleDamage;
import world.entity.item.ItemInfo;
import world.entity.item.ItemType;
import world.entity.item.Stat;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * SnipperRiffle is a gun that penetrates multiple enemies with high damage, but has a
 * slow fire speed.
 *
 * @author Tony
 */
public class SniperRifle extends AbstractWeapon {
	private static final int PENETRATION = 3;
	private static final float RANGE = 1000f;

	@Override
	protected String getSoundPath() {
		return "Sounds/weapons/snipper.wav";
	}

	@Override
	public boolean attack(Player attacking) {
		if (firing) return false;
		firing = true;

		List<EntityHitResult> results = PhysicsUtilities.raycastHitResult(attacking.getLocation(), attacking.getDirection(), world.getNode());

		if(world.getWorldType() != WorldType.SERVER) sound.playInstance();

		EntityHitResult e;
		for (int i=0,j=0; i < PENETRATION && j < results.size();) {
			e = results.get(i);
			//if (e instanceof Player) continue;
			if(e.entity == attacking) { j++; continue;}
			if (!(e.entity instanceof Actor) || e.distance > RANGE) break;

			world.makeEffect(new SimpleDamage((Actor)(e.entity), attacking.getStats().getStat(Stat.DAMAGE)));
			i++;
			attacking.incrementHits();
		}

		return true;
	}

	@Override
	protected String getImage() {
		return null;
	}

	@Override
	public void getPreloadAssets(Set<String> assets) {
	}

	@Override
	protected void makeMesh(AssetManager assetManager) {
		geometry = new Geometry ("snipper", new Box(4f,4f,4f));
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Diffuse", new ColorRGBA(1,0,1,1));
        geometry.setMaterial(mat);
	}

	@Override
	public float getFireSpeed() {
		return 1f;
	}

	@Override
	public float getRecoil() {
		return 0.08f;
	}

	@Override
	protected StatModification makeItemStats() {
		Stat keys[] = new Stat[] {Stat.DAMAGE};
		float values[] = new float[] {10};
		return new StatModification(keys, values);
	}

	@Override
	protected ItemInfo makeItemInfo() {
		Stat keys[] = new Stat[] {Stat.NAME};
		String values[] = new String[] {"Sniper Rifle"};
		return new ItemInfo(keys, values);
	}

	@Override
	public ItemType getType() {
		return ItemType.SNIPER;
	}
}
