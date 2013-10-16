package world.effects;

import java.io.IOException;
import java.util.Map.Entry;

import world.Actor;
import world.ActorStats;
import world.Entity;
import world.StatModification;
import world.World;
import world.entity.item.Stat;
import world.entity.mob.Mob;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * DayNightStatChange globally changes the stats of all Mobs in the
 * game world. It is used, as the name suggests, to change the stats
 * of Mobs at different times in the day. 
 * 
 * @author Tony
 */
public class DayNightStatChange extends Effect {
	private StatModification multipliers;
	
	public DayNightStatChange() {}

	public DayNightStatChange(StatModification multipliers) {
		this.multipliers = multipliers;
	}
	
	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule c = im.getCapsule(this);
		multipliers = (StatModification)c.readSavable("multipliers", null);
	}
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule c = ex.getCapsule(this);
		c.write(multipliers, "multipliers", null);
	}
	
	@Override
	public void linkToWorld(World world) {
		super.linkToWorld(world);
	}

	@Override
	public void apply(Actor target) {
		ActorStats stats = target.getStats();
		for (Entry<Stat, Float> entry : multipliers) {
			stats.multStat(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void start() {}

	@Override
	public void update(float tpf) {
		Node mobs = world.getMobNode();

		for (Spatial spatial : mobs.getChildren()) {
			Entity e = spatial.getUserData("entity");
			if (e == null) return;

			Mob mob = (Mob) e;
			mob.applyEffect(this);
		}
	}
	
	@Override
	public void onDestroy() {
	}
}
