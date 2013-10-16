package world.entity.item.torch;

import java.util.Set;

import world.entity.item.Stat;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * SimpleTorch is an implementation of AbstractTorch which provides a configurable torch that
 * constantly emits light and a slow effect when active.
 * 
 * @author Tony 300242775
 */
public class SimpleTorch extends AbstractTorch {
	
	/**
	 * @param col the colour of this torch
	 * @param radius the radius of the light of this torch
	 * @param power the strength of the slow effect of this torch
	 * @param range the range of the slow effect of this torch
	 * @param infoStats an array of information Stats to describe this torch
	 * @param infoValues the associated information with infoStats
	 * @param meshPath a path to the mesh for this torch to use, or null
	 */
	public SimpleTorch(ColorRGBA col, float radius, float power, float range, Stat[] infoKeys, String[] infoValues, String meshPath) {
		super(col, radius, power, range, infoKeys, infoValues, meshPath);
	}

	public SimpleTorch(){
		super(ColorRGBA.White, 0, 1f, 0, new Stat[]{}, new String[]{}, null);
	}
	
	@Override
	public void getPreloadAssets(Set<String> assets) {
		if(meshPath != null)
			assets.add(meshPath);
	}

	@Override
	protected void makeMesh(AssetManager assetManager) {
		if (this.meshPath != null) {
			geometry = assetManager.loadModel(this.meshPath);
			geometry.setLocalScale(5f);
		} else {
			geometry = new Geometry ("simple torch", new Box(4, 6, 4));
	        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
	        //mat.setColor("Color", new ColorRGBA(0,1,0,1));
	        geometry.setMaterial(mat);
		}
	}

	@Override
	protected String getImage() {
		return null;
	}
}
