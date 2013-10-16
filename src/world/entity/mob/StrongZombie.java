package world.entity.mob;

import java.util.Set;

import world.ActorStats;
import world.World;
import world.entity.item.Stat;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * A strong, high-health, but slow moving Zombie.
 *
 * @author Tony 300242775
 */
public class StrongZombie extends AbstractZombie {
	@Override
	public void getPreloadAssets(Set<String> assets) {
		assets.add("Models/zombie/zombie.mesh.xml");
	}

	@Override
	protected void makeMesh(AssetManager assetManager) {
		Spatial meshNode = assetManager.loadModel("Models/zombie/zombie.mesh.xml");

		meshNode.setLocalScale(2f);
		meshNode.setLocalTranslation(0, -4.0f, 0);

		geometry = new Node();
		((Node) geometry).attachChild(meshNode);

		initAnim(meshNode, "walk", "attack", "arm_idle");
	}

	@Override
	protected ActorStats makeStats(World world) {
		Stat keys[] = new Stat[] {Stat.HEALTH, Stat.MAXHEALTH, Stat.SPEED, Stat.DAMAGE};
		float values[] = new float[] {14, 14, 0.1f, 10};
		return new ActorStats(keys, values);
	}

	@Override
	public String getImage(){
		return "TestMob.png";
	}
}
