package GUI;

import java.util.ArrayList;

import GUI.Factories.BaseComponentFactory;
import GUI.ScreenControllers.HudScreenController;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.Droppable;
import de.lessvoid.nifty.controls.dragndrop.builder.DroppableBuilder;
import de.lessvoid.nifty.controls.listbox.builder.ListBoxBuilder;
import de.lessvoid.nifty.controls.textfield.builder.TextFieldBuilder;

/**Sets up the main Head's up display screen
 * @author Craig Forret
 */
public class HeadsUpScreen {
	
	final HudScreenController screenController;
	private double screenHeight;
	private double screenWidth;
	private Nifty nifty;
	private ArrayList<String> dropSlots = new ArrayList<String>();
	private CharacterEquipPanel cequip;
	private InventoryPanel inPanel;
	private WorldChestPanel worldChest;
	private ContainerPanel container;
	
	public HeadsUpScreen (Nifty nifty, final HudScreenController screenController){
		this.screenHeight = nifty.getRenderEngine().getHeight();
		this.screenWidth = nifty.getRenderEngine().getWidth();
		this.nifty = nifty;
		this.screenController = screenController;
		this.cequip = new CharacterEquipPanel(dropSlots, nifty);
		this.inPanel = new InventoryPanel(dropSlots, nifty);
		this.worldChest = new WorldChestPanel(dropSlots,nifty);
		this.container = new ContainerPanel(dropSlots,nifty);
	}
	
	/** Builds the Head's up display screen. Separated from constructor to enable
	 * screen generation to take place at a specific point in game loading.
	 * 
	 */
	public void generateScreen(){
		nifty.addScreen("hud", new ScreenBuilder("hud") {{
			controller(screenController);

			//Set up the background base panel
			layer(new LayerBuilder("Bae panel") {{
				childLayoutHorizontal();
				panel(new PanelBuilder("stop-left") {{
					valignBottom();
					width("100%");
					height("20%");
					childLayoutVertical();
					image(new ImageBuilder() {{
						valignBottom();
						width("100%");
						height("101%");
						filename("Interface/Base.v8.png");
						childLayoutAbsolute();
					}});
				}});
			}});
			
			//Sets up the layer containing the health and 
			//energy orbs, along with the droppable slots for items
			layer(new LayerBuilder("backgroundElements") {{
				childLayoutAbsolute();
				
				
				image(new ImageBuilder("chatButton"){{
					x(""+(int)(screenWidth*0.250));
					y(""+(int)(screenHeight*0.93));
					width(""+(int)(screenWidth*0.030));
					height(""+ (int)(screenHeight*0.055));
					visibleToMouse(true);
					filename("Interface/"+ "chatbutton.png");
				}});
				
				//sets up the 'health' orb and associated effects
				image (new ImageBuilder("HealthOrb"){{
					x(""+(int)(screenWidth*0.028));
					y(""+(int)(screenHeight*0.81));
					width(""+(int)(screenWidth*0.097));
					height(""+(int)(screenHeight*0.154));
					onCustomEffect(new EffectBuilder("orbChanger"){{
						effectParameter("start","#ff");
                    	effectParameter("end", "#00");
                    	length(2000);
					}});
				
				
				}});

				//separate image for for the fade effect 
				//that takes place when the player takes damage
				image (new ImageBuilder("HealthOrbFade"){{
					x(""+(int)(screenWidth*0.028));
					y(""+(int)(screenHeight*0.81));
					width(""+(int)(screenWidth*0.097));
					height(""+(int)(screenHeight*0.154));
					onCustomEffect(new EffectBuilder("orbFadeChange"){{
						customKey("OrbFade");
						effectParameter("start","#ff");
                    	effectParameter("end", "#00");
                    	length(2000);
					}});
					onCustomEffect(new EffectBuilder("orbChanger"){{
						customKey("OrbChange");
					}});
				}});
				
				
				//sets up the 'Energy' orb and associated effects
				image (new ImageBuilder("EnergyOrb"){{
					x(""+(int)(screenWidth*0.868));
					y(""+(int)(screenHeight*0.821));
					width(""+(int)(screenWidth*0.0968));
					height(""+(int)(screenHeight*0.147));
					onCustomEffect(new EffectBuilder("orbChanger"){{
						effectParameter("start","#ff");
                    	effectParameter("end", "#00");
                    	length(2000);
					}});
				}});

				//separate image for for the fade effect 
				//that takes place when the player takes damage
				image (new ImageBuilder("EnergyOrbFade"){{
					x(""+(int)(screenWidth*0.868));
					y(""+(int)(screenHeight*0.821));
					width(""+(int)(screenWidth*0.0968));
					height(""+(int)(screenHeight*0.147));
					onCustomEffect(new EffectBuilder("orbFadeChange"){{
						customKey("OrbFade");
						effectParameter("start","#ff");
                    	effectParameter("end", "#00");
                    	length(2000);
					}});
					onCustomEffect(new EffectBuilder("orbChanger"){{
						customKey("OrbChange");
					}});
				}});

				//sets up the droppable slots on the base panel. As
				//these items are not at fixed intervals and heights, their relative positions 
				//are hard coded here.
				int itemCount = 1;
				control(createDropSlot("InventBase"+itemCount++, (int)(screenWidth*0.145),(int)(screenHeight*0.91),
						(int)(screenWidth*0.031+2), (int)(screenHeight*0.07)));
				control(createDropSlot("InventBase"+itemCount++, (int)(screenWidth*0.3066),(int)(screenHeight*0.91),
						(int)(screenWidth*0.031+2), (int)(screenHeight*0.07)));
				control(createDropSlot("InventBase"+itemCount++, (int)(screenWidth*0.367),(int)(screenHeight*0.91),
						(int)(screenWidth*0.031+2), (int)(screenHeight*0.07)));
				control(createDropSlot("InventBase"+itemCount++, (int)(screenWidth*0.428),(int)(screenHeight*0.91),
						(int)(screenWidth*0.031+2), (int)(screenHeight*0.07)));
				control(createDropSlot("InventBase"+itemCount++, (int)(screenWidth*0.489),(int)(screenHeight*0.91),
						(int)(screenWidth*0.031+2), (int)(screenHeight*0.07)));
				control(createDropSlot("InventBase"+itemCount++, (int)(screenWidth*0.548),(int)(screenHeight*0.91),
						(int)(screenWidth*0.031+2), (int)(screenHeight*0.07)));
				control(createDropSlot("InventBase"+itemCount++, (int)(screenWidth*0.608),(int)(screenHeight*0.91),
						(int)(screenWidth*0.031+2), (int)(screenHeight*0.07)));
				control(createDropSlot("InventBase"+itemCount++, (int)(screenWidth*0.658),(int)(screenHeight*0.91),
						(int)(screenWidth*0.031+2), (int)(screenHeight*0.07)));
				
				
	
				//Sets up the drop area for items 
				panel(new PanelBuilder("AreasForDrop"){{
					childLayoutHorizontal();
					width("100%");
					height("100%");
					//equip drop area is the area beneath the char equip panel.
					//if a player accidentally misses a drop slot they will
					//not drop their item
					control(new DroppableBuilder("EqDropArea"){{ 
						height("80%");
						width("40%");
					}});
					//the chief drop area (main screen)
					control(new DroppableBuilder("DropArea"){{ 
						height("80%");
						width("60%");
					}});
					
				}});
				dropSlots.add("EqDropArea"); //drop slots added to establish DroppableDropFilter
				dropSlots.add("DropArea");	
				
				//adds the "
				panel(new PanelBuilder("CharBut") {{
					x(""+(int)(screenWidth*0.705));
					y(""+(int)(screenHeight*0.906));
					childLayoutVertical();
					visibleToMouse(true);
					image(new ImageBuilder() {{
						width(""+((int)(screenWidth*0.062+4)));
						height(""+((int)(screenHeight*0.090)));
						childLayoutVertical();
						filename("Interface/CharButton.png");
	
					}});
				}});
				
				//The Inventory Button
				panel(new PanelBuilder("InventoryBut") {{
					x(""+(int)(screenWidth*0.78));
					y(""+(int)(screenHeight*0.9));
					childLayoutVertical();
					visibleToMouse(true);
					image(new ImageBuilder() {{
						width(""+((int)(screenWidth*0.062+4)));
						height(""+((int)(screenHeight*0.095)));
						childLayoutVertical();
						filename("Interface/Inventory.png");
					}});
				}});
				

				//Panel for the chat
				panel(new PanelBuilder("chatPanel"){{
					x(""+(int)(screenWidth*0.25));
					y(""+(int)(screenHeight*0.65));
					childLayoutVertical();
					visible(false);
					onCustomEffect(new EffectBuilder("move"){{
						length(200);
						inherit();
						effectParameter("mode", "in");
						effectParameter("direction", "bottom");
					}});
					//list box containing chat Strings
					control(new ListBoxBuilder("listBoxChat") {{
						style("Interface/Styles/newListBox.xml");
						displayItems(5);
						width(""+(int)(screenWidth*0.45));
						height(""+(int)(screenHeight*0.2));
						selectionModeDisabled();
						hideHorizontalScrollbar();
						backgroundColor("#145a"); 
					}});
					
					//input for chat
					control(new TextFieldBuilder("chatText",""){{
						width(""+(int)(screenWidth*0.45));
						height(""+(int)(screenHeight*0.04));
						backgroundColor("#145a"); 
					}});
				}});
				
				//Generate the key inventory and equipment panels
				panel(cequip.makePanel());
				panel(worldChest.makeWorldChest());
				panel(container.makeContainer());
				panel(inPanel.makeInventory());
				
				//set up the 'MobDisplay' panel for targetting mobs
				panel(new PanelBuilder("MobDisplay") {{
					childLayoutAbsolute();
					//create the outer cover image
					image (new ImageBuilder("MobTabCover"){{
						x(""+(int)(screenWidth*0.05));
						y(""+(int)(screenHeight*0.04));
						width(""+((int)(screenWidth*0.22)));
						height(""+((int)(screenHeight*0.12)));
						filename("Interface/MobTabCover.png");
					}});
					//the image of the actual mob (generated dynamically)
					image(new ImageBuilder("MobTab") {{
						x(""+(int)(screenWidth*0.06));
						y(""+(int)(screenHeight*0.06));
						width(""+((int)(screenWidth*0.15)));
						height(""+((int)(screenHeight*0.064)));
						childLayoutVertical();
						filename("Interface/TestMob.png");//placeholder
						onCustomEffect(new EffectBuilder("mobDisplay"){{
							length(1);
							neverStopRendering(true);
						}});
					}});
					//text for the mob's name (generated dynamically)
					text(new TextBuilder("MobText") {{
						x(""+(int)(screenWidth*0.095));
						y(""+(int)(screenHeight*0.136));
						font("Interface/Fonts/MobText.fnt");
					}});
					visible(false);
				}});
			}});
			
			//Sets up foreground panel 
			layer(new LayerBuilder("foreground") {{
				childLayoutHorizontal();
				//Cross hairs for the first person view.
		

				panel(new PanelBuilder("CrossHairPanel") {{
					childLayoutAbsolute();
					height("100%");
					width("100%");
					image (new ImageBuilder("CrossHairs"){{
						x(""+(int)(screenWidth*0.475));
						y(""+(int)(screenHeight*0.475));
						width(""+((int)(screenWidth*0.05)));
						height(""+((int)(screenWidth*0.05)));
						filename("Interface/CrossHairs.png");
						visible(false);
					}});
				}});
			}});
			
			
			layer(new LayerBuilder("messaging") {{
				// panel for displaying messages on the hud screen
				childLayoutHorizontal();
				panel(new PanelBuilder("message_panel") {{
					childLayoutAbsolute();
					height("100%");
					width("50%");
					text(new TextBuilder("MessagePanel"){{
						x(""+(int)(screenWidth*0.5));
						y(""+(int)(screenHeight*0.7));
						valignCenter();
						font("Interface/Fonts/CharEquip.fnt");
						text("");
						textHAlignCenter();
						//fade erase effect on the hud screen message text
						onCustomEffect(new EffectBuilder("fadeErase"){{
                        	effectParameter("start","#ff");
                        	effectParameter("end", "#00");
                        	length(2000);
                        }});
					}});
					width("80%");
				}});
			}});
		}}.build(nifty));

		configureDroppables();
		screenController.initialisePositions(dropSlots);
		
	}
	
	/**Adds the drop filters to the droppable slot elements stored in the dropSlots*/
	private void configureDroppables(){
		for (String slot : dropSlots){
			Droppable drop = nifty.getScreen("hud").findNiftyControl(slot, Droppable.class);
			drop.addFilter(screenController);
		}
	}

	/** Helper method to create the Droppable slots for a given image **/
	
	private DroppableBuilder createDropSlot(String posName, int x, int y, int wid, int height){
		DroppableBuilder drop = new DroppableBuilder(posName);
		BaseComponentFactory.itemSlot(drop, posName, "Interface/blank.png", "Interface/blankGlow.png",
				""+x, ""+y, ""+wid, ""+height);
		dropSlots.add(posName);
		return drop;
	}
	

}
