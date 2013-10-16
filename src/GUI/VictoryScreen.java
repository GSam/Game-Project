package GUI;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;

/**
 * Final Victory Screen
 * 
 * @author Craig Forret
 */
public class VictoryScreen {
	private Nifty nifty;
	
	public VictoryScreen (Nifty nifty){
		this.nifty = nifty;
	}
	
	//Generates the screen and binds its components
	public void generateScreen(){
		
		nifty.addScreen("victory", new ScreenBuilder("victory") {{
			layer(new LayerBuilder("background") {{
				childLayoutCenter();
				image(new ImageBuilder() {{
					width("100%");
					height("100%");
					filename("Interface/victory.jpg");
				}});
			}});

		}}.build(nifty));
	}
}