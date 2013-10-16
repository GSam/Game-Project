package world.entity.trigger;

import java.io.IOException;

import world.Player;
import world.ai.StandardZombie;
import world.entity.mob.FastZombie;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Vector3f;

/**
 * @author Alex Campbell 300252131
 */
public class SpawnZombieAction implements TriggerAction {
	public SpawnZombieAction() {}
	
	private Vector3f loc;
	
	public SpawnZombieAction(Vector3f where) {
		this.loc = where;
	}
	
	@Override
	public void trigger(Player triggerer) {
		for(int k = 0; k < 5; k++) {
			FastZombie z = new FastZombie();
			z.setAI(new StandardZombie());
			triggerer.getWorld().addEntity(z, loc);
		}
	}
	
	@Override
	public void read(JmeImporter im) throws IOException {
		loc = (Vector3f)im.getCapsule(this).readSavable("loc", null);
	}
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		ex.getCapsule(this).write(loc, "loc", null);
	}
}
