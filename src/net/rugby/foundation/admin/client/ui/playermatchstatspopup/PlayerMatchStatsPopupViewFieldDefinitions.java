package net.rugby.foundation.admin.client.ui.playermatchstatspopup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.ClientFactory.GetCountryListCallback;
import net.rugby.foundation.admin.client.ClientFactory.GetPositionListCallback;
import net.rugby.foundation.admin.client.ui.FieldDefinition;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.Position.position;

public class PlayerMatchStatsPopupViewFieldDefinitions<T> {
	private static List<FieldDefinition<IPlayerMatchStats>> fieldDefinitions =
			new ArrayList<FieldDefinition<IPlayerMatchStats>>();

	//	@UiField Label   nameAndId;
	//	@UiField Label   playerAndId;
	//	@UiField Label   matchAndId;
	//	@UiField Label   slot;
	//	@UiField ListBox position;
	//	@UiField TextBox tries;
	//	@UiField TextBox tryAssists;
	//	@UiField TextBox points;
	//	@UiField TextBox runs;
	//	@UiField TextBox passes;
	//	@UiField TextBox kicks;
	//	@UiField TextBox metersRun;
	//	@UiField TextBox cleanBreaks;
	//	@UiField TextBox defendersBeaten;
	//	@UiField TextBox offloads;
	//	@UiField TextBox turnovers;
	//	@UiField TextBox tacklesMade;
	//	@UiField TextBox tacklesMissed;
	//	@UiField TextBox lineoutsWonOnThrow;
	//	@UiField TextBox lineoutsStolenOnOppThrow;
	//	@UiField TextBox penaltiesConceded;
	//	@UiField TextBox yellowCards;
	//	@UiField TextBox redCards;
	//	@UiField Anchor scrumLink;

	public PlayerMatchStatsPopupViewFieldDefinitions(final ClientFactory clientFactory) {
		if (fieldDefinitions.isEmpty()) {

			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//id

				@Override
				public Widget render(IPlayerMatchStats c) {
					assert (w instanceof Label);
					((Label)w).setText(c.getName() + "/" + c.getId().toString());

					return w;
				}

				@Override
				public void clear() {
					((Label)w).setText(null);
				}

				@Override
				public IPlayerMatchStats update(IPlayerMatchStats p) {
					// can't edit id
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//player and id

				@Override
				public Widget render(IPlayerMatchStats c) {
					assert (w instanceof Label);
					((Label)w).setText(c.getPlayerId().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof Label);
					((Label)w).setText(null);
				}

				@Override
				public IPlayerMatchStats update(IPlayerMatchStats p) {
					// can't edit id
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//match and id

				@Override
				public Widget render(IPlayerMatchStats c) {
					assert (w instanceof Label);
					((Label)w).setText(c.getMatchId().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof Label);
					((Label)w).setText(null);
				}

				@Override
				public IPlayerMatchStats update(IPlayerMatchStats p) {
					// can't edit id
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//slot

				@Override
				public Widget render(IPlayerMatchStats c) {
					assert (w instanceof Label);
					((Label)w).setText(c.getSlot().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof Label);
					((Label)w).setText(null);
				}

				@Override
				public IPlayerMatchStats update(IPlayerMatchStats p) {
					// can't edit id
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//position
				private ListBox w;
				// need this bit of gorminess as the first player is passed in before the country list is completely loaded.
				private Integer selectedId = -1;
				private List<position> list = null;

				@Override
				public void bind (Widget in)
				{
					w = (ListBox) in;
					clientFactory.getPositionListAsync(new GetPositionListCallback() {

						@Override
						public void onPositionListFetched(List<position> positions) {
							list = positions;
							int count = 0;
							for (position c : positions) {
								w.addItem(c.getName(), Integer.valueOf(c.ordinal()).toString());
								// check to see if the one we are adding is the one we want selected (as set in render())
								if (selectedId.equals(Integer.valueOf(c.ordinal()))) {
									w.setItemSelected(count, true);
								}
								count++;
							}
						}

					});
				}

				@Override
				public Widget render(IPlayerMatchStats c) {
					if (w.getItemCount() > 0) {
						int i = 0;
						String index = w.getItemText(i);
						if (c.getPosition() != null) {
							while (!c.getPosition().getName().equals(index) && i < w.getItemCount()) {
								++i;
								index = w.getItemText(i);
							}

							w.setItemSelected(i, true);
						}
					} else {
						selectedId = c.getPosition().ordinal();
					}

					return w;
				}

				@Override
				public void clear() {
					//w.setItemSelected(0, false);
				}

				@Override
				public IPlayerMatchStats update(IPlayerMatchStats p) {
					int val = w.getSelectedIndex();

					if (val != -1) {
						p.setPosition(list.get(Integer.valueOf(w.getValue(val))));
					}

					return p;
				}

			});


			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//tries

				@Override
				public Widget render(IPlayerMatchStats c) {
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
				public IPlayerMatchStats update(IPlayerMatchStats p) {
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

			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//tries

				@Override
				public Widget render(IPlayerMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getTryAssists().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public IPlayerMatchStats update(IPlayerMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setTryAssists(l);
					}
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//points

				@Override
				public Widget render(IPlayerMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getPoints().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public IPlayerMatchStats update(IPlayerMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setPoints(l);
					}
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//runs

				@Override
				public Widget render(IPlayerMatchStats c) {
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
				public IPlayerMatchStats update(IPlayerMatchStats p) {
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
			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//passes

				@Override
				public Widget render(IPlayerMatchStats c) {
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
				public IPlayerMatchStats update(IPlayerMatchStats p) {
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
			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//kicks

				@Override
				public Widget render(IPlayerMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getKicks().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public IPlayerMatchStats update(IPlayerMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setKicks(l);
					}
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//metersRun

				@Override
				public Widget render(IPlayerMatchStats c) {
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
				public IPlayerMatchStats update(IPlayerMatchStats p) {
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
			
			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//CleanBreaks

				@Override
				public Widget render(IPlayerMatchStats c) {
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
				public IPlayerMatchStats update(IPlayerMatchStats p) {
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
			
			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//DefendersBeaten

				@Override
				public Widget render(IPlayerMatchStats c) {
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
				public IPlayerMatchStats update(IPlayerMatchStats p) {
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

			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//OffLoads

				@Override
				public Widget render(IPlayerMatchStats c) {
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
				public IPlayerMatchStats update(IPlayerMatchStats p) {
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
			
			
			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//Turnovers

				@Override
				public Widget render(IPlayerMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getTurnovers().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public IPlayerMatchStats update(IPlayerMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setTurnovers(l);
					}
					return p;
				}

			});
			
			
			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//TacklesMade

				@Override
				public Widget render(IPlayerMatchStats c) {
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
				public IPlayerMatchStats update(IPlayerMatchStats p) {
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
			
			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//TacklesMissed

				@Override
				public Widget render(IPlayerMatchStats c) {
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
				public IPlayerMatchStats update(IPlayerMatchStats p) {
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

			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//LineoutsWonOnThrow

				@Override
				public Widget render(IPlayerMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getLineoutsWonOnThrow().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public IPlayerMatchStats update(IPlayerMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setLineoutsWonOnThrow(l);
					}
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//LineoutsStolenOnOppThrow

				@Override
				public Widget render(IPlayerMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getLineoutsStolenOnOppThrow().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public IPlayerMatchStats update(IPlayerMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setLineoutsStolenOnOppThrow(l);
					}
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//PenaltiesConceded

				@Override
				public Widget render(IPlayerMatchStats c) {
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
				public IPlayerMatchStats update(IPlayerMatchStats p) {
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

			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//YellowCards

				@Override
				public Widget render(IPlayerMatchStats c) {
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
				public IPlayerMatchStats update(IPlayerMatchStats p) {
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

			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//RedCards

				@Override
				public Widget render(IPlayerMatchStats c) {
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
				public IPlayerMatchStats update(IPlayerMatchStats p) {
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

			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
				//TimePlayed

				@Override
				public Widget render(IPlayerMatchStats c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getTimePlayed().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public IPlayerMatchStats update(IPlayerMatchStats p) {
					Integer l = null;
					try {
						l = Integer.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setTimePlayed(l);
					}
					return p;
				}

			});
			
			fieldDefinitions.add(new FieldDefinition<IPlayerMatchStats>() {
					//scrumLink
			        private Anchor w;
			        
			        @Override
			        public void bind (Widget in)
			        {
			        	w = (Anchor) in;
			        }
			        
			        @Override
					public Widget render(final IPlayerMatchStats c) {
						clientFactory.getMatchGroupAsync(c.getMatchId(), new ClientFactory.GetMatchGroupCallback() {
			
							@Override
							public void onMatchGroupFetched(IMatchGroup match) {
								w.setText("Link to match report on scrum.com");
					        	w.setHref(match.getForeignUrl());
					        	w.setTarget("_blank");
							}
						});
			        	
			        	return w;
			        }
			
					@Override
					public void clear() {
						w.setText(null);
					}
					
					@Override
					public IPlayerMatchStats update(IPlayerMatchStats p) {
						// can't edit id
						return p;
					}
					
			      });
		}
	}

	public List<FieldDefinition<IPlayerMatchStats>> getFieldDefinitions() {
		return fieldDefinitions;
	}

}
