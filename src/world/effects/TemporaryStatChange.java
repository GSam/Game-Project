package world.effects;

import java.io.IOException;
import java.util.Map.Entry;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;

import world.Actor;
import world.ActorStats;
import world.StatModification;
import world.World;
import world.entity.item.Stat;

/**
 * TemporaryStatChange is identical to the StatChange effect except that the
 * StatModification is applied for a duration, and then removed from the Actor.
 * 
 * @author Tony 300242775
 */
public class TemporaryStatChange extends Effect {
	private float duration;
	private float current;
	private Actor target;
	private StatModification effect;
	private int targetID = -1;
	
	public TemporaryStatChange() {}
	
	/**
	 * @param effect the StatModification to apply to an Actor
	 * @param duration the duration to apply this Effect for, in seconds
	 */
	public TemporaryStatChange(StatModification effect, float duration) {
		this.duration = duration;
		this.effect = effect;
	}
	
	@Override
	public void linkToWorld(World world) {
		super.linkToWorld(world);
		
		if(targetID != -1)
			target = (Actor)world.getEntityByID(targetID);
	}
	
	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule c = im.getCapsule(this);
		duration = c.readFloat("dur", 0);
		current = c.readFloat("cur", 0);
		targetID = c.readInt("target", -1);
		effect = (StatModification)c.readSavable("effect", null);
	}
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule c = ex.getCapsule(this);
		c.write(duration, "dur", 0);
		c.write(current, "cur", 0);
		c.write(target == null ? -1 : target.getEntityID(), "target", -1);
		c.write(effect, "effect", null);
	}

	@Override
	public void apply(Actor actor) {
		this.target = actor;
	}

	@Override
	public void start() {
		if(target == null) return;
		ActorStats stats = target.getStats();
		for (Entry<Stat, Float> entry : effect) {
			stats.modStat(entry.getKey(), entry.getValue());
		}		
	}

	@Override
	public void update(float tpf) {
		current += tpf;
		if (current >= duration)
			destroy();
	}

	@Override
	public void onDestroy() {
		if(target == null) return;
		ActorStats stats = target.getStats();
		for (Entry<Stat, Float> entry : effect) {
			stats.modStat(entry.getKey(), -1 * entry.getValue());
		}
	}
}
