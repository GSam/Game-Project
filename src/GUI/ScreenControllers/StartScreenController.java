package GUI.ScreenControllers;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
 
/**
 * Basic controller for StartScreen. Progresses to loading screen when
 * start button is pressed
 * 
 * @author Craig Forret
 */
public class StartScreenController extends AbstractAppState implements ScreenController{
 
  private Nifty nifty;
  private SimpleApplication app;
  /** custom methods */ 
 
  public StartScreenController(String data) {} 
  
  /** Nifty GUI ScreenControl methods */ 
  public void bind(Nifty nifty, Screen screen) {
    this.nifty = nifty;
  }
  
  /** Progresses to loading screen when called by screen button */ 
  public void startGame(String nextScreen) {
    nifty.gotoScreen(nextScreen); 
  }
  
  public void onStartScreen() { }
 
  public void onEndScreen() { }
 
  /** jME3 AppState methods */ 
  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    super.initialize(stateManager, app);
    this.app=(SimpleApplication)app;
  }
 
  @Override
  public void update(float tpf) {  }
 

}