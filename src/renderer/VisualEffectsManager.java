package renderer;

import java.util.HashSet;

import world.TimeManager;
import world.audio.AudioTimeEventListener;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;

/**
 * A class to manager all visual effects, including fog, water, light scattering
 * and shadows. It also controls the sun timer
 * @author scott
 *
 */
public class VisualEffectsManager{
    private final static int SECONDS_PER_MINUTE = 20;

    private Sun sun;
    private Node node;
    private ViewPort viewPort;

    private FilterPostProcessor fpp;

    private DirectionalLightShadowFilter shadow;
    private FogFilter fog;
    private WaterFilter water;
    private float waveHeight = -1;
    private float baseWaterHeight;
    private LightScatteringFilter lightScatter;

    private HashSet<AudioTimeEventListener> audioListeners = new HashSet<AudioTimeEventListener>();

    private Spatial daySkyBox;
    private Spatial nightSkyBox;

    private Spatial currentSkyBox;

    /**
     * Constructor, activates all the effects and then relies on the user to deactivate what they don't want
     *
     * @param am
     *  Asset manager required to load up the textures
     * @param node
     *  The node to attach the effects to
     * @param viewPort
     *  the view port to attach the effects to
     */
    public VisualEffectsManager(AssetManager am, Node node, ViewPort viewPort){
        this.node = node;
        this.viewPort = viewPort;

        sun = new Sun(node, Vector3f.UNIT_Y.negate(), ColorRGBA.White.mult(2f));
        fpp = new FilterPostProcessor(am);

        loadSkyBoxes(am);

        initBasicEffects(); //just make sure to set the effects if you want something different

        setShadows(am, 1024, 3);
        addShadows();

        viewPort.addProcessor(fpp);
    }
    
    public void detachFromViewPort(){
        viewPort.removeProcessor(fpp);
        fpp.removeAllFilters();
    }

    /**
     * Loads up the sky boxes into fields, ready to use
     * @param am
     *  The asset manager used to load the textures from
     */
    private void loadSkyBoxes(AssetManager am){
        daySkyBox = SkyFactory.createSky(am,
                am.loadTexture("/Textures/skybox/day_right.jpg"),
                am.loadTexture("/Textures/skybox/day_left.jpg"),
                am.loadTexture("/Textures/skybox/day_back.jpg"),
                am.loadTexture("/Textures/skybox/day_front.jpg"),
                am.loadTexture("/Textures/skybox/day_top.jpg"),
                am.loadTexture("/Textures/skybox/day_top.jpg"));

        nightSkyBox = SkyFactory.createSky(am,
                am.loadTexture("/Textures/skybox/night_right.jpg"),
                am.loadTexture("/Textures/skybox/night_left.jpg"),
                am.loadTexture("/Textures/skybox/night_back.jpg"),
                am.loadTexture("/Textures/skybox/night_front.jpg"),
                am.loadTexture("/Textures/skybox/night_top.jpg"),
                am.loadTexture("/Textures/skybox/night_bottom.jpg"));

        daySkyBox.setShadowMode(ShadowMode.Off);
        nightSkyBox.setShadowMode(ShadowMode.Off);
    }

    /**
     * Initializes the effects to a default value that I picked out of my ass
     */
    public void initBasicEffects(){
        setWater(-73f, null, new ColorRGBA(0.0f, 0.2f, 1.0f, 0.8f).mult(0.1f), true, 10);
        setFog(80f, 1.0f, ColorRGBA.Gray);
        setLightScattering();

        addWater();
        addFog();
        addLightScattering();
    }

    public Sun getSun(){
        return sun;
    }

    /**
     * To be called every tick, updates all the effects, i.e. moves the sun, moves the light scattering with the sun,
     * ans changes the sky box if necessary
     * @param time
     *  The new time, from the update manager
     */
    public void updateEffects(float time){
        if (TimeManager.shouldPlayNightMusic(time)){
            for(AudioTimeEventListener atl : audioListeners){
        		atl.update();
        	}
        }

        if(time > TimeManager.DAY_LENGTH && currentSkyBox != nightSkyBox || currentSkyBox == null){   //takes care of switching the sky boxes when necessay
            currentSkyBox = nightSkyBox;
            node.detachChild(daySkyBox);
            node.attachChild(nightSkyBox);
        }else if(time > 0 && time <= TimeManager.DAY_LENGTH && currentSkyBox != daySkyBox){
            currentSkyBox = daySkyBox;
            node.detachChild(nightSkyBox);
            node.attachChild(daySkyBox);
        }

        if(waveHeight > 0){  //if there are waves, move the water up and down in a cosine pattern
            water.setWaterHeight((float)(waveHeight*Math.cos(time*0.7)+baseWaterHeight));
        }

        if(time > TimeManager.DAY_LENGTH && lightScatter.isEnabled()){  //take care of moving the light scattering effect and turning it off at night, otherwise bad bad
            lightScatter.setEnabled(false);
        }else if(time > 0 && time < TimeManager.DAY_LENGTH){
            if(!lightScatter.isEnabled()){
                lightScatter.setEnabled(true);
            }
            lightScatter.setLightPosition(new Vector3f(sun.getSunX(), sun.getSunY(), 0).mult(3000f));
        }

        sun.updateSun(time);
    }

    /**
     * Creates new shadows off a given shadowmap size and the number of splits to calculate
     * @param am
     *  the assetmanager to needed to load something
     * @param shadowMapSize
     *  @require is a power of 2
     *  The size of the shadow map
     * @param nbSplits
     *  The number of splits to take of the shadow map
     */
    public void setShadows(AssetManager am, int shadowMapSize, int nbSplits){
        shadow = new DirectionalLightShadowFilter(am, shadowMapSize, nbSplits);
        shadow.setLight(sun.getLight());
    }

    /**
     * adds shadows to the view
     */
    public void addShadows(){
        shadow.setEnabled(true);
        fpp.addFilter(shadow);
    }

    /**
     * removes the shadows from the view
     */
    public void removeShadows(){
        if(!fpp.getFilterList().contains(shadow)){
            return;
        }
        shadow.setEnabled(false);
        fpp.removeFilter(shadow);
    }

    /**
     * Creates a new fog setting
     * @param fogDistance
     *  The distance of the fog
     * @param fogDensity
     *  The density of the fog
     * @param fogColor
     *  The color of the fog
     */
    public void setFog(float fogDistance, float fogDensity, ColorRGBA fogColor){
        fog = new FogFilter();
        fog.setFogDistance(fogDistance);
        fog.setFogDensity(fogDensity);
        fog.setFogColor(fogColor);
    }

    /**
     * Adds the fog to the view
     */
    public void addFog(){
        if(fpp.getFilterList().contains(fog)){
            return;
        }
        fog.setEnabled(true);
        fpp.addFilter(fog);
    }

    /**
     * removes the fog from the view
     */
    public void removeFog(){
        if(!fpp.getFilterList().contains(fog)){
            return;
        }
        fog.setEnabled(false);
        fpp.removeFilter(fog);
    }

    /**
     * Creates a new water to add to the view
     * @param height
     *  The height of the water
     * @param color
     *  The color of the water
     * @param deepColor
     *  The color of the deep water, i.e. that which is really deep
     * @param waves
     *  True if you want there to be a tide
     * @param waveHeight
     *  The height of the tide, given that it is being used
     */
    public void setWater(float height, ColorRGBA color, ColorRGBA deepColor, boolean waves, float waveHeight){
        water = new WaterFilter();
        water.setWaterHeight(height);
        baseWaterHeight = height;

        if(color != null){
            water.setWaterColor(color);
        }
        if(deepColor != null){
            water.setDeepWaterColor(deepColor);
        }

        if(waves){
            this.waveHeight = waveHeight;
        } else{
            waveHeight = -1;
        }
    }

    /**
     * adds the water to the given view
     */
    public void addWater(){
        if(fpp.getFilterList().contains(water)){
            return;
        }
        water.setEnabled(true);
        fpp.addFilter(water);
    }

    /**
     * Removes the water from the view
     */
    public void removeWater(){
        if(!fpp.getFilterList().contains(water)){
            return;
        }
        water.setEnabled(false);
        fpp.removeFilter(water);
    }

    /**
     * Creates a new light scattering object, doesn't let you set anything about it though
     */
    public void setLightScattering(){
        lightScatter = new LightScatteringFilter();
        lightScatter.setLightPosition(new Vector3f(sun.getSunX(), sun.getSunY(), 0).mult(3000f));
        lightScatter.setEnabled(true);
        lightScatter.setBlurStart(0.06f);
        lightScatter.setBlurWidth(0.5f);
        lightScatter.setLightDensity(1.1f);
    }

    /**
     * adds the light scattering to the scene
     */
    public void addLightScattering(){
        if(fpp.getFilterList().contains(lightScatter)){
            return;
        }
        lightScatter.setLightPosition(new Vector3f(sun.getSunX(), sun.getSunY(), 0).mult(3000f));
        lightScatter.setEnabled(true);
        fpp.addFilter(lightScatter);
    }

    /**
     * remvoes the light scattering from the scene
     */
    public void removeLightScattering(){
        if(!fpp.getFilterList().contains(lightScatter)){
            return;
        }
        lightScatter.setEnabled(false);
        fpp.removeFilter(lightScatter);
    }

    /**
     * adds an audio listener to the visual effects manager for when the time ticks over to nihgt
     * @param atl
     *      The audo listener to add to the scene
     */
    public void addAudioListener(AudioTimeEventListener atl){
    	audioListeners.add(atl);
    }

}
