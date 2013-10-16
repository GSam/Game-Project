package world;

import world.TimeManager.Stage;
import world.entity.mob.Mob;
import world.entity.mob.MobFactory;

import com.jme3.math.Vector3f;

/**
 * An MobSpawnController is responsible for controlling the logic of when, and where, computer-
 * controller Mobs should be spawned in the game world.
 *
 * The spawnTick method is its publicly exposed handle.
 *
 * @author Tony 300242775
 */
public class MobSpawnController {
	private static final float TIME_BETWEEN_SPAWNS = 10f;
	
	private static final float SPAWN_VARIATION = 20f;
	private static final float SPAWN_RADIUS = 500f;
	private static final float SPAWN_EDGES = 50f;
	
	private static final float HALF_SPAWN_VARIATION = SPAWN_VARIATION / 2;
	private static final float HALF_SPAWN_RADIUS = SPAWN_RADIUS / 2;
	private static final float EDGE_SPAWN_RADIUS = HALF_SPAWN_RADIUS - SPAWN_EDGES;

	private World world;
	private MobFactory mobFactory;

	private float timeSinceLastSpawn = TIME_BETWEEN_SPAWNS;

	/**
	 * @param world the World to spawn mobs in
	 */
	public MobSpawnController (World world) {
		this.world = world;
		this.mobFactory = new MobFactory();
	}

	/**
	 * Perform one tick of mob spawning logic, which may or many not create new
	 * mobs in the World provided on construction.
	 * 
	 * @param tpf the time per frame
	 */
	public void spawnTick (float tpf) {
		timeSinceLastSpawn += tpf;
		if (timeSinceLastSpawn < TIME_BETWEEN_SPAWNS && tpf >= 0)
			return;

		timeSinceLastSpawn = 0;

		Stage stage = world.getTimeManager().getStage();
		
		if (stage == Stage.DAWN) {
			makeMobs (0, 0);
		} else if (stage == Stage.DAY) {
			if (Math.random() > 0.5) makeMobs (1, 1);
		} else if (stage == Stage.DUSK) {
			makeMobs (2, 2);
		} else if (stage == Stage.NIGHT) {
			makeMobs (4, 3);
		}
	}
	
	private void makeMobs (int numPerGroup, int groupsPerPlayer) {
		for (Player p : world.getPlayers()) { // for each player
			for (int i=0; i < groupsPerPlayer; i++) { // make some groups of mobs
				
				float z = p.getLocation().z + (float) (Math.random() * 500 - 250);
				//float x = p.getLocation().x + (float) (Math.random() * 500 - 250);
				float x = p.getLocation().x + ((z > EDGE_SPAWN_RADIUS || z < -EDGE_SPAWN_RADIUS) ? (float) (Math.random() * SPAWN_RADIUS - HALF_SPAWN_RADIUS) : (float) ((Math.random() > 0.5 ? -HALF_SPAWN_RADIUS : HALF_SPAWN_RADIUS - SPAWN_EDGES) + Math.random()*SPAWN_EDGES));
				Vector3f spawn = new Vector3f(x,p.getLocation().getY(),z);
				
				for (int j=0; j < numPerGroup; j++) { // actually spawn the mobs
					Mob mob = mobFactory.getMobInstance();
					mob.setTarget(p); // set their target as the player
					world.addEntity(mob, spawn.add((float)(Math.random() * SPAWN_VARIATION - HALF_SPAWN_VARIATION),0,(float)(Math.random() * SPAWN_VARIATION - HALF_SPAWN_VARIATION)));
				}
			}
		}
	}
}
