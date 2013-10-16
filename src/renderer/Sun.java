package renderer;

import world.TimeManager;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * A class to represent the sun and control how it moves through the sky,
 * also controls the ambient light for the scene
 * @author scott
 *
 */
public class Sun{
    private DirectionalLight light;
    private Node node;
    private boolean attached;

    private float sunx;
    private float suny;
    private float sunAngle;
    private AmbientLight ambient;

    public Sun(Node node, Vector3f direction, ColorRGBA color){
        light = new DirectionalLight();
        light.setDirection(direction);
        light.setColor(color);
        light.setName("sun");
        this.node = node;

        node.addLight(light);
        ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.1f));
        ambient.setName("ambient");
        node.addLight(ambient);
        attached = true;
    }

    /**
     * Updates the sun given the current time in the game
     * @param time
     *  The time in the game
     */
    public void updateSun(float time){
        if(time >= 0 && time <= TimeManager.DAY_LENGTH){   //the day time
            if(!attached){
                attached = true;
                node.addLight(light);
            }

            calcPosition(time);

            light.setDirection(light.getDirection().add(-sunx, -suny, 0));

        }else if(time > TimeManager.DAY_LENGTH && time <= TimeManager.TOTAL_LENGTH){   //for when it is night time
            if(attached){
                attached = false;
                node.removeLight(light);
            }
        }else{
            throw new IllegalArgumentException("This is an invalid time");
        }
    }

    /**
     * Calculates it's position in the sky based on the time of day
     * @param time
     *  The time of day
     */
    private void calcPosition(float time){
        sunAngle =time*(180/TimeManager.DAY_LENGTH);

        sunx = (float) Math.cos(FastMath.DEG_TO_RAD*sunAngle);
        suny = (float) Math.sin(FastMath.DEG_TO_RAD*sunAngle);
    }

    public float getSunX(){
        return sunx;
    }

    public float getSunY(){
        return suny;
    }

    public float getSunAngle(){
        return sunAngle;
    }

    public DirectionalLight getLight(){
        return light;
    }

    public boolean getAttached(){
        return attached;
    }

    public boolean isDay () {
    	return attached;
    }
    
    public void setAmbient(AmbientLight ambient){
    	this.ambient = ambient;
    }
}
