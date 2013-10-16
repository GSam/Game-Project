package network.packets;

import network.interfaces.PacketHandler;

import com.jme3.network.serializing.Serializable;

/**
 * Message to be sent when a chest is attempted to be accessed or closed. Only
 * upon proper receipt of this message can a player open a chest.
 * 
 * @author Garming Sam 300198721
 * 
 */
@Serializable
public class ChestAccessMessage extends GameUpdateMessage{
	public int chestID;
	public int eid;
	public boolean open;

	public ChestAccessMessage(){

	}

	public ChestAccessMessage(int chestID, boolean b, int eid){
		super(true);
		this.chestID = chestID;
		this.eid = eid;
		this.open = b;
	}

	@Override
	public void accept(PacketHandler visitor) {
		visitor.handleMessage(this);
	}
}
