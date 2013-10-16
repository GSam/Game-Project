package world;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import network.interfaces.WorldObserver;
import renderer.Sun;
import renderer.VisualEffectsManager;
import world.PhysicsUtilities.EntityHitResult;
import world.audio.AudioTimeEvent;
import world.effects.Effect;
import world.entity.item.Item;
import world.entity.item.miscellaneous.Treasure;
import world.entity.mob.Mob;
import world.entity.staticentity.OutpostGate;
import world.entity.staticentity.SimpleContainer;
import world.entity.staticentity.WorldObject;
import world.entity.trigger.TriggerCollisionListener;
import GUI.Managers.ScreenManager;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.audio.AudioNode;
import com.jme3.audio.Listener;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.input.ChaseCamera;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * The World class is a top-level class that coordinates all the (non-collisions) logic for the game.
 *
 * World is an instance of the Facade pattern for the world package.
 *
 * @author Tony 300242775
 */
public class World implements Savable{

    private static final boolean PHYSICS_DEBUG = false;
    /**
     * Whether the fix for strange door behaviour is enabled or not.
     */
    public static final boolean HOPPING_DOORS = false;

    /**
     * Whether shadows are enabled or not.
     */
    public static final boolean SHADOWS = false;

    /**
     * The file to load the game world from.
     */
    public static String MAPFILE = "maps/OutpostMap.txt";

    /**
     * The maximum number of mobs that can be in the game world at once.
     */
    public static int MAX_MOBS = Integer.MAX_VALUE;

    PriorityQueue<Mob> mobQueue;

    /**
     *
     * Set when running some JUnit tests which don't care about physics, to make them run faster.
     */
    public boolean USE_SUPERFAST_PHYSICS = false;

    /**
     * Set when running some JUnit tests that don't care about rendering, to make them run faster.
     */
    public static boolean USE_SUPERFAST_RENDERING = false;

    private List<WorldObserver> observers = new ArrayList<WorldObserver>();
    private ScreenManager screenManager;
    private WorldType worldType;
    private boolean usePathNodes = true;
    private Collection<PathNode> pathNodes = new ArrayList<PathNode>();

    // temp when loading
    private Collection<EntitySpawnData> loadedEntities = null;

    private Collection<Entity> entities = new CopyOnWriteArraySet<Entity>();
    private Map<Integer, Entity> entitiesByID = new HashMap<Integer, Entity>();
    private Collection<Entity> updatableEntities = new CopyOnWriteArraySet<Entity>();

    private List<Player> otherPlayers;
    private Player player;

    private Vector3f spawnPoint;
    private float playerSpeed;

    private boolean enableMobSpawning = true;

    private MobSpawnController spawner;
    private Collection<Effect> effects = new CopyOnWriteArraySet<Effect>();

    private AssetManager assetManager;
    private PhysicsSpace physics;

    private ExpensiveOperationManager expensiveOperationManager = new ExpensiveOperationManager();

    private int nextEntityID = 0;

    private Node root;
    private Node mobs;
    private Node rigidEntities;
    private Node frees;
    private AudioNode nightCommences;

    private BulletAppState bulletAppState;
    private Camera camera;
    private Listener audioListener;
    private TimeManager timeManager;
    private VisualEffectsManager vem;

    public World(){
        // create the scenegraph structure.
        this.root = new Node();
        this.mobs = new Node();
        this.rigidEntities = new Node();
        this.frees = new Node();

        root.attachChild(mobs);
        root.attachChild(rigidEntities);
        root.attachChild(frees);

        bulletAppState = new BulletAppState();

        spawner = new MobSpawnController(this);
    }

    // INITIALISATION

    /**
     * Removes this World from the core JMonkey SimpleApplication state.
     *
     * @param appManager
     */
    public void detachFromGame(AppStateManager appManager){
        physics.disableDebug();
        bulletAppState.setDebugEnabled(false);
        appManager.detach(bulletAppState);
        if(vem != null)
        	vem.detachFromViewPort();
    }

    /**
     * Adds this World from the core JMonkey SimpleApplication state, and initialises core components of the World.
     *
     * @param appManager
     * @param assetManager
     * @param cam
     * @param type
     * @param viewPort
     */
    @SuppressWarnings("deprecation")
    public void attachToGame(AppStateManager appManager, AssetManager assetManager, Listener audioListener, Camera cam, WorldType type, ViewPort viewPort){
        this.assetManager = assetManager;
        this.camera = cam;
        this.worldType = type;
        appManager.attach(bulletAppState);
        physics = bulletAppState.getPhysicsSpace();
        physics.setGravity(new Vector3f(0, -100, 0));
        if(PHYSICS_DEBUG)
            physics.enableDebug(assetManager);
        physics.addCollisionListener(new ActorCollisionManager());
        physics.addCollisionListener(new TriggerCollisionListener());
        assetManager.registerLocator("assets/Models/WorldObjects.zip", ZipLocator.class);
        assetManager.registerLocator("assets/Models/NewAsses.zip", ZipLocator.class);
        assetManager.registerLocator("assets/Scenes/terrain.zip", ZipLocator.class);

        if(otherPlayers == null)
            otherPlayers = new ArrayList<Player>();

        if(loadedEntities != null){
            addEntities(loadedEntities);
            loadedEntities = null;
        } else{

            if(type != WorldType.CLIENT){
                initializeWorldObjectsFromFile(MAPFILE);
                initializePathGraph();
            }
            if(type == WorldType.SP){
                player = new Player();
                otherPlayers.add(player);
                addEntity(player, spawnPoint);
                player.setCamera(camera);
                player.setAudioListener(audioListener);
            }
        }

        vem = new VisualEffectsManager(assetManager, root, viewPort);
        timeManager = new TimeManager(this, vem);
        vem.removeLightScattering();     //Uncomment this if you have a powerful computer mr markers
        if(SHADOWS){
            root.setShadowMode(ShadowMode.CastAndReceive);
        } else{
            vem.removeShadows();
        }

        if(USE_SUPERFAST_RENDERING){
            vem.removeFog();
            vem.removeLightScattering();
            vem.removeShadows();
            vem.removeWater();
        }

        setupAudio();

    }

    private void setupAudio(){
        nightCommences = new AudioNode(assetManager, "Sounds/environment/NightCommences_reenc.ogg", true);
        nightCommences.setLooping(false); // activate continuous playing
        nightCommences.setPositional(false);
        nightCommences.setVolume(4);
        AudioTimeEvent nightEvent = new AudioTimeEvent(nightCommences);
        if(worldType != WorldType.SERVER)
            vem.addAudioListener(nightEvent);
        root.attachChild(nightCommences);
    }

    private void initializeWorldObjectsFromFile(String filename){
        MapLoader loader = new MapLoader();
        Collection<MapLoader.Tuple> ent = loader.loadMap(filename);

        spawnPoint = loader.getSpawnPoint();
        winZone = loader.getWinZone();
        playerSpeed = loader.getPlayerSpeed();

        if(spawnPoint == null){
            spawnPoint = new Vector3f(0,50,0);
        }

        for(MapLoader.Tuple t : ent){
            addEntity(t.entity, t.location);

            if(t.entity instanceof WorldObject && !(t.entity instanceof OutpostGate)){
                ((WorldObject) t.entity).addSpecialNodes();
            }
        }
    }

    /**
     * Set by worldTestMain to establish communication channels between the GUI and player.
     *
     * @param manager
     */
    public void attachScreenManager(ScreenManager manager){
        this.screenManager = manager;
    }

    // ENTITY CREATION

    /**
     * Adds multiple entities at once. If some of the entities in the list require other entities in the list to be loaded before them, this will still work,
     * unlike addEntity which would have to be called for the entities in the right order.
     *
     * @throw IllegalStateException if any of the entities are already added, or any of the entity IDs are already in use
     */
    public void addEntities(Collection<EntitySpawnData> es){
        for(EntitySpawnData e : es)
            if(entities.contains(e.e) || entitiesByID.containsKey(e.id))
                throw new IllegalStateException("duplicate entity or ID");

        for(EntitySpawnData e : es){
            entities.add(e.e);
            entitiesByID.put(e.id, e.e);
            if(e.e.isUpdatable())
                updatableEntities.add(e.e);
        }
        for(EntitySpawnData e : es)
            e.e.linkToWorld(this, e.location, e.id);
        for(EntitySpawnData e : es)
            if(e.e instanceof Container)
                ((Container) e.e).getContainerInventory().linkToWorld(this);
    }

    /**
     * Adds an entity to the world. Throws an exception if called on a client world.
     *
     * @param e
     *            The entity to add.
     * @param location
     *            The entity's initial location.
     */
    public void addEntity(Entity e, Vector3f location){
        if(location == null)
            throw new NullPointerException("location is null");
        if(e == null)
            throw new NullPointerException("entity is null");
        if(!entities.add(e)){
            throw new IllegalStateException("Entity already added");
        }
        if(getWorldType() == WorldType.CLIENT)
            throw new IllegalStateException("Cannot create entities on the client");
        int id;
        do
            id = nextEntityID++;
        while(entitiesByID.containsKey(id));
        entitiesByID.put(id, e);

        e.linkToWorld(this, location, id);

        if(e instanceof Container)
            ((Container) e).getContainerInventory().linkToWorld(this);

        e.setLocation(location);
        for(WorldObserver o : observers)
            o.onAddEntity(this, e);

        if(e.isUpdatable())
            updatableEntities.add(e);
    }

    /**
     * Removes the passed Entity from the world space and notifies the server
     *
     * @param e
     *            the Entity to remove
     */
    public void removeEntity(Entity e){
        for(WorldObserver o : observers)
            o.onRemoveEntity(this, e);

        if(e.isUpdatable())
            updatableEntities.remove(e);

        otherPlayers.remove(e);
        entities.remove(e);
        entitiesByID.remove(e.getEntityID());
        e.unlinkFromWorld(this);
    }

    private void addWorldObject(String filename, float x, float y, float z, Vector3f vecscale, float scale, float angle, boolean cull){
        WorldObject wo = new WorldObject(filename, vecscale, scale, angle, cull);
        addEntity(wo, new Vector3f(x, y, z));
        wo.addSpecialNodes();
    }

    // GAME LOGIC

    /**
     * Performs one tick of game-logic, including updating all Mobs and any items that need updating.
     *
     * This should be called from the main loop.
     */
    public void gameTick(float tpf){
        if(player != null)
            player.update(tpf);

        expensiveOperationManager.update(tpf);
        timeManager.update(tpf);

        for(Effect e : effects){
            e.linkToWorld(this); // effects aren't linked anywhere else?
            e.update(tpf);
        }

        int count = 0;
        for(Entity e : updatableEntities){
            if(e.isRemovedFromWorld())
                continue;
            if(e instanceof Mob){
                count++;
                e.update(tpf);
            } else{
                e.update(tpf);
            }
        }

        if(enableMobSpawning && count < MAX_MOBS)
            spawner.spawnTick(tpf);

        if(gameIsWon()){
            for(WorldObserver o : observers){
                o.onGameWon(this);
            }
        }

    }

    private BoundingBox winZone = new BoundingBox(Vector3f.UNIT_X, Vector3f.UNIT_XYZ);

    private boolean gameIsWon(){
        Vector3f location;
        for(Player p : otherPlayers){
            location = p.getLocation();
            if(!winZone.contains(location))
                return false;
        }

        for(Entity item : entities){
        	if(!(item instanceof Treasure))
        		continue;

            if(((Treasure)item).getInventory() == null || !(((Treasure)item).getInventory().getOwner() instanceof Player))
                return false;
        }

        return true;
    }

    // EFFECT CREATION AND DESTRUCTION

    /**
     * Removes an Effect from the game world, including the scenegraph and physics space.
     *
     * @param effect
     *            the Effect to remove
     */
    public void destroyEffect(Effect effect){
        effects.remove(effect);
    }

    /**
     * Create an Effect in the game world, start it (effect.start() is guaranteed to be called), and notify the server.
     *
     * @param effect
     */
    public void makeEffect(Effect effect){
        effects.add(effect);
        effect.linkToWorld(this);
        effect.start();

        // NETWORKING
        // setChanged();
        // notifyObservers(effect);
        for(WorldObserver o : observers){
            o.addEffect(this, effect);
        }

        /*
         * for (int i=0; i < effects.size(); i++) { if (effects.get(i).shouldBeDestroyed()) { effects.remove(i); } }
         */
    }

    /**
     * Similar to make effect but makes an effect locally on system running it. Doesn't alert any observers of that change.
     *
     * @param effect
     *            effect
     */
    public void makeLocalEffect(Effect effect){
        effects.add(effect);
        effect.linkToWorld(this);
        effect.start();
    }

    // PLAYER ACTIONS

    /**
     * Handles all logic for when the user presses the 'pick' button. Eg. picking up items.
     *
     * @param location
     *            the location of the player that picks
     * @param direction
     *            the direction of the player that picks
     */
    public void playerPick(Vector3f location, Vector3f direction){
        List<EntityHitResult> results = PhysicsUtilities.raycastHitResult(location, direction, root);

        if(results.size() < 2)
            return; // the first element is always the player.

        EntityHitResult hit = results.get(1);
        if(hit.entity instanceof Item && hit.distance <= Player.MAX_PICKUP_DISTANCE){ // needs to subclass so not using Item.class
            Item item = (Item) (hit.entity);

            // if it isn't the client, attempt to pick up the item
            if(worldType != WorldType.CLIENT){
                if(!player.addItem(item))
                    return; // index 0 is the player model
                item.onPick();
            }

            for(WorldObserver o : observers){
                o.onPickItem(this, item);
            }
        }
    }

    /**
     * Handles all logic for when the user presses the 'secondary action' button. Eg. opening chests or activating things.
     *
     * @param location
     *            the location of the player
     * @param direction
     *            the direction of the player
     */
    public void playerSecondaryAction(Vector3f location, Vector3f direction){
        List<Entity> results = PhysicsUtilities.raycast(location, direction, root);

        if(results.size() < 2)
            return; // the first element is always the player.

        if(results.get(1) instanceof Activatable){ // activate things
            ((Activatable) results.get(1)).activate(getPlayer());

            for(WorldObserver o : observers){
                o.onActivate(this, results.get(1));
            }

        } else if(results.get(1) instanceof Container){ // open things
            /*
             * TODO hook into the GUI for opening a container.
             */
        }
    }

    // GETTERS AND SETTERS

    /**
     * @return the Sun linked to this World
     */
    public Sun getSun(){
        return vem.getSun();
    }

    /**
     * @return the VisualEffectsManager of this World
     */
    public VisualEffectsManager getVisualEffectsManager(){
        return vem;
    }

    /**
     * @return the WorldType of this world
     */
    public WorldType getWorldType(){
        return worldType;
    }

    /**
     * @return the root Node of this World's scenegraph
     */
    public Node getNode(){
        return root;
    }

    /**
     * @return the Node the groups StaticEntities on the scenegraph
     */
    public Node getRigidNode(){
        return rigidEntities;
    }

    /**
     * @return the Node the groups Mobs on the scenegraph
     */
    public Node getMobNode(){
        return mobs;
    }

    /**
     * @return this World's (client) Player
     */
    public Player getPlayer(){
        return player;
    }

    /**
     * Sets the Player's chase camera to the passed ChaseCamera
     *
     * @param chase
     *            the camera to add
     */
    public void setChaseCam(ChaseCamera chase){
        player.setChaseCam(chase);
        // this.chaseCam = chase;
    }

    /**
     * @return this World's PhysicsSpace
     */
    public PhysicsSpace getPhysicsSpace(){
        return physics;
    }

    /**
     * @return the TimeManager linked to this World
     */
    public TimeManager getTimeManager(){
        return timeManager;
    }

    /**
     * @return an AssetManager that loads from the default assets folder
     */
    public AssetManager getAssetManager(){
        return assetManager;
    }

    /**
     * @return the ScreenManager attached to this World
     */
    public ScreenManager getScreenManager(){
        return screenManager;
    }

    /**
     * @return all the Entities in this World
     */
    public Collection<Entity> getEntities(){
        return entities;
    }

    /**
     * Returns a Collection of all the Entities of the passed class in the game world.
     *
     * @param clazz
     *            the Class of the Entities to return
     * @return all Entities of the passed Class
     */
    public <T> Collection<T> getEntitiesOfClass(Class<T> clazz){
        Collection<T> rv = new ArrayList<T>();
        for(Entity e : entities)
            if(clazz.isInstance(e))
                rv.add(clazz.cast(e));
        return rv;
    }

    /**
     * @return the ExpensiveOperationManager linked to this World.
     */
    public ExpensiveOperationManager getExpensiveOperationManager(){
        return expensiveOperationManager;
    }

    // NETWORKING

    /**
     * @return all the Players currently connected to this game
     */
    public Collection<Player> getPlayers(){
        return otherPlayers;
    }

    /**
     * Add a WorldObserver to be notified when this World object changes.
     *
     * @param observer
     *            the WorldObserver to add
     */
    public void addObserver(WorldObserver observer){
        observers.add(observer);
    }

    /**
     * TODO Alex Craig
     *
     * @param i
     * @param b
     */
    public void observeEquipItem(Item i, boolean b){
        for(WorldObserver o : observers){
            o.onEquipItem(this, i, b);
        }
    }

    /**
     * Sets the player of this World to the passed Entity.
     *
     * @param entity
     *            the Entity to set as the Player
     */
    public void setPlayer(Entity entity){
        if(entity == null)
            throw new NullPointerException("entity");
        player = (Player) entity;
        player.setCamera(camera);
    }

    /**
     * @param eid
     *            the id to find the Entity for
     * @return the Entity with the passed id
     */
    public Entity getEntityByID(int eid){
        return entitiesByID.get(eid);
    }

    /**
     * Adds another player to this world, specifically this happens for the server.
     *
     * @return the new player
     */
    public Player addOtherPlayer(){
        Player p = new Player();
        addEntity(p, spawnPoint);
        otherPlayers.add(p);
        return p;
    }

    // PATHFIDNING

    /**
     * @return all the PathNodes in this World
     */
    public Collection<PathNode> getPathNodes(){
        return pathNodes;
    }

    /**
     * @param location
     *            the point to get the closest PathNode to
     * @return the PathNode closest to the passed Vector3f
     */
    public PathNode getClosestPathNode(Vector3f location){
        float bestDistSq = Float.POSITIVE_INFINITY;
        PathNode best = null;
        for(PathNode pn : pathNodes){
            float dist = pn.position.distanceSquared(location);
            if(dist < bestDistSq && PhysicsUtilities.checkLineOfSight(location, pn.position, root, true)){
                bestDistSq = dist;
                best = pn;
            }
        }
        return best;
    }

    /**
     * If setUsePathNodes(false) is called, then path nodes will be ignored and will not be loaded. This is used to make tests run faster. It is not called
     * normally.
     */
    public void setUsePathNodes(boolean value){
        usePathNodes = value;
    }

    public boolean getUsePathNodes(){
        return usePathNodes;
    }

    /**Sets up a queue of mobs according to distance from the player, and returns the Mob
     * queue members in order. If the queue is empty, a new one is created.
     * @param player - player selecting mobs
     * @return - next mob in the queue
     */
    public Mob getTarget(Player player){
        if(mobQueue == null || mobQueue.size() == 0){
            mobQueue = new PriorityQueue<Mob>(20, new MobDistComparator(getPlayer()));
            for(Spatial ent : mobs.getChildren()){
                Mob mob = (Mob) (ent.getUserData("entity"));
                if(mob.getLocation().distance(player.getLocation()) < 100f){
                    mobQueue.add(mob);
                }
            }
        }
        return mobQueue.poll();
    }

    /**
     * Used to compare the distances of two mobs when using the
     * tab targetting system
     * @author Craig
     *
     */
    private class MobDistComparator implements Comparator<Mob>{

        Vector3f playerLocation;

        public MobDistComparator(Player p){
            this.playerLocation = p.getLocation();
        }

        @Override
        public int compare(Mob m1, Mob m2){
            float m1Dist = m2.getLocation().distance(playerLocation);
            float m2Dist = m2.getLocation().distance(playerLocation);
            if(m1Dist < m2Dist)
                return 1;
            else if(m1Dist == m2Dist)
                return 0;
            else
                return -1;
        }
    }

    /**
     * @param point
     *            the point to get the closest Player to
     * @return the Player closest to the passed Vector3f
     */
    public Player getNearestPlayer(Vector3f point){
        float minDist = Float.MAX_VALUE;
        Player closest = null;
        float curDist;

        for(Player p : otherPlayers){
            curDist = p.getLocation().distanceSquared(point);
            if(curDist < minDist && !p.isInvisible()){
                minDist = curDist;
                closest = p;
            }
        }

        return closest;
    }

    /**
     * Returns the Entity of the passed class closest to the passed Vector3f.
     *
     * @param point
     *            the point to get the closest Entity to
     * @param entClass
     *            the Class of the Entity to find
     * @param exclude
     *            an Entity that is guaranteed not to be returned
     * @return the Entity of the given class closest to the passed point
     */
    public <T> T getNearestEntity(Vector3f point, Class<T> entClass, Entity exclude){
        float minDist = Float.MAX_VALUE;
        T closest = null;
        float curDist;

        for(Entity p : entities){
            if(p == exclude)
                continue;
            if(!entClass.isInstance(p))
                continue;

            curDist = p.getLocation().distanceSquared(point);
            if(curDist < minDist){
                minDist = curDist;
                closest = entClass.cast(p);
            }
        }

        return closest;
    }

    /**
     * @return a random Player from all the Players currently connected
     */
    public Player getRandomPlayer(){
        int seen = 0;
        int numPlayers = otherPlayers.size();
        if(numPlayers != 0){
            while(seen < numPlayers){
                int random = (int) Math.floor(Math.random() * otherPlayers.size());
                if(!otherPlayers.get(random).isInvisible())
                    return otherPlayers.get(random);
            }
        }
        return player.isInvisible() ? null : player;
    }

    // DATA SAVING AND LOADING

    @SuppressWarnings("unchecked")
    @Override
    public void read(JmeImporter arg0) throws IOException{
        InputCapsule ic = arg0.getCapsule(this);
        loadedEntities = (Collection<EntitySpawnData>) ic.readSavableArrayList("savedents", null);

        otherPlayers = ic.readSavableArrayList("otherPlayers", null);
        player = (Player) ic.readSavable("player", null);
        spawner = new MobSpawnController(this);
        // effects = new HashSet<Effect>(ic.readSavableArrayList("effects",
        // null));
        // root = (Node)ic.readSavable("root", null);
        nextEntityID = ic.readInt("nextEntityID", 0);

        pathNodes.clear();
        pathNodes.addAll(ic.readSavableArrayList("pathNodes", null));

        effects.clear();
        effects.addAll(ic.readSavableArrayList("effects", null));

        spawnPoint = (Vector3f) ic.readSavable("spawn", new Vector3f(0, 50, 0));
        winZone = (BoundingBox) ic.readSavable("winZone", winZone);
        playerSpeed = ic.readFloat("playerSpeed", 2);
    }

    @Override
    public void write(JmeExporter arg0) throws IOException{
        OutputCapsule oc = arg0.getCapsule(this);

        ArrayList<EntitySpawnData> savedents = new ArrayList<EntitySpawnData>(entities.size());
        for(Entity e : entities)
            savedents.add(new EntitySpawnData(e));
        oc.writeSavableArrayList(savedents, "savedents", null);

        oc.writeSavableArrayList(new ArrayList<Player>(otherPlayers), "otherPlayers", null);
        oc.write(player, "player", null);
        // oc.writeSavableArrayList(new ArrayList<Effect>(effects), "effects",
        // null);
        // oc.write(root, "root", null);
        oc.write(nextEntityID, "nextEntityID", 0);
        oc.writeSavableArrayList(new ArrayList<PathNode>(pathNodes), "pathNodes", null);
        oc.writeSavableArrayList(new ArrayList<Effect>(effects), "effects", null);
        oc.write(spawnPoint, "spawn", null);
        oc.write(winZone, "winZone", null);
        oc.write(playerSpeed, "playerSpeed", 0);
    }

    /**
     * Links path nodes together to form the path graph.
     */
    private void initializePathGraph(){
        for(PathNode n : pathNodes)
            n.link();
    }

    public void addPathNode(PathNode pathNode){
        pathNodes.add(pathNode);
    }

    public Set<String> getPreloadAssets(){
        Set<String> rv = new HashSet<String>();
        for(Entity e : entities)
            e.getPreloadAssets(rv);
        return rv;
    }

    /**
     * @return the current time of this World
     */
    public float getTime(){
        return timeManager.getTime();
    }

    /**
     * Sets the current time of this world to the passed time.
     *
     * @param time
     *            the time to set to
     */
    public void setTime(float time){
        timeManager.setTime(time);
    }

    public void setEnableMobSpawning(boolean value){
        enableMobSpawning = value;
    }

	public Vector3f getSpawnPoint() {
		return spawnPoint;
	}

	public float getPlayerSpeed() {
		return playerSpeed;
	}

	/**
	 * Allows a time update to be sent.
	 * @param time
	 */
	public void updateTime(float time) {
		for(WorldObserver o : observers){
			o.onDayNightChange(this, time);
		}

	}

	public void setPlayerSpeed(float speed) {
		this.playerSpeed = speed;

	}

	public void setPlayerSpawn(float spx, float spy, float spz) {
		this.spawnPoint = new Vector3f(spx, spy, spz);
	}
}
