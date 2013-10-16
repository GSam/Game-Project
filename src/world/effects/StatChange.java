package world.effects;

import java.io.IOException;
import java.util.Map.Entry;

import world.Actor;
import world.ActorStats;
import world.StatModification;
import world.World;
import world.entity.item.Stat;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;

/**
 * StatChange is a more general case of SimpleDamage, that adds to an
 * Actor's ActorStats object according to an a arbitrary StatModification
 * object.
 * 
 * @author Tony 300242775
 */
public class StatChange extends Effect {
	private Actor target;
	private StatModification effect;
	private int targetID = -1;
	
	public StatChange () {}
	
	/**
	 * @param effect the StatModification to apply
	 */
	public StatChange(StatModification effect) {
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
		targetID = c.readInt("target", -1);
		effect = (StatModification)c.readSavable("effect", null);
	}
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule c = ex.getCapsule(this);
		c.write(target == null ? -1 : target.getEntityID(), "target", -1);
		c.write(effect, "effect", null);
	}

	@Override
	public void apply(Actor actor) {
		this.target = actor;
	}

	@Override
	public void start() {
		ActorStats stats = target.getStats();
		for (Entry<Stat, Float> entry : effect) {
			stats.modStat(entry.getKey(), entry.getValue());
			
			if (stats.getStat(Stat.HEALTH) > stats.getStat(Stat.MAXHEALTH)) {
				stats.setStat(Stat.HEALTH, stats.getStat(Stat.MAXHEALTH));
			}
			
			if (stats.getStat(Stat.ENERGY) > stats.getStat(Stat.MAXENERGY)) {
				stats.setStat(Stat.ENERGY, stats.getStat(Stat.MAXENERGY));
			}
		}	
		destroy ();
	}
}
