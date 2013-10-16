package world.entity.staticentity;

import java.io.IOException;

import world.Activatable;
import world.Player;
import world.entity.item.Item;
import world.entity.item.miscellaneous.Treasure;

import com.jme3.asset.AssetManager;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * OutpostGate represents the gate that needs to be activated to win the game, and
 * can be thought of as a 'GameWinController'.
 *
 * It duplicates much functionality from WorldObject for no reason.
 *
 * @author Tony 300242775
 */
public class OutpostGate extends WorldObject implements Activatable{

	private Vector3f scale;
	private float rotation;

	public OutpostGate() {}

	public OutpostGate (Vector3f scale, float rotation) {
	    this.scale = scale;
	    this.rotation = rotation;
	}

	@Override
	public void makeMesh(AssetManager assetManager){
	    geometry = assetManager.loadModel("/Models/Gate/Gate.scene");
        geometry.setLocalScale(scale);
        geometry.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(rotation, Vector3f.UNIT_Y));

	}

	@Override
	public void read(JmeImporter arg0) throws IOException {
		super.read(arg0);
		scale = (Vector3f) arg0.getCapsule(this).readSavable("gatescale", null);
		rotation = arg0.getCapsule(this).readFloat("gaterot", 0);
	}

	@Override
	public void write(JmeExporter arg0) throws IOException {
		super.write(arg0);
		arg0.getCapsule(this).write(scale, "gatescale", null);
		arg0.getCapsule(this).write(rotation, "gaterot", 0);
	}

	@Override
	public void activate(Player player) {
		int count = 0;
		for (Item item : player.getInventory()) {
			if (item instanceof Treasure) count++;
		}

		if (count < 1) return;

		super.world.getScreenManager().goToVictoryScreen();
	}
}
