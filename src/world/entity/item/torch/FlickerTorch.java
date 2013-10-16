package world.entity.item.torch;

import world.entity.item.ItemInfo;
import world.entity.item.Stat;

import com.jme3.math.ColorRGBA;

public class FlickerTorch extends SimpleTorch {
	public FlickerTorch() {}
	
	public FlickerTorch(ColorRGBA col, float radius, float power, float range, Stat[] infoKeys, String[] infoValues, String meshPath) {
		super(col, radius, power, range, infoKeys, infoValues, meshPath);
	}
	
	@Override
	public boolean isUpdatable() {
		return true;
	}

	private float updateTimer;
	public void update(float tpf) {
		super.update(tpf);
		
		updateTimer += tpf;
		if(updateTimer > 0.1) {
			updateTimer = Math.min(0.1f, updateTimer - 0.1f);
			if(isActive != (Math.random() > 0.2))
				activate(null);
		}
	}
}
