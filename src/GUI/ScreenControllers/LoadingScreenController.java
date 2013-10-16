package GUI.ScreenControllers;


import GUI.Startable;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 * 
 * Basic controller for Loading screen
 * 
 * @author Craig Forret
 */
public class LoadingScreenController extends AbstractAppState implements ScreenController{

	private Nifty nifty;
	private SimpleApplication app;
	private Startable mainApp;
	/** custom methods */ 

	public LoadingScreenController(String data, Startable mainApp) { 
		this.mainApp = mainApp;
	} 

	/** Nifty GUI ScreenControl methods */ 
	public void bind(Nifty nifty, Screen screen) {
		this.nifty = nifty;
	}

	/**Display hint when loading screen opened **/
	public void onStartScreen() {
		Element mes = nifty.getScreen("loading").findElementByName("hintText");
		mes.getRenderer(TextRenderer.class).setText(getTextHint());
		mes.show();
	}
	public void onEndScreen() {}

	/**Provides buffer in loading and commences the world model loading*/
	public void loadWorld(){
		Element toShow = nifty.getScreen("loading").findElementByName("loadImage2");
		toShow.show();
		mainApp.attachWorldToGame();
	}
	
	/**Switches to the hud screen when load world finished**/
	public void startHud(){
		nifty.gotoScreen("hud");
	}

	/**Helper method for returning start screen hints**/
	private String getTextHint(){
		String hint = "";
		
		int val = (int)(Math.random()*5);
		switch (val){
		case 0: 
			hint = "You can open some doors\nby right clicking them\n(you may need a key)";
			break;
		case 1:
			hint = "Press tab when near enemies\nto target them";
			break;
		case 2:
			hint = "Many items can be activated\nwith the right mouse button";
			break;
		case 3:
			hint = "Press 'k' in multiplayer\nto chat with your friends";
			break;
		case 4:
			hint = "Building a 3d game can be\nstressful.";
			break;
		}
		return hint;
	}

	/** jME3 AppState methods */ 

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		this.app=(SimpleApplication)app;
	}

	@Override
	public void update(float tpf) { 
		/** jME update loop! */ 
	}


}