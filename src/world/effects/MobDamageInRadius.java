package world.effects;

import java.io.IOException;
import java.util.HashSet;

import world.Actor;
import world.Entity;
import world.World;
import world.entity.item.Stat;
import world.entity.mob.Mob;

import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * MobDamangeInRadius does damage (in the same manner as SimpleDamage) to all
 * mobs in a radius around a point at the time start() is called. This Effect
 * is not continuous, like SimpleDamange and unlike SlowInRadius, and so is destroyed
 * at the end of the call to start()	
 * 
 * @author Tony 300242775
 */
public class MobDamageInRadius extends Effect {
	private BoundingVolume sphere;
	private Node sphereNode;
	private float radius;
	private Vector3f centre;
	private float damage;
	
	public MobDamageInRadius() {}

	/**
	 * @param centre the centre of the area to deal damage in
	 * @param radius the radius around the centre to deal damage in
	 * @param damage the amount of damage to deal to each Mob in the area
	 */
	public MobDamageInRadius(Vector3f centre, float radius, float damage) {
		this.damage = damage;
		this.radius = radius;
		this.centre = centre;
	}
	
	@Override
	public void linkToWorld(World world) {
		super.linkToWorld(world);
		
		this.sphere = new BoundingSphere(radius, centre);
		sphereNode = new Node();
		sphereNode.setModelBound(sphere);
		world.getNode().attachChild(sphereNode);
	}

	@Override
	public void apply(Actor actor) {
		actor.getStats().modStat(Stat.HEALTH, -damage);
	}

	@Override
	public void start() {
		Node mobs = world.getMobNode();
		if (mobs.getChildren().size() == 0) return;

		HashSet<Mob> toApply = new HashSet<Mob>();

		CollisionResults results = new CollisionResults();
		mobs.collideWith(sphere, results);

		for (CollisionResult result : results) {
			Entity e = (Entity) result.getGeometry().getUserData("entity");
			if (!(e instanceof Mob)) continue; // includes null case
			toApply.add((Mob)e);
			
		}
		
		for (Mob mob : toApply) {
			mob.applyEffect(this);
		}
		
		destroy ();
	}

	@Override
	public void update(float tpf) {}

	@Override
	public void onDestroy() {
		world.getNode().detachChild(sphereNode);
	}
	
	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule c = im.getCapsule(this);
		damage = c.readFloat("damage", 0);
		radius = c.readFloat("radius", 0);
		centre = (Vector3f)c.readSavable("centre", null);
	}
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule c = ex.getCapsule(this);
		c.write(damage, "damage", 0);
		c.write(radius, "radius", 0);
		c.write(centre, "centre", null);
	}
}
