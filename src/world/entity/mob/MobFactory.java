package world.entity.mob;

import world.ai.StandardZombie;

/**
 * TorchFactory is the entry point for creating torches. It defines the different types of
 * torch in the game and provides access to them through an enum and a static
 * method. 
 * 
 * MobFactory creates instances of Mobs according to internal probabilities, to give
 * the game a balanced feel with different types of mobs.
 * 
 * @author Tony 300242775
 */
public class MobFactory {
	/**
	 * @return an instance of Mob
	 */
	public Mob getMobInstance() {
		//Mob mob = new SmallGreenCube (physicsSpace, assetManager, toAttach);
		//mob.setAI(aiFactory.getAIInstance());
		int choice = (int)(Math.random() * 10);
		Mob mob = null;
		if (choice <= 5)
			mob = new SlowZombie ();
		else if (choice <= 8)
			mob = new FastZombie ();
		else if (choice == 9)
			mob = new StrongZombie ();
		
		mob.setAI(new StandardZombie());
		return mob;
	}
}