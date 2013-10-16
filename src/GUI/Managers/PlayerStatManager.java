package GUI.Managers;

import java.util.HashMap;
import java.util.Map;
import world.ActorStatObserver;
import world.MobObserver;
import world.Player;
import world.entity.item.Stat;
import world.entity.mob.Mob;
import GUI.CustomEffects.EffectSource;
import GUI.CustomEffects.GuiEffectType;
import com.jme3.light.AmbientLight;
import com.jme3.light.Light;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;

/**
 * Observes changes in player stats that need to be registered with the GUI
 * Also updates mobs targeted by the player
 * @author Craig
 */
public class PlayerStatManager implements ActorStatObserver, MobObserver{

	private Player player;
	private Nifty nifty;
	private ScreenManager screenManager;
	private float lastHealth = Float.MAX_VALUE;
	private float lastEnergy = Float.MAX_VALUE;
	private EffectSource mobSource;

	public PlayerStatManager(Nifty nifty, ScreenManager screenManager){
		this.nifty = nifty;
		this.screenManager = screenManager;
	}

	/**Sets the player for this manager
	 * 
	 * @param p - the Player to set
	 */
	public void setPlayer(Player p) {
		if(player != null) {
			player.getStats().removeObserver(this);
		}

		this.player = p;

		if(player != null) {
			player.addMobObserver(this);
			player.getStats().addObserver(this);
			setStatEffects(player);
			player.getStats().initialise();
			for(Stat s : Stat.values())
				update(s, p.getStats().getStat(s));
		}
	}

	/**Called by the ActorStat class this listener is attached to
	 * Adjusts the hud elements accordingly
	 * 
	 * @param stat - the Stat to update
	 * @param value - the value of the stat to adjust
	 * 
	 */
	public void update(Stat stat , float value){

		switch(stat){

		case ARMOUR:
			Element armour = nifty.getScreen("hud").findElementByName("CharArmour");
			armour.getRenderer(TextRenderer.class).setText("ARMOUR  "+player.getStats().getStat(Stat.ARMOUR));
			break;

		case DAMAGE:
			Element damage = nifty.getScreen("hud").findElementByName("CharDamage");
			damage.getRenderer(TextRenderer.class).setText("DAMAGE  "+player.getStats().getStat(Stat.DAMAGE));
			break;

		case HEALTH:
			Element health = nifty.getScreen("hud").findElementByName("CharHealth");
			health.getRenderer(TextRenderer.class).setText("HEALTH  "+player.getStats().getStat(Stat.HEALTH));
			screenManager.getHudScreenController().updateHealth(lastHealth < player.getStats().getStat(Stat.HEALTH));
			lastHealth = player.getStats().getStat(Stat.HEALTH);
			break;

		case ENERGY:
			Element energy = nifty.getScreen("hud").findElementByName("CharEnergy");
			energy.getRenderer(TextRenderer.class).setText("ENERGY  "+player.getStats().getStat(Stat.ENERGY));
			screenManager.getHudScreenController().updateEnergy(lastEnergy < player.getStats().getStat(Stat.ENERGY));
			lastEnergy = player.getStats().getStat(Stat.ENERGY);
			break;

		case MAXHEALTH:
			Element maxHealth = nifty.getScreen("hud").findElementByName("CharMaxHealth");
			maxHealth.getRenderer(TextRenderer.class).setText("MAX HEALTH  "+player.getStats().getStat(Stat.MAXHEALTH));
			break;
			
		case MAXENERGY:
			Element maxEnergy = nifty.getScreen("hud").findElementByName("CharMaxEnergy");
			maxEnergy.getRenderer(TextRenderer.class).setText("MAX ENERGY  "+player.getStats().getStat(Stat.MAXENERGY));
			break;

		default:
		}
	}

	/**Creates the StatEffect objects associated with a given custom orb effect
	 * 
	 * @param player - the player for which the orb effects need to be generated
	 */
	public void setStatEffects(Player player){
		//health
		Element healthOrb = nifty.getScreen("hud").findElementByName("HealthOrb");
		Element healthOrbfade = nifty.getScreen("hud").findElementByName("HealthOrbFade");
		EffectSource healthOrbef = new EffectSource(GuiEffectType.HEALTHORBCHANGE,healthOrb.getNifty(),healthOrb.getElementType(),"HealthOrbef",healthOrb,healthOrb.getFocusHandler(),false,null,null);
		healthOrbef.setActor(player);
		healthOrb.add(healthOrbef);
		healthOrbfade.add(healthOrbef);
		
		//Energy
		Element energyOrb = nifty.getScreen("hud").findElementByName("EnergyOrb");
		Element energyorbfade = nifty.getScreen("hud").findElementByName("EnergyOrbFade");
		EffectSource energyOrbef = new EffectSource(GuiEffectType.ENERGYORBCHANGE,energyOrb.getNifty(),energyOrb.getElementType(),"EnergyOrbef",energyOrb,energyOrb.getFocusHandler(),false,null,null);
		energyOrbef.setActor(player);
		energyOrb.add(energyOrbef);
		energyorbfade.add(energyOrbef);
	}

	@Override
	/**MobObserver method used to udate the GUI according to the targetted mob
	 * 
	 */
	public void update(Mob mob) {
		if (mob == null || mob.getStats().getStat(Stat.HEALTH) == 0){
			Element mobDisplay = screenManager.getHudScreenController().getNifty().getCurrentScreen().findElementByName("MobDisplay");
			if(mobDisplay != null) mobDisplay.hide();
		}
		else{
			setNextTarget(mob);
		}
	}

	private Map<Spatial, Light> highlightLights = new HashMap<Spatial, Light>();

	/**Highlights the mob given a particular target
	 * 
	 * @param nextTarget - the target to highlight
	 */
	public void setNextTarget(Mob nextTarget){

		Element mobDisplay = screenManager.getHudScreenController().getNifty().getCurrentScreen().findElementByName("MobDisplay");
		mobDisplay.show();

		Element mobTab = screenManager.getHudScreenController().getNifty().getCurrentScreen().findElementByName("MobTab");

		if (mobSource == null){
			EffectSource efSource = new EffectSource(GuiEffectType.MOBCHANGE, nifty, mobTab.getElementType(), "MobEffect", mobTab, mobTab.getFocusHandler(), false, null, null);
			efSource.setActor(nextTarget);
			mobTab.add(efSource);
			mobSource = efSource;
		}
		else{
			mobSource.setActor(nextTarget);
		}

		mobTab.startEffect(EffectEventId.onCustom);
		mobTab.setVisible(true);

		for(Map.Entry<Spatial, Light> e : highlightLights.entrySet()) {
			e.getKey().removeLight(e.getValue());

		}
		AmbientLight light = new AmbientLight();
		light.setColor(ColorRGBA.White.mult(3f));
		nextTarget.getMesh().addLight(light);
		highlightLights.put(nextTarget.getMesh(), light);
	}

}
