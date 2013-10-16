package world.entity.item.equippable;

import java.util.List;
import java.util.Set;

import world.Actor;
import world.Inventory;
import world.PhysicsUtilities;
import world.StatModification;
import world.PhysicsUtilities.EntityHitResult;
import world.Player;
import world.World;
import world.WorldType;
import world.effects.SimpleDamage;
import world.entity.item.ItemInfo;
import world.entity.item.Stat;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * SimpleGun is a simple gun that fires a single shot and has an average
 * fire speed.
 *
 * @author Tony 300242775
 */
public class SimpleGun extends AbstractWeapon {
	private static final float RANGE = 500f;
	private ParticleEmitter blood;

	@Override
	public void linkToWorld(World world, Vector3f location, int id) {
		super.linkToWorld(world, location, id);
		initBloodEffect();
	}

	@Override
	public void linkToInventory (World world, Inventory inventory, int id) {
		super.linkToInventory(world, inventory, id);
		initBloodEffect();
	}

	private void initBloodEffect () {
		blood = new ParticleEmitter("blood", Type.Point, 32 * 30);
		blood.setSelectRandomImage(true);
		blood.setStartColor(new ColorRGBA(0.1f, 0.0f, 0.0f, 1f));
		blood.setEndColor(new ColorRGBA(0.1f, 0.0f, 0.0f, 1f));
		blood.setStartSize(1f);
		blood.setEndSize(1f);
		blood.setShape(new EmitterSphereShape(Vector3f.ZERO, 1f));
		blood.setParticlesPerSec(0);
		blood.setGravity(0, 10, 0);
		blood.setLowLife(0.2f);
		blood.setHighLife(0.2f);
		blood.getParticleInfluencer().setInitialVelocity(new Vector3f(0, -9, 0));
		blood.getParticleInfluencer().setVelocityVariation(0f);
		blood.setImagesX(2);
		blood.setImagesY(2);
		Material mat = new Material(world.getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
		mat.setTexture("Texture", world.getAssetManager().loadTexture("Effects/Explosion/flame.png"));
		mat.setBoolean("PointSprite", true);
		blood.setMaterial(mat);
		world.getNode().attachChild(blood);
	}

	@Override
	protected String getSoundPath() {
		return "Sounds/weapons/simple.wav";
	}

	@Override
	public boolean attack(Player attacking) {
		blood.setLocalTranslation(new Vector3f (10,20,10));
		blood.emitAllParticles();

		if (firing) return false;
		firing = true;

		List<EntityHitResult> results = PhysicsUtilities.raycastHitResult(attacking.getLocation(), attacking.getDirection(), world.getNode());
		EntityHitResult e;
		if (results.size() > 0) {
			e = results.get(0);

			if(e.entity == attacking && results.size() > 1) {
				e = results.get(1);
				if (e.entity == attacking) return true;
			}

			if(world.getWorldType() != WorldType.SERVER) sound.playInstance();

			if (!(e.entity instanceof Actor) || e.distance > RANGE) return true;

			//World.sop(attacking.getLocation(), e.hitAt);
			blood.setLocalTranslation(e.hitAt);
			blood.setNumParticles(50);
			blood.emitAllParticles();

			world.makeEffect(new SimpleDamage((Actor)(e.entity), attacking.getStats().getStat(Stat.DAMAGE)));
			attacking.incrementHits();
		}

		return true;
	}

	@Override
	public String getName() {
		return "SIMPLE GUN OUT OF SYNC";
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
	public float getFireSpeed() {
		return 0.4f;
	}

	@Override
	public float getRecoil() {
		return 0.004f;
	}

	@Override
	protected StatModification makeItemStats() {
		Stat keys[] = new Stat[] {Stat.DAMAGE};
		float values[] = new float[] {5};
		return new StatModification(keys, values);
	}

	@Override
	protected ItemInfo makeItemInfo() {
		Stat keys[] = new Stat[] {Stat.NAME};
		String values[] = new String[] {"Old Rifle"};
		return new ItemInfo(keys, values);
	}
}
