package net.rugby.foundation.admin.client.ui.teammatchstatspopup;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.ClientFactory.GetPositionListCallback;
import net.rugby.foundation.admin.client.ui.FieldDefinition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.Position.position;

public class TeamMatchStatsPopupViewFieldDefinitions<T> {
	private static List<FieldDefinition<ITeamMatchStats>> fieldDefinitions =
			new ArrayList<FieldDefinition<ITeamMatchStats>>();

//	private Long 	id;                          
//	private Long 	matchId;                     
//	private Long 	teamId;                      
//	                                             
//	private Integer tries;                       
//	                                             
//	private Integer conversionsAttempted;        
//	private Integer conversionsMade;             
//	                                             
//	private Integer penaltiesAttempted;          
//	private Integer penaltiesMade;               
//	                                             
//	private Integer dropGoalsAttempted;          
//	private Integer dropGoalsMade;               
//	                                             
//	private Integer kicksFromHand;               
//	private Integer passes;                      
//	private Integer runs;                        
//	private Integer metersRun;                   
//	                                             
//	private Float   possession;                   
//	private Float   territory;                   
//	                                             
//	private Integer cleanBreaks;                 
//	private Integer defendersBeaten;             
//	private Integer offloads;                    
//	                                             
//	private Integer rucks;                       
//	private Integer rucksWon;                    
//	                                             
//	private Integer mauls;                       
//	private Integer maulsWon;                    
//	                                             
//	private Integer turnoversConceded;           
//	                                             
//	private Integer tacklesMade;                 
//	private Integer tacklesMissed;               
//	                                             
//	private Integer scrumsPutIn;                 
//	private Integer scrumsWonOnOwnPut;           
//	                                             
//	private Integer lineoutsThrownIn;            
//	private Integer lineoutsWonOnOwnThrow;       
//	                                             
//	private Integer penaltiesConceded;           
//	private Integer yellowCards;                 
//	private Integer redCards;                    

	public TeamMatchStatsPopupViewFieldDefinitions(final ClientFactory clientFactory) {
		if (fieldDefinitions.isEmpty()) {





			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//tries

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getTries().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setTries(l);
					}
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//conAttempts

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getConversionsAttempted().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setConversionsAttempted(l);
					}
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//cons made

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getConversionsMade().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setConversionsMade(l);
					}
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//penAttempts

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getPenaltiesAttempted().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setPenaltiesAttempted(l);
					}
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//pens made

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getPenaltiesMade().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setPenaltiesMade(l);
					}
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//DGs Attempts

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getDropGoalsAttempted().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setDropGoalsAttempted(l);
					}
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//DGs made

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getDropGoalsMade().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setDropGoalsMade(l);
					}
					return p;
				}

			});
			
			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//runs

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getRuns().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setRuns(l);
					}
					return p;
				}

			});
			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//passes

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getPasses().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setPasses(l);
					}
					return p;
				}

			});
			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//kicks

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getKicksFromHand().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setKicksFromHand(l);
					}
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//metersRun

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getMetersRun().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setMetersRun(l);
					}
					return p;
				}

			});    		
			
			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//possession

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getPossesion().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setPossesion(l);
					}
					return p;
				}

			});    		
			
			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//territory

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getTerritory().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setTerritory(l);
					}
					return p;
				}

			}); 
			
			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//CleanBreaks

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getCleanBreaks().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setCleanBreaks(l);
					}
					return p;
				}

			});
			
			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//DefendersBeaten

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getDefendersBeaten().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setDefendersBeaten(l);
					}
					return p;
				}

			});    		

			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//OffLoads

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getOffloads().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setOffloads(l);
					}
					return p;
				}

			});
			
			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//Rucks

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getRucks().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setRucks(l);
					}
					return p;
				}

			});
			
			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//Rucks won

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getRucksWon().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setRucksWon(l);
					}
					return p;
				}

			});
	
			
			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//Mauls

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getMauls().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setMauls(l);
					}
					return p;
				}

			});
			
			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//Mauls won

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getMaulsWon().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setMaulsWon(l);
					}
					return p;
				}

			});
			
			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//Turnovers

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getTurnoversConceded().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setTurnoversConceded(l);
					}
					return p;
				}

			});
			
			
			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//TacklesMade

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getTacklesMade().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setTacklesMade(l);
					}
					return p;
				}

			});
			
			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//TacklesMissed

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getTacklesMissed().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setTacklesMissed(l);
					}
					return p;
				}

			});


			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//Scrums put in

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getScrumsPutIn().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setScrumsPutIn(l);
					}
					return p;
				}

			});
	
			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//Scrums won on put

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getScrumsWonOnOwnPut().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setScrumsWonOnOwnPut(l);
					}
					return p;
				}

			});
			

			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//Lineouts thrown in

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getLineoutsThrownIn().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setLineoutsThrownIn(l);
					}
					return p;
				}

			});
	
			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//Lineouts won on throw

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getLineoutsWonOnOwnThrow().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setLineoutsWonOnOwnThrow(l);
					}
					return p;
				}

			});
			
		

			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//PenaltiesConceded

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getPenaltiesConceded().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setPenaltiesConceded(l);
					}
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//YellowCards

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getYellowCards().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setYellowCards(l);
					}
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//RedCards

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getRedCards().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setRedCards(l);
					}
					return p;
				}

			});

	
			
			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
					//scrumLink
			        private Anchor w;
			        
			        @Override
			        public void bind (Widget in)
			        {
			        	w = (Anchor) in;
			        }
			        
			        @Override
					public Widget render(final ITeamMatchStats c) {
						if (c != null && c.getMatchId() != null) {

							clientFactory.getMatchGroupAsync(c.getMatchId(), new ClientFactory.GetMatchGroupCallback() {
				
								@Override
								public void onMatchGroupFetched(IMatchGroup match) {
									w.setText("Link to match report on scrum.com");
						        	w.setHref(match.getForeignUrl());
						        	w.setTarget("_blank");
								}
							});
						}
			        	
			        	return w;
			        }
			
					@Override
					public void clear() {
						w.setText(null);
					}
					
					@Override
					public ITeamMatchStats update(ITeamMatchStats p) {
						// can't edit id
						return p;
					}
					
			      });
			
			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//id

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof Label);
					if (c != null && c.getId() != null) {
						((Label)w).setText(c.getId().toString());
					}

					return w;
				}

				@Override
				public void clear() {
					((Label)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					// can't edit id
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//Team and id

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof Anchor);
					if (c != null && c.getTeamId() != null) {
						((Anchor)w).setText(c.getTeamId().toString());
					}
					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof Anchor);
					((Anchor)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					// can't edit id
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ITeamMatchStats>() {
				//match and id

				@Override
				public Widget render(ITeamMatchStats c) {
					assert (w instanceof Label);
					if (c != null && c.getMatchId() != null) {
						((Label)w).setText(c.getMatchId().toString());
					}
					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof Label);
					((Label)w).setText(null);
				}

				@Override
				public ITeamMatchStats update(ITeamMatchStats p) {
					// can't edit id
					return p;
				}

			});
		}
	}

	public List<FieldDefinition<ITeamMatchStats>> getFieldDefinitions() {
		return fieldDefinitions;
	}

}
