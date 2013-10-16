package GUI;

import GUI.ScreenControllers.LoadingScreenController;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.screen.ScreenController;

/**
 * Loading screen called upon pressing start
 * on the start screen
 * 
 * @author Craig Forret
 */
public class LoadingScreen {
	private ScreenController screenController;
	private Nifty nifty;
	
	public LoadingScreen (Nifty nifty, LoadingScreenController screenController){
		this.nifty = nifty;
		this.screenController = screenController;
	}
	
	//Generates the screen and binds its components
	public void generateScreen(){
		
		nifty.addScreen("loading", new ScreenBuilder("loading") {{
			controller(screenController);
			layer(new LayerBuilder("background") {{
				childLayoutCenter();
				image(new ImageBuilder() {{
					filename("Interface/LoadingScreen.png");
				}});
			}});
			layer(new LayerBuilder("foreground") {{
				childLayoutVertical();
				panel(new PanelBuilder("hintTextPadding"){{
					height("65%");
					alignCenter();
				}});
				
				//displays hints on the loading screen
				text(new TextBuilder("hintText"){{
					alignCenter();
					height("10%");
					width("10%");
					text("");
					font("Interface/Fonts/ItemText.fnt");
					onShowEffect(new EffectBuilder("fade"){{
						length(2000);
						startDelay(2000);
						onEndEffectCallback("loadWorld()");
					}});
				}});

				//fade with callback to help buffer loading
				image(new ImageBuilder("loadImage2") {{
					visible(false);
					onShowEffect(new EffectBuilder("fade"){{
						length(2000);
						startDelay(2000);
						onEndEffectCallback("startHud()");
					}});
				}});
				
			}});

		}}.build(nifty));
	}
}