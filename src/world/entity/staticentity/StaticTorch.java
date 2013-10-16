package world.entity.staticentity;

import java.util.Set;

import world.RigidEntity;
import world.World;
import world.effects.SlowInRadius;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * StaticTorch is an immovable version of a standard inventory-based torch,
 * that slows Mobs withini a radius of it.
 * @author Tony 300242775
 */
public class StaticTorch extends RigidEntity {
	private SlowInRadius effect;

	@Override
	public void linkToWorld(World world, Vector3f location, int id) {
		super.linkToWorld(world, location, id);
		effect = new SlowInRadius (location, 100, 0.1f);
		world.makeEffect(effect);
	}

	@Override
	public void getPreloadAssets(Set<String> assets) {
	}

	@Override
	protected void makeMesh(AssetManager assetManager) {
		geometry = new Geometry ("test torch with slow effect", new Box(1,6,1));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(1,1,1,1));
        geometry.setMaterial(mat);
	}

	@Override
	protected float getWeight() {
		return 0;
	}
}
