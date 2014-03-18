package net.rugby.foundation.admin.client.ui.matchratingengineschemapopup;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.ui.FieldDefinition;
import net.rugby.foundation.model.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema20130713;

public class MatchRatingEngineSchemaPopupViewFieldDefinitions<T extends IRatingEngineSchema> {
	private List<FieldDefinition<T>> fieldDefinitions =
			new ArrayList<FieldDefinition<T>>();

	//	@UiField Label   id;
	//	@UiField Label   name;
	//	@UiField Label   description;

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
	
	//

	@SuppressWarnings("unchecked")
	public MatchRatingEngineSchemaPopupViewFieldDefinitions(final ClientFactory clientFactory) {
		if (fieldDefinitions.isEmpty()) {

			fieldDefinitions.add((FieldDefinition<T>) (FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//1 id

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof Label);
					if (c != null && c.getId() != null) {
						String text = c.getId().toString();
						if (c.getIsDefault() != null && c.getIsDefault()) {
							text += " (Default)";
						}
						((Label)w).setText(text);
					}

					return w;
				}

				@Override
				public void clear() {
					((Label)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					// can't edit
					return p;
				}

			});

			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//2 name

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					if (c != null && c.getName() != null) {
						((TextBox)w).setText(c.getName());
					}
					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					p.setName(((TextBox)w).getText());
					return p;
				}

			});


			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//3 description

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					if (c != null && c.getDescription() != null) {
						((TextBox)w).setText(c.getDescription());
					}
					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					p.setDescription(((TextBox)w).getText());
					return p;
				}

			});


			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//4 tries

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getTriesWeight().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setTriesWeight(l);
					}
					return p;
				}

			});

			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//5 try assists

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getTryAssistsWeight().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setTryAssistsWeight(l);
					}
					return p;
				}

			});

			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//points

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getPointsWeight().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setPointsWeight(l);
					}
					return p;
				}

			});

			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//runs

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getRunsWeight().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setRunsWeight(l);
					}
					return p;
				}

			});
			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//passes

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getPassesWeight().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setPassesWeight(l);
					}
					return p;
				}

			});
			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//9 kicks

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getKicksWeight().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setKicksWeight(l);
					}
					return p;
				}

			});

			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//metersRun

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getMetersRunWeight().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setMetersRunWeight(l);
					}
					return p;
				}

			});    		
			
			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//CleanBreaks

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getCleanBreaksWeight().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setCleanBreaksWeight(l);
					}
					return p;
				}

			});
			
			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//DefendersBeaten

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getDefendersBeatenWeight().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setDefendersBeatenWeight(l);
					}
					return p;
				}

			});    		

			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//OffLoads

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getOffloadsWeight().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setOffloadsWeight(l);
					}
					return p;
				}

			});
			
			
			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//Turnovers

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getTurnoversWeight().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setTurnoversWeight(l);
					}
					return p;
				}

			});
			
			
			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//15 TacklesMade

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getTacklesMadeWeight().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setTacklesMadeWeight(l);
					}
					return p;
				}

			});
			
			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//TacklesMissed

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getTacklesMissedWeight().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setTacklesMissedWeight(l);
					}
					return p;
				}

			});

			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//LineoutsWonOnThrow

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getLineoutsWonOnThrowWeight().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setLineoutsWonOnThrowWeight(l);
					}
					return p;
				}

			});

			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//LineoutsStolenOnOppThrow

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getLineoutsStolenOnOppThrowWeight().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setLineoutsStolenOnOppThrowWeight(l);
					}
					return p;
				}

			});

			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//PenaltiesConceded

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getPenaltiesConcededWeight().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setPenaltiesConcededWeight(l);
					}
					return p;
				}

			});

			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//YellowCards

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getYellowCardsWeight().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setYellowCardsWeight(l);
					}
					return p;
				}

			});

			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//21 RedCards

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					((TextBox)w).setText(c.getRedCardsWeight().toString());

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setRedCardsWeight(l);
					}
					return p;
				}

			});

			// *****
			//	Team based stats
			// *****
			


			
			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				// scrum share

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					if (c != null && c.getScrumShareWeight() != null) {
						((TextBox)w).setText(c.getScrumShareWeight().toString());
					}
					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setScrumShareWeight(l);
					}
					return p;
				}

			});
			

			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				// lineout share

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					if (c != null && c.getLineoutShareWeight() != null) {
						((TextBox)w).setText(c.getLineoutShareWeight().toString());
					}
					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setLineoutShareWeight(l);
					}
					return p;
				}

			});
			
			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				// ruck share

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					if (c != null && c.getRuckShareWeight() != null) {
						((TextBox)w).setText(c.getRuckShareWeight().toString());
					}
					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setRuckShareWeight(l);
					}
					return p;
				}

			});
			
			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				// scrum lost

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					if (c != null && c.getScrumLostWeight() != null) {
						((TextBox)w).setText(c.getScrumLostWeight().toString());
					}
					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setScrumLostWeight(l);
					}
					return p;
				}

			});
			

			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				// lineout lost

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					if (c != null && c.getLineoutLostWeight() != null) {
						((TextBox)w).setText(c.getLineoutLostWeight().toString());
					}
					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setLineoutLostWeight(l);
					}
					return p;
				}

			});
			
			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				// ruck lost

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					if (c != null && c.getRuckLostWeight() != null) {
						((TextBox)w).setText(c.getRuckLostWeight().toString());
					}
					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setRuckLostWeight(l);
					}
					return p;
				}

			});
			
			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				// scrum stolen

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					if (c != null && c.getScrumStolenWeight() != null) {
						((TextBox)w).setText(c.getScrumStolenWeight().toString());
					}
					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setScrumStolenWeight(l);
					}
					return p;
				}

			});
			

			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				// lineout stolen

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					if (c != null && c.getLineoutStolenWeight() != null) {
						((TextBox)w).setText(c.getLineoutStolenWeight().toString());
					}
					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setLineoutStolenWeight(l);
					}
					return p;
				}

			});
			
			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				// ruck stolen

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					if (c != null && c.getRuckStolenWeight() != null) {
						((TextBox)w).setText(c.getRuckStolenWeight().toString());
					}
					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setRuckStolenWeight(l);
					}
					return p;
				}

			});

			
			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				// maul share

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					if (c != null && c.getMaulShareWeight() != null) {
						((TextBox)w).setText(c.getMaulShareWeight().toString());
					}
					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setMaulShareWeight(l);
					}
					return p;
				}

			});
			
			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				// maul lost

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					if (c != null && c.getMaulLostWeight() != null) {
						((TextBox)w).setText(c.getMaulLostWeight().toString());
					}
					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setMaulLostWeight(l);
					}
					return p;
				}

			});
			
			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				// maul stolen

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					if (c != null && c.getMaulStolenWeight() != null) {
						((TextBox)w).setText(c.getMaulStolenWeight().toString());
					}
					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setMaulStolenWeight(l);
					}
					return p;
				}

			});
			
			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				// minutes share

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					if (c != null && c.getMinutesShareWeight() != null) {
						((TextBox)w).setText(c.getMinutesShareWeight().toString());
					}
					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setMinutesShareWeight(l);
					}
					return p;
				}

			});
			

			
			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//points differential

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					if (c.getPointsDifferentialWeight() != null) {
						((TextBox)w).setText(c.getPointsDifferentialWeight().toString());
					}

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setPointsDifferentialWeight(l);
					}
					return p;
				}

			});
			
			fieldDefinitions.add((FieldDefinition<T>) new FieldDefinition<ScrumMatchRatingEngineSchema20130713>() {
				//win

				@Override
				public Widget render(ScrumMatchRatingEngineSchema20130713 c) {
					assert (w instanceof TextBox);
					if (c.getWin() != null) {
						((TextBox)w).setText(c.getWin().toString());
					}

					return w;
				}

				@Override
				public void clear() {
					assert (w instanceof TextBox);
					((TextBox)w).setText(null);
				}

				@Override
				public ScrumMatchRatingEngineSchema20130713 update(ScrumMatchRatingEngineSchema20130713 p) {
					Float l = null;
					try {
						l = Float.valueOf(((TextBox)w).getText());
					} catch (NumberFormatException e) {
						return null;
					}
					if (l != null) {
						p.setWin(l);
					}
					return p;
				}

			});
		}
	}
		

	public List<FieldDefinition<T>> getFieldDefinitions() {
		return fieldDefinitions;
	}

}
