package world.entity.mob;

import java.util.Set;

import world.ActorStats;
import world.World;
import world.entity.item.Stat;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * A slow and weak zombie.
 * @author Tony 300242775
 */
public class SlowZombie extends AbstractZombie {
	@Override
	public void getPreloadAssets(Set<String> assets) {
		assets.add("Models/zombie/zombie.mesh.xml");
	}

	@Override
	protected void makeMesh(AssetManager assetManager) {

		Spatial meshNode = assetManager.loadModel("Models/zombie/zombie.mesh.xml");

		meshNode.setLocalScale(1.7f);
		meshNode.setLocalTranslation(0, -3.4f, 0);

		geometry = new Node();
		((Node) geometry).attachChild(meshNode);

		initAnim(meshNode, "walk", "attack", "arm_idle");
	}

	@Override
	protected ActorStats makeStats(World world) {
		Stat keys[] = new Stat[] {Stat.HEALTH, Stat.MAXHEALTH, Stat.SPEED, Stat.DAMAGE};
		float values[] = new float[] {8, 8, 0.15f, 5};
		return new ActorStats(keys, values);
	}

	@Override
	public String getImage(){
		return "TestMob.png";
	}

	/*@Override
	public String getName(){
		return "Slow Zombie";
	}*/
}
