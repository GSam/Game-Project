package world.entity.staticentity;

import java.io.IOException;

import world.Activatable;
import world.Container;
import world.Inventory;
import world.Player;

import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * @author Tony
 */
public class SimpleContainer extends AbstractStaticLockedActivator implements Container, Activatable{
    private boolean isAccessible = true;
    protected Inventory inventory = new Inventory(this, 16);

    public SimpleContainer(){
    }

    public SimpleContainer(String meshPath, Vector3f scale, float angle){
        super(meshPath, scale, angle);
    }

    @Override
    protected void makeMesh(AssetManager assetManager){

        geometry = assetManager.loadModel("Models/chest/chest.scene");

        geometry.setLocalScale(scale);
        geometry.getLocalRotation().addLocal(new Quaternion().fromAngleNormalAxis(angle, Vector3f.UNIT_Y));
    }

    @Override
    public Inventory getContainerInventory(){
        return inventory;
    }

    @Override
    public boolean canAccess(){
        return isAccessible;
    }

    @Override
    public void setCanAccess(boolean access){
        isAccessible = access;
    }

    @Override
	protected void onOpen(Player player) {
		if (!player.isInContainer()) {
			player.displayChest(getContainerInventory());
			player.setInContainer(true, getLocation());
		}

    }

    @Override
    protected void onClose(Player player){
        onOpen(player);
    }

    @Override
    public void read(JmeImporter arg0) throws IOException{
        super.read(arg0);
        InputCapsule c = arg0.getCapsule(this);
        inventory = (Inventory) arg0.getCapsule(this).readSavable("inventory", null);
    }

    @Override
    public void write(JmeExporter arg0) throws IOException{
        super.write(arg0);
        OutputCapsule c = arg0.getCapsule(this);
        arg0.getCapsule(this).write(inventory, "inventory", null);
    }
}
