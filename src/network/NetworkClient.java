package network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import network.interfaces.PacketHandler;
import network.interfaces.WorldObserver;
import network.packets.AddEntityFinishMessage;
import network.packets.AddEntityMessage;
import network.packets.AttackMessage;
import network.packets.ChatMessage;
import network.packets.ChestAccessMessage;
import network.packets.DayNightMessage;
import network.packets.EffectMessage;
import network.packets.EquipItemMessage;
import network.packets.GameUpdateMessage;
import network.packets.GameWonMessage;
import network.packets.InventoryTransferMessage;
import network.packets.ItemTransferMessage;
import network.packets.MoveMessage;
import network.packets.OnActivateMessage;
import network.packets.PingMessage;
import network.packets.PlayerJoinedMessage;
import network.packets.PlayerSetupMessage;
import network.packets.PlayerSpeedMessage;
import network.packets.RemoveEntityMessage;
import network.packets.RightClickMessage;
import network.packets.ServerSaveMessage;
import savefile.SaveUtils;
import world.Activatable;
import world.Actor;
import world.Container;
import world.Entity;
import world.EntitySpawnData;
import world.Inventory;
import world.Player;
import world.World;
import world.entity.item.Item;
import world.entity.item.RightClickable;
import world.ActorState;

import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;

/**
 * The networking client is responsible for packet handling on the client side.
 * Implements the visitor pattern to discern between each packet.
 *
 * Generally the network client is where all the packets will go through.
 *
 * The networking client also implements a world observer and so when some
 * designated event occurs, the client sends the data.
 *
 * In order to deal with interacting with the main game thread, an app queue is
 * utilized.
 *
 * @author Garming Sam 300198721
 *
 */
public class NetworkClient extends WorldObserver implements MessageListener<Client>, PacketHandler{
	private Client client;
	private World world;
	private ClientMain app;
	private boolean ready = false;
	private int playerID;
	private long timeDiff; // difference from server
	private List<AddEntityMessage> queuedAddEntities = new ArrayList<AddEntityMessage>();
	private String name = null;

	/**
	 * Construct a network client.
	 *
	 * @param client
	 *            client connection
	 * @param w
	 *            world
	 * @param app
	 *            client main
	 */
	public NetworkClient(Client client,World w, ClientMain app) {
		this.client = client;
		this.world = w;
		this.app = app;
		world.addObserver(this);
	}

	/**
	 * Send the current location of the player (if the client is ready)
	 */
	public void sendCurrentLocation() {
		if (ready) {
			Player p = world.getPlayer();
			Vector3f pos = p.getLocation();
			client.send(new MoveMessage(playerID, pos, p.getState().ordinal(), p.getDirection().negate()));
		}
	}

	// JME message listener

	@Override
	public void messageReceived(Client source, Message message) {
		((GameUpdateMessage)message).accept(this);
	}

	// Packet handler methods

	@Override
	public void handleMessage(final MoveMessage m){
		if(System.currentTimeMillis() - (m.timestamp + timeDiff) > 1000){ return; } // discard old move messages
		app.enqueue(new Callable<Void>(){
		@Override
			public Void call() throws Exception {
				if (world != null) {
					Entity p;
					if (m.eid == playerID) {
						p = world.getPlayer();
					} else {
						p = world.getEntityByID(m.eid);
					}
					if(p == null)
						return null;
					// move the entity some distance, but not immediately to prevent jerking
					if (p != world.getPlayer()) {
						p.changeLocation((m.x - p.getLocation().x) / 10,
								(m.y - p.getLocation().y) / 10,
								(m.z - p.getLocation().z) / 10);
					}

					if(m.eid != playerID && p instanceof Actor){
						Actor a = (Actor)p;
						Vector3f walkDir = new Vector3f(m.dirA, 0, m.dirC);
						a.setDirection(walkDir.subtract(a.getDirection()).mult(0.9f).normalize());
						a.setState(ActorState.values()[m.state]);
					}

				}
				return null;
			}
		});

	}

	@Override
	public void handleMessage(final AttackMessage m) {
		// handle attacks from other players
		app.enqueue(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Player p = (Player)world.getEntityByID(m.eid);
				p.primaryAction();
				return null;
			}
		});
	}

	@Override
	public void handleMessage(final ChatMessage m) {
		System.out.println(m.source + " " + m.text);
		app.enqueue(new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				app.showMessage(m.source, m.text);
				return null;
			}
		});

	}

	@Override
	public void handleMessage(final EffectMessage m) {
		app.enqueue(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				world.makeEffect(m.getEffect(world));
				return null;
			}
		});
	}

	@Override
	public void handleMessage(final PlayerJoinedMessage m) {
		playerID = m.eid;
		app.enqueue(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				world.setPlayer(world.getEntityByID(playerID));
				world.getScreenManager().setPlayer(world.getPlayer());
				world.getPlayer().getStats().initialise();
				ready = true;
				client.send(m);
				return null;
			}
		});

	}

	@Override
	public void handleMessage(PingMessage pingPacket) {
		System.out.println("PING TIME: " + pingPacket.sentTime + " ms");
		client.send(pingPacket);
		timeDiff = System.currentTimeMillis() - pingPacket.sentTime - pingPacket.timestamp;
	}

	@Override
	public void handleMessage(final ItemTransferMessage itemTransfer) {
		app.enqueue(new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				Player p = (Player)world.getEntityByID(itemTransfer.eid);
				// move the item between world and an inventory
				if (!itemTransfer.drop) {
					p.getContainerInventory()
							.add((Item) world.getEntityByID(itemTransfer.itemID));
					((Item) world.getEntityByID(itemTransfer.itemID)).onPick(); // call pick on item
				} else {
					if(itemTransfer.eid != playerID) p.getContainerInventory().dropItem(
							(Item) world.getEntityByID(itemTransfer.itemID));
				}

				// basically client receives confirmation of the pickup/drop
				return null;
			}
		});

	}

	@Override
	public void handleMessage(final AddEntityMessage m) {
		// defers adding of entities to prevent problems in linking
		synchronized(queuedAddEntities) {
			queuedAddEntities.add(m);
		}
	}

	@Override
	public void handleMessage(AddEntityFinishMessage m) {
		// deferral of adding of entities is finally handled
		app.enqueue(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				synchronized(queuedAddEntities) {
					List<EntitySpawnData> es = new ArrayList<EntitySpawnData>();
					for(AddEntityMessage m : queuedAddEntities) {
						Entity e = SaveUtils.fromBytes(m.data, Entity.class, world.getAssetManager());
						Vector3f loc = Float.isNaN(m.x) ? null : new Vector3f(m.x, m.y, m.z);
						es.add(new EntitySpawnData(e, loc, m.eid));
					}
					queuedAddEntities.clear();
					world.addEntities(es);
				}
				return null;
			}

		});
	}

	@Override
	public void handleMessage(DayNightMessage dayNightMessage) {
		world.setTime(dayNightMessage.time);
	}

	@Override
	public void handleMessage(final RemoveEntityMessage removeEntityMessage) {
		app.enqueue(new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				Entity e = world.getEntityByID(removeEntityMessage.eid);
				if (e != null)
					world.removeEntity(e);
				return null;
			}
		});
	}

	@Override
	public void handleMessage(final ChestAccessMessage chestAccessMessage) {
		app.enqueue(new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				if(chestAccessMessage.open){
					if (world.getPlayer().isInContainer())
						app.showChest(true);
				} else {
					app.displayHUDMessage("YOU COULDN'T OPEN THIS CHEST!");
					world.getPlayer().setInContainer(false, null);
					// ensure that they aren't in the container
				}
				return null;
			}
		});
	}

	@Override
	public void handleMessage(final InventoryTransferMessage m) {
		if (m.idTo == playerID || m.idFrom == playerID) {
			return; // note: this wouldn't work for trading between players
		}

		app.enqueue(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Container c = (Container) world.getEntityByID(m.idTo);
				Item i = (Item) world.getEntityByID(m.itemID);
				Container c2 = (Container) world.getEntityByID(m.idFrom);
				c2.getContainerInventory().removeItem(i);
				c.getContainerInventory().add(i);
				return null;
			}
		});
	}

	@Override
	public void handleMessage(final PlayerSetupMessage m) {
		// message is received on the client when a name change is invoked
		app.enqueue(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				if(m.name != null && !m.name.equals("")){
					// name shouldn't ever be null or empty
					NetworkClient.this.name = m.name;
					app.displayHUDMessage("Current name: " + NetworkClient.this.name);
				} else {
					app.displayHUDMessage("Please set your name using /name");
				}
				return null;
			}
		});
	}


	@Override
	public void handleMessage(final EquipItemMessage m) {
		app.enqueue(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				// handle equips by other players
				Entity e = world.getEntityByID(m.eid);
				Entity i = world.getEntityByID(m.itemID);
				if (!(e instanceof Player) || !(i instanceof Item)) {
					throw new IllegalArgumentException(
							"Non-players cannot equip and you can only equip items.");
				}

				Player p = (Player) e;
				if (m.equip) {
					p.equip((Item) i);
				} else {
					p.unequip((Item) i);
				}
				return null;
			}
		});
	}

	@Override
	public void handleMessage(final RightClickMessage m) {
		app.enqueue(new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				RightClickable r = (RightClickable)world.getEntityByID(m.itemID);
				r.rightClick();
				return null;
			}
		});

	}

	@Override
	public void handleMessage(final OnActivateMessage m) {
		app.enqueue(new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				Activatable a = (Activatable)world.getEntityByID(m.eid);
				a.activate((Player)world.getEntityByID(m.playerID));
				return null;
			}
		});
	}

	@Override
	public void handleMessage(ServerSaveMessage m) {
		// should never get this
	}


	@Override
	public void handleMessage(GameWonMessage m) {
		app.displayHUDMessage("GAME HAS BEEN WON!");
	}


	// Observer methods

	@Override
	public void onPickItem(World w, Item i) {
		Integer id = i.getEntityID();
		if(id != null){
			client.send(new ItemTransferMessage(id, playerID, false));
		}
	}

	@Override
	public void onEquipItem(World w, Item i, boolean equip){
		client.send(new EquipItemMessage(i.getEntityID(), playerID, equip));
	}

	@Override
	public void onActivate(World w, Entity i){
		client.send(new OnActivateMessage(i.getEntityID(), playerID));
	}


	// Methods acting through GUI

	/**
	 * Send the appropriate drop message.
	 *
	 * @param i
	 *            item
	 */
	public void dropItem(Item i) {
		Integer id = i.getEntityID();
		if(id != null) {
			client.send(new ItemTransferMessage(id, playerID, true));
		}
	}

	/**
	 * Send the corresponding chat message. If your name isn't set, you are
	 * marked as a guest.
	 *
	 * @param text
	 *            text
	 */
	public void sendChatMessage(String text) {
		client.send(new ChatMessage((name == null) ? "Guest " + playerID : name, text, playerID));
	}

	/**
	 * Send a save message to the server.
	 */
	public void sendSave() {
		client.send(new ServerSaveMessage());
	}

	/**
	 * Send a chest access to the server.
	 *
	 * @param chest
	 *            chest
	 * @param open
	 *            open or close
	 */
	public void sendChestAcccess(Inventory chest, boolean open) {
		client.send(new ChestAccessMessage(chest.getOwner().getEntityID(), open, playerID));
	}

	/**
	 * Send an attack message to the server.
	 */
	public void sendAttack() {
		client.send(new AttackMessage(playerID, world.getPlayer().getLocation(), world.getPlayer().getDirection().negate()));
	}

	/**
	 * Send a right click to the server.
	 *
	 * @param i
	 *            item
	 */
	public void sendRightClick(Item i) {
		client.send(new RightClickMessage(i.getEntityID(), playerID));
	}

	/**
	 * Set the networking client to ready or not.
	 *
	 * @param b
	 *            is ready
	 */
	public void setReady(boolean b) {
		ready = b;
	}

	/**
	 * Get the current name that is associated with this client.
	 *
	 * @return the name
	 */
	public String getName(){
		return name;
	}

	@Override
	public void handleMessage(PlayerSpeedMessage m) {
		world.setPlayerSpeed(m.speed);
		world.setPlayerSpawn(m.spx, m.spy, m.spz);
	}


}