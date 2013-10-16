package world.effects;

import java.io.IOException;

import world.Actor;
import world.Entity;
import world.Player;
import world.entity.item.Stat;

import com.jme3.bounding.BoundingVolume;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;

/**
 * SlowInRadius slows all the Mobs in a radius around a point. This is
 * used, for example, to make torches a key part of gameplay.
 * 
 * @author Tony 300242775
 */
public class SlowInRadius extends Effect {
	private BoundingVolume sphere;
	private float slowAmount;
	private float radius;
	private Vector3f centre;
	
	public SlowInRadius() {}

	/**
	 * @param centre the centre of the area to slow mobs in
	 * @param radius the radius around the centre to slow mobs in
	 * @param slowAmount the amount to multiply the speed of mobs in the area by
	 */
	public SlowInRadius(Vector3f centre, float radius, float slowAmount) {
		this.slowAmount = slowAmount;
		this.radius = radius;
		this.centre = centre;
	}
	
	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule c = im.getCapsule(this);
		slowAmount = c.readFloat("slow", 0);
		radius = c.readFloat("radius", 0);
		centre = (Vector3f)c.readSavable("centre", null);
	}
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule c = ex.getCapsule(this);
		c.write(slowAmount, "slow", 0);
		c.write(radius, "radius", 0);
		c.write(centre, "centre", null);
	}

	@Override
	public void apply(Actor actor) {
		actor.getStats().multStat(Stat.SPEED, slowAmount);
	}

	@Override
	public void start() {
	}

	/**
	 * Moves the centre of the SlowInRadius effect to the passed Vector3f.
	 * 
	 * @param centre
	 *            the position to move this Effect to
	 */
	public void setLocation(Vector3f centre) {
		sphere.setCenter(centre);
	}

	@Override
	public void update(float tpf) {
		float radius_sq = radius*radius;
		for(Entity e : world.getEntities()) {
			if(!(e instanceof Actor)) continue;
			if(e instanceof Player) continue;
			if(e.getLocation().distanceSquared(centre) <= radius_sq)
				((Actor)e).applyEffect(this);
		}
	}
	
	@Override
	public void onDestroy() {
	}
}
