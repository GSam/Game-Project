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
import world.entity.item.Stat;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * Shotgun is a weapon with a wide range of fire and a slow fire speed.
 * @author Tony 300242775
 */
public class Shotgun extends AbstractWeapon {
	private static final float ANGLE_DIFF = FastMath.PI / 8;
	private static final int NUM_PROJECTILES = 5;
	private static final float RANGE = 100f;

	@Override
	protected String getSoundPath() {
		return "Sounds/weapons/shotgun.wav";
	}

	@Override
	public boolean attack(Player attacking) {
		if (firing) return false;
		firing = true;

		Vector3f direction = null;
		if (attacking.isFirstPerson()) {
			direction = attacking.getFirstPersonCam().getCamera().getDirection();
		} else {
			if(attacking.getCurrentTarget()!=null)
				direction = attacking.getCurrentTarget().getLocation().subtract(attacking.getLocation());
			else
				direction = attacking.getDirection();
		}
		Vector3f location = attacking.getLocation();

		Vector3f axis = Vector3f.UNIT_Y.add(0, direction.getY(), 0);
		Quaternion quat;
		Vector3f ray;

		for (int i=-(NUM_PROJECTILES / 2); i <= NUM_PROJECTILES / 2; i++) {
			quat = new Quaternion().fromAngleAxis(ANGLE_DIFF * i, axis);
			ray = quat.mult(direction);

			List<EntityHitResult> results = PhysicsUtilities.raycastHitResult(location, ray, world.getNode());

			EntityHitResult e;
			if (results.size() > 0) {
				e = results.get(0);

				if(e.entity == attacking && results.size() > 1) {
					e = results.get(1);
					if (e.entity == attacking) continue;
				}

				if(world.getWorldType() != WorldType.SERVER) sound.playInstance();

				if (!(e.entity instanceof Actor) || e.distance > RANGE) continue;
				world.makeEffect(new SimpleDamage((Actor)(e.entity), attacking.getStats().getStat(Stat.DAMAGE)));
				attacking.incrementHits();
			}
		}

		return true;
	}

	@Override
	public float getFireSpeed() {
		return 0.8f;
	}

	@Override
	public float getRecoil() {
		return 0.01f;
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
		geometry = new Geometry ("simple gun", new Box(4f,4f,4f));
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Diffuse", new ColorRGBA(1,0,1,1));
        geometry.setMaterial(mat);
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
		String values[] = new String[] {"Shotgun"};
		return new ItemInfo(keys, values);
	}
}