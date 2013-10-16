package world.entity.staticentity;

import java.util.Set;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import world.RigidEntity;

/**
 * @author Tony
 */
public class RedDot extends RigidEntity {

	@Override
	protected float getWeight() {
		return 0;
	}

	@Override
	public void getPreloadAssets(Set<String> assets) {
	}

	@Override
	protected void makeMesh(AssetManager assetManager) {
		geometry = new Geometry ("red dot", new Box(1, 1, 1));
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        geometry.setMaterial(mat);
	}

}
