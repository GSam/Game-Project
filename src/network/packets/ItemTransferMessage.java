package network.packets;

import com.jme3.network.serializing.Serializable;

import network.interfaces.PacketHandler;

/**
 * Message for informing clients and the server of item pickups and drops.
 * 
 * @author Garming Sam 300198721
 * 
 */
@Serializable
public class ItemTransferMessage extends GameUpdateMessage {
	public int itemID;
	public int eid;
	public boolean drop;

	public ItemTransferMessage(){
	}

	public ItemTransferMessage(int itemID, int playerID, boolean drop){
		super(true);
		this.itemID = itemID;
		this.eid = playerID;
		this.drop = drop;
	}

	@Override
	public void accept(PacketHandler visitor) {
		visitor.handleMessage(this);
	}

}
