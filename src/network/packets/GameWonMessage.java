package network.packets;

import com.jme3.network.serializing.Serializable;

import network.interfaces.PacketHandler;

@Serializable
public class GameWonMessage extends GameUpdateMessage {

	@Override
	public void accept(PacketHandler visitor) {
		visitor.handleMessage(this);

	}
}
