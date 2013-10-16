package world.effects;

import java.io.IOException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;

import world.Actor;
import world.ActorStats;
import world.World;
import world.entity.item.Stat;

/**
 * SimpleDamage deals damage to an Actor by modifying the HEALTH Stat in the
 * Actor's ActorStats object.
 * 
 * @author Tony 300242775
 */
public class SimpleDamage extends Effect {
	private float damage;
	private Actor actor;
	private int savedActorID = -1;
	
	public SimpleDamage() {}
	
	/**
	 * @param actor the Actor to damage
	 * @param damage the amount of damage to do
	 */
	public SimpleDamage(Actor actor, float damage) {
		this.damage = damage;
		this.actor = actor;
	}
	
	@Override
	public void linkToWorld(World world) {
		super.linkToWorld(world);
		
		if(savedActorID != -1)
			actor = (Actor)world.getEntityByID(savedActorID);
	}
	
	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule c = im.getCapsule(this);
		savedActorID = c.readInt("actor", -1);
		damage = c.readFloat("damage", 0);
	}
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule c = ex.getCapsule(this);
		c.write(actor == null ? -1 : actor.getEntityID(), "actor", -1);
		c.write(damage, "damage", 0);
	}

	@Override
	public void apply(Actor actor) {
		ActorStats stats = actor.getStats();
		
		float armor = stats.getStat(Stat.ARMOUR);
		float modDamage = damage - armor >= 1 ? damage - armor : 1;
		
		stats.modStat(Stat.HEALTH, -(1 * modDamage));
		destroy ();
	}

	@Override
	public void start() {
		if(actor != null) actor.applyEffect(this);
	}

	@Override
	public void update(float tpf) {}
}
