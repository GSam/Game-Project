package GUI;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.HoverEffectBuilder;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.screen.ScreenController;

/**
 * @author Craig
 * 
 * Initial screen with start button prompting loading screen
 * and world generation
 */
public class StartScreen {
	private ScreenController screenController;
	private Nifty nifty;
	
	public StartScreen (Nifty nifty,  ScreenController screenController){
		this.nifty = nifty;
		this.screenController = screenController;
	}
	
	/**Generates the StartScreen and binds elements**/
	
	public void generateScreen(){
		
		nifty.addScreen("start", new ScreenBuilder("start") {{
			controller(screenController);
			layer(new LayerBuilder("background") {{
				childLayoutCenter();
				image(new ImageBuilder() {{
					filename("Interface/outPostStartScreen.png");
				}});

			}});

			//Sets up spacing elements, image, and image button to commence game
			layer(new LayerBuilder("foreground") {{
				childLayoutVertical();
				width("100%");
				height("100%");
				panel(new PanelBuilder(){{
					height("80%");
					alignCenter();
				}});
				image(new ImageBuilder("StartButton"){{
					height("10%");
					width("15%");
					filename("Interface/StartButton.png");
					alignCenter();
					visibleToMouse(true);
					interactOnClick("startGame(loading)");
					onHoverEffect(new HoverEffectBuilder("changeImage"){{
						effectParameter("active", "Interface/StartButtonGlow.png");
						effectParameter("inactive", "Interface/StartButton.png");
					}});
				}});
			}});
		}}.build(nifty));
	}
}