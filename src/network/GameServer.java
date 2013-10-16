package network;

import java.io.File;

import savefile.SaveUtils;
import world.ActorState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import world.Activatable;
import world.Actor;
import world.Container;
import world.Entity;
import world.Player;
import world.World;
import world.effects.Effect;
import world.entity.item.Item;
import world.entity.item.RightClickable;
import world.entity.mob.Mob;

import com.jme3.math.Vector3f;
import com.jme3.network.ConnectionListener;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Server;

import network.interfaces.PacketHandler;
import network.interfaces.WorldObserver;
import network.packets.*;

/**
 * The game server is responsible for handling packets on the server side.
 * Implements the visitor pattern to discern packets. Follows a general
 * publish/subscribe pattern for networking. Server information mostly overrides
 * client data and essentially all the damage and AI movements are handled on
 * the server.
 *
 * Server implements a world observer and notifies the clients of game updates.
 * In order to deal with multiple threads, an app queue is used.
 *
 * @author Garming Sam 300198721
 *
 */
public class GameServer extends WorldObserver implements MessageListener<HostedConnection>, ConnectionListener, PacketHandler{
	private World world;
	private Map<Integer, HostedConnection> connections = new ConcurrentHashMap<Integer, HostedConnection>();
	private Map<Integer, Integer> conIDToPlayerID = new ConcurrentHashMap<Integer, Integer>();
	private Map<Integer, Integer> playerIDtoConID = new ConcurrentHashMap<Integer, Integer>();
	private PlayerData playerData;
	private ServerMain app;
	private Server server;

	/**
	 * Create a game server.
	 *
	 * @param server
	 *            server
	 * @param w
	 *            world
	 * @param app
	 *            server main
	 * @param data
	 *            data storage for players
	 */
	public GameServer(Server server, World w, ServerMain app, PlayerData data){
		this.playerData = data;
		this.server = server;
		this.world = w;
		this.app = app;

		world.addObserver(this);
	}

	// JME message listener
	@Override
	public void messageReceived(HostedConnection conn, Message msg) {
		((GameUpdateMessage)msg).accept(this);
	}

	@Override
	public void connectionAdded(Server server, final HostedConnection conn) {
		System.out.println("New connection from " + conn.getAddress());
		app.enqueue(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				connections.put(conn.getId(), conn);
				conn.send(new PlayerSpeedMessage(world.getPlayerSpeed(), world.getSpawnPoint()));
				for(Entity o : world.getEntitiesOfClass(Entity.class))
					conn.send(new AddEntityMessage(o.getEntityID(), o, o.getLocation()));
				conn.send(new AddEntityFinishMessage());

				return null;
			}
		});
	}

	@Override
	public void onAddEntity(World w, Entity e) {
		// send both add entity and finish add entity messages to ensure order
		for(HostedConnection conn : connections.values()) {
			conn.send(new AddEntityMessage(e));
			conn.send(new AddEntityFinishMessage());
		}
	}

	@Override
	public void onRemoveEntity(World w, final Entity e) {
		app.enqueue(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				server.broadcast(new RemoveEntityMessage(e.getEntityID()));
				return null;
			}
		});
	}

	@Override
	public void connectionRemoved(Server arg0, HostedConnection arg1) {
		connections.remove(arg1.getId());
		Integer i = conIDToPlayerID.remove(arg1.getId());
		if(i != null) playerIDtoConID.remove(i);
	}

	/**
	 * Broadcast all the connected player positions.
	 */
	public void broadcastPlayerPosition() {
		for (int i : conIDToPlayerID.keySet()) {
			Actor p = (Actor)world.getEntityByID(conIDToPlayerID.get(i));
			if(p == null) continue; // player likely died
			Vector3f pos = p.getLocation();
			MoveMessage m = new MoveMessage(conIDToPlayerID.get(i),pos, p.getState().ordinal(), p.getDirection().negate());
			server.broadcast(m);
		}

	}

	/**
	 * Call a save on the world and save the player data at this current point
	 * in time as well.
	 */
	public void save() {
		app.enqueue(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				SaveUtils.save(world, new File("server.sav"));
				SaveUtils.save(playerData, new File("player-data.sav"));
				return null;
			}
		});
	}

	@Override
	public void handleMessage(final MoveMessage m) {
		app.enqueue(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Entity p = world.getEntityByID(m.eid);
				if (p == null)
					return null;
				p.changeLocation(m.x - p.getLocation().x, m.y
						- p.getLocation().y, m.z - p.getLocation().z);
				((Actor) p).setState(ActorState.values()[m.state]);
				((Actor) p).setDirection(new Vector3f(m.dirA, m.dirB, m.dirC));
				return null;
			}
		});

	}

	@Override
	public void handleMessage(final AttackMessage m) {
		app.enqueue(new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				Player p = (Player)world.getEntityByID(m.eid);
				if(p == null) return null;
				p.setLocation(m.x, m.y, m.z);
				p.setDirection(new Vector3f(m.dirX, m.dirY, m.dirZ)); // note: directions are inverted
				p.primaryAction();
				server.broadcast(Filters.notEqualTo(connections.get(playerIDtoConID.get(m.eid))), m);
				return null;
			}
		});

	}

	/**
	 * Chat system commands: /name - set name, /hit-count - counts hits, /mobs -
	 * disable mob AI, /msg [player] - private message note: only works with
	 * players who register their names
	 *
	 * Any other messages are public.
	 */
	@Override
	public void handleMessage(ChatMessage m) {
		if (m.text.startsWith("/name ")) {
			m.text = m.text.replace("/name ", "");
			if (!playerData.containsName(m.text)) {
				playerData.addData(m.text, m.id);
				//connections.get(playerIDtoConID.get(m.id)).send(
				//		new ChatMessage("Name", m.text, 0));
				connections.get(playerIDtoConID.get(m.id)).send(new PlayerSetupMessage(-1,m.text));
			}
			return;
		} else if(m.text.startsWith("/msg ")){
			Scanner s = new Scanner(m.text);
			s.next();
			if(!s.hasNext()) {
				s.close();
				return;
			}
			String target = s.next();
			if(playerData.containsName(target)){
				int id = playerData.getID(target);
				Integer con = playerIDtoConID.get(id);
				if(con != null && s.hasNext()) {
					s.useDelimiter("\\Z");
					String f = s.next().trim();
					ChatMessage c = new ChatMessage(m.source, f, -1);
					connections.get(con).send(c);
					connections.get(playerIDtoConID.get(m.id)).send(c);
				}
			}
			s.close();
		} else if(m.text.startsWith("/hit-count")){
			List<Player> players = new ArrayList<Player>(world.getPlayers());
			// sort the players in descending order
			Collections.sort(players, new Comparator<Player>(){
				@Override
				public int compare(Player p1, Player p2) {
					return p2.hitCount() - p1.hitCount();
				}
			});
			server.broadcast(new ChatMessage("", "HIT COUNT", -1));
			for(Player p : players){
				int id = p.getEntityID();
				String name = playerData.getName(id);
				server.broadcast(new ChatMessage("",p.hitCount() + " - " + ((name != null) ? name : ("Guest " + id)), -1));
			}
		} else if(m.text.startsWith("/mobs")){
			app.toggleSpawn();
		} else {
			server.broadcast(m);
		}
	}

	@Override
	public void handleMessage(final EffectMessage m) {
		// Not sure if this is ever called.
		app.enqueue(new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				world.makeEffect(m.getEffect(world));
				server.broadcast(m);
				return null;
			}
		});
	}

	@Override
	public void handleMessage(PlayerJoinedMessage m) {
		// Player has their player set, send them some ping messages and
		// day/night. Store their connection.
		conIDToPlayerID.put(m.connection, m.eid);
		playerIDtoConID.put(m.eid, m.connection);
		HostedConnection c = connections.get(m.connection);

		c.send(new DayNightMessage(world.getTime()));
		c.send(new PingMessage(m.eid));
	}

	@Override
	public void handleMessage(PingMessage pingPacket) {
		// Calculate difference between server and client. Ideally this should
		// be stored somewhere to offset lag.
		long diff = System.currentTimeMillis() - pingPacket.timestamp;
		pingPacket.sentTime = diff;
		if (--pingPacket.count > 0) {
			pingPacket.timestamp = System.currentTimeMillis();
			server.broadcast(Filters.equalTo(connections.get(playerIDtoConID.get(pingPacket.id))), pingPacket);
		}
	}

	/**
	 * Broadcast all mob updates.
	 */
	public void updateMobs() {
		for(Mob m : world.getEntitiesOfClass(Mob.class))
			server.broadcast(new MoveMessage(m.getEntityID(), m.getLocation(), ((Actor)m).getState().ordinal(), m.getDirection()));
	}

	@Override
	public void handleMessage(final ItemTransferMessage itemTransfer) {
		app.enqueue(new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				Player p = ((Player)world.getEntityByID(itemTransfer.eid));
				if(p == null) return null;
				Item i = (Item)world.getEntityByID(itemTransfer.itemID);
				if(!itemTransfer.drop){
					p.getContainerInventory().add(i);
					i.onPick(); // item was picked
				} else {
					p.getContainerInventory().dropItem(i);
				}
				server.broadcast(itemTransfer); // resend the message for the client
				return null;
			}
		});
	}

	@Override
	public void handleMessage(AddEntityMessage addEntityMessage) {
		// shouldn't do anything

	}

	@Override
	public void handleMessage(DayNightMessage dayNightMessage) {
		// shouldn't do anything

	}

	@Override
	public void handleMessage(RemoveEntityMessage removeEntityMessage) {
		// shouldn't do anything

	}

	public boolean hasConnectedPlayers(){
		return !conIDToPlayerID.isEmpty();
	}

	@Override
	public void handleMessage(final ChestAccessMessage m) {
		app.enqueue(new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				Container c = (Container)world.getEntityByID(m.chestID);
				// if opening
				if(m.open){
					if(c.canAccess()){
						c.setCanAccess(false);
					} else {
						m.open = false;
					}
					//send the player the chest access message again
					connections.get(playerIDtoConID.get(m.eid)).send(m);
				} else {
					// if closing
					c.setCanAccess(true);
					//connections.get(playerIDtoConID.get(m.eid)).send(m);
				}
				return null;
			}
		});

	}

	@Override
	public void handleMessage(final InventoryTransferMessage m) {
		app.enqueue(new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				Container c = (Container)world.getEntityByID(m.idTo);
				Item i = (Item)world.getEntityByID(m.itemID);
				Container c2 = (Container)world.getEntityByID(m.idFrom);
				c2.getContainerInventory().removeItem(i);
				c.getContainerInventory().add(i);
				server.broadcast(m); // ensure that the item transfer is told across the clients
				return null;
			}
		});
	}

	@Override
	public void handleMessage(final EquipItemMessage m) {
		app.enqueue(new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				Entity e = world.getEntityByID(m.eid);
				Entity i = world.getEntityByID(m.itemID);
				if(!(e instanceof Player) || !(i instanceof Item)){
					throw new IllegalArgumentException("Non-players cannot equip and you can only equip items.");
				}

				Player p = (Player)e;

				if(m.equip){
					p.equip((Item)i);
				} else {
					p.unequip((Item)i);
				}

				server.broadcast(Filters.notEqualTo(connections
						.get(playerIDtoConID.get(m.eid))), m);

				return null;
			}

		});

	}

	@Override
	public void handleMessage(ServerSaveMessage m) {
		save();
		System.out.println("Server world saved.");

	}

	@Override
	public void handleMessage(AddEntityFinishMessage m) {
		// shouldn't happen on server
	}

	@Override
	public void handleMessage(final RightClickMessage m) {
		app.enqueue(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				RightClickable r = (RightClickable) world
						.getEntityByID(m.itemID);
				r.rightClick();
				server.broadcast(Filters.notEqualTo(connections
						.get(playerIDtoConID.get(m.player))), m);
				// broadcast the right click to everyone else
				return null;
			}
		});
	}

	@Override
	public void handleMessage(final OnActivateMessage m) {
		app.enqueue(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Activatable a = (Activatable) world.getEntityByID(m.eid);
				a.activate((Player) world.getEntityByID(m.playerID));
				server.broadcast(Filters.notEqualTo(connections
						.get(playerIDtoConID.get(m.playerID))), m);
				// broadcast the activate
				return null;
			}
		});

	}

	@Override
	public void handleMessage(final PlayerSetupMessage m) {
		// if an improper name is given, just add them and not their name.
		app.enqueue(new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				if(m.name == null || m.name.equals("")){
					Entity e = world.addOtherPlayer();
					e.changeLocation(new Vector3f(0, 200, 0));
					connections.get(m.conID).send(new PlayerJoinedMessage(e.getEntityID(), m.conID));
					connections.get(m.conID).send(new PlayerSetupMessage());
					return null;
				}
				if(playerData.containsName(m.name)){
					Entity e = world.getEntityByID(playerData.getID(m.name));
					if(e == null || playerIDtoConID.get(e.getEntityID()) != null){
						// if there is already a player connected using this name or the entity is missing
						// add the player but don't record a name
						e = world.addOtherPlayer();
						e.changeLocation(new Vector3f(0, 200, 0));
						connections.get(m.conID).send(new PlayerJoinedMessage(e.getEntityID(), m.conID));
						connections.get(m.conID).send(new PlayerSetupMessage());
					} else {
						// name is correct
						connections.get(m.conID).send(new PlayerJoinedMessage(e.getEntityID(), m.conID));
						connections.get(m.conID).send(m);
					}
				} else {
					Entity e = world.addOtherPlayer();
					e.changeLocation(new Vector3f(0, 200, 0));
					connections.get(m.conID).send(new AddEntityFinishMessage());
					connections.get(m.conID).send(new PlayerJoinedMessage(e.getEntityID(), m.conID));
					playerData.addData(m.name, e.getEntityID());
					//names.put(e.getEntityID(), m.name);
					//nameToID.put(m.name, e.getEntityID());
					// name is correct
					connections.get(m.conID).send(m);
				}
				return null;
			}
		});

	}


	@Override
	public void handleMessage(GameWonMessage m) {
		// doesn't do anything

	}


	/// observer methods

	@Override
	public void onPickItem(World w, Item i) {
		// picking happens on the clients
	}

	@Override
	public void addEffect(World w, Effect e) {
		server.broadcast(new EffectMessage(e));
	}

	@Override
	public void onDayNightChange(World w, float time) {
		server.broadcast(new DayNightMessage(time));
	}

	@Override
	public void onGameWon(World w) {
		server.broadcast(new GameWonMessage());
	}

	@Override
	public void handleMessage(PlayerSpeedMessage m) {
		// do nothing

	}

}