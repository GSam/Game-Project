package network.packets;

import com.jme3.network.serializing.Serializable;

import network.interfaces.PacketHandler;

/**
 * Message to be sent when a player equips an item.
 * 
 * @author Garming Sam 300198721
 * 
 */
@Serializable
public class EquipItemMessage extends GameUpdateMessage {
	public int itemID;
	public boolean equip;
	public int eid;
	
	public EquipItemMessage() {
	}
	
	public EquipItemMessage(int itemID, int eid, boolean equip){
		super(true);
		this.itemID = itemID;
		this.equip = equip;
		this.eid = eid;
	}
	  
	@Override
	public void accept(PacketHandler visitor) {
		visitor.handleMessage(this);
	}

}
