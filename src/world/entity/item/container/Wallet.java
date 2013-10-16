package world.entity.item.container;

import java.util.Set;

import world.entity.item.ItemInfo;
import world.entity.item.Stat;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * Wallet is an example implementation of an AbstractContainerItem.
 * 
 * @author Tony 300242775
 */
public class Wallet extends AbstractContainerItem{

	@Override
	public void getPreloadAssets(Set<String> assets) {
	}

	@Override
	protected void makeMesh(AssetManager assetManager) {
		geometry = new Geometry ("Wallet", new Box(4f,4f,4f));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(1,0,0,1));
        geometry.setMaterial(mat);
	}

	@Override
	protected String getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ItemInfo makeItemInfo() {
		Stat keys[] = new Stat[] {Stat.NAME};
		String values[] = new String[] {"Wallet"};
		return new ItemInfo(keys, values);
	}

	@Override
	public boolean canAccess() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCanAccess(boolean access) {
		// TODO Auto-generated method stub
		
	}

}
