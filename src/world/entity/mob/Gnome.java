package world.entity.mob;

import java.util.Set;

import world.ActorStats;
import world.World;
import world.entity.item.Stat;

import com.jme3.asset.AssetManager;

/**
 * @author Tony
 */
public class Gnome extends Mob {
	@Override
	public void getPreloadAssets(Set<String> assets) {
		assets.add("Models/zombie/zombie.mesh.xml");
	}

	@Override
	protected void makeMesh(AssetManager assetManager) {
		geometry = assetManager.loadModel("zombie/zombie.mesh.xml");
		geometry.setLocalScale(5f);
	}

	@Override
	protected ActorStats makeStats(World world) {
		Stat keys[] = new Stat[] {Stat.HEALTH, Stat.MAXHEALTH, Stat.SPEED, Stat.DAMAGE};
		float values[] = new float[] {100,100, 0.2f,100};
		return new ActorStats(keys, values);
	}

	@Override
	protected void onStateChange() {

	}
	
	@Override
	public String getImage(){
		return "TestMob.png";
	}
	
//	public String getName(){
//		return "Misunderstood";
//	}
}
