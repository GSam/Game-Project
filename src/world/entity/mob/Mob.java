package world.entity.mob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import world.Actor;
import world.ActorStats;
import world.World;
import world.WorldType;
import world.ai.AI;
import world.entity.item.Stat;

import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * Mob is an abstract class representing a computer-controlled npc in the game world.
 * The Mob class is purely representational, the behaviour of a specific instance of
 * Mob is dependant on the associated AI object. The interaction between a Mob and an
 * AI is a Strategy pattern.
 *
 * @author Tony 300242775
 */
public abstract class Mob extends Actor {
	private AI ai;
	private Actor target;
	private String name;

	/**
	 * All the names a Mob can possibly have
	 */
	protected static ArrayList<String> mobNames = new ArrayList<String> ();
	static {
		try {
			Scanner sc = new Scanner(new File(Mob.class.getResource("mobnames.txt").getPath()));
			while (sc.hasNext()) {
				mobNames.add(sc.nextLine().trim());
			}
			sc.close();
		} catch (FileNotFoundException e) {
			mobNames.add("Garming");
		}
	}

	public Mob () {
		name = mobNames.get((int)(Math.random() * mobNames.size()));
	}

	// AI AND BEHAVIOUR

	private float mobSpeed = 0;

	public float getMobSpeed() {
		return mobSpeed;
	}

	@Override
	public void update(float tpf) {


		//if (state

		mobSpeed = getStats().getStat(Stat.SPEED);
		getStats().resetStat(Stat.SPEED);

		if (world.getWorldType() != WorldType.CLIENT) {
			if (ai == null) throw new IllegalStateException ("AI being called before being initialised.");

			ai.update(this, tpf);
		}

		getStats().resetStat(Stat.DAMAGE);


	}

	/**
	 * Set the AI of this mob to the provided AI.
	 * @param ai the AI to control this mob
	 */
	public void setAI (AI ai) {
		this.ai = ai;
		if(ai != null)
			ai.linkToWorld(world);
	}

	/**
	 * @return the actor this mob is currently targetting
	 */
	public Actor getTarget(){
		return target;
	}

	/**
	 * Set the actor this mob is currently targetting.
	 * @param player the actor to target
	 */
	public void setTarget(Actor player){
		this.target = player;
	}

	@Override
	protected void onStateChange() {}

	// DISPLAY

	public abstract String getImage();

	public String getName() {
		return name;
	}

	// GEOMETRY

	@Override
	protected abstract void makeMesh(AssetManager assetManager);

	// WORLD LINK

	@Override
	protected Node getNodeToAttach() {
		return world.getMobNode();
	}

	@Override
	public void linkToWorld(World world, Vector3f location, int id) {
		super.linkToWorld(world, location, id);
		if(ai != null)
			ai.linkToWorld(world);
	}

	// SAVE LOAD
	@Override
	protected ActorStats makeStats(World world) {
		Stat keys[] = new Stat[] {Stat.HEALTH, Stat.MAXHEALTH, Stat.SPEED, Stat.DAMAGE};
		float values[] = new float[] {100,100, 1.0f,100};
		return new ActorStats(keys, values);
	}

	@Override
	public void read(JmeImporter arg0) throws IOException {
		super.read(arg0);
		InputCapsule ic = arg0.getCapsule(this);
		ai = (AI)ic.readSavable("ai", null);
		name = ic.readString("name", null);
	}

	@Override
	public void write(JmeExporter arg0) throws IOException {
		super.write(arg0);
		OutputCapsule oc = arg0.getCapsule(this);
		oc.write(ai, "ai", null);
		oc.write(name, "name", null);
	}

	@Override
	public boolean isUpdatable() {
		return true;
	}
}
