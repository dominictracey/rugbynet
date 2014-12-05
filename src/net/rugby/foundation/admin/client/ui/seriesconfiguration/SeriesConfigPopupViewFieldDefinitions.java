package net.rugby.foundation.admin.client.ui.seriesconfiguration;

import java.util.ArrayList;
import java.util.List;

import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.ClientFactory.GetCompListCallback;
import net.rugby.foundation.admin.client.ClientFactory.GetCountryListCallback;
import net.rugby.foundation.admin.client.ClientFactory.GetUniversalRoundsListCallback;
import net.rugby.foundation.admin.client.ui.FieldDefinition;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.model.shared.UniversalRound;

public class SeriesConfigPopupViewFieldDefinitions<T> {
	private static List<FieldDefinition<ISeriesConfiguration>> fieldDefinitions =
			new ArrayList<FieldDefinition<ISeriesConfiguration>>();

	//	@UiField Label id;
	//	@UiField TextBox displayName;
	//	@UiField DatePicker startDate;
	//	@UiField DatePicker endDate;
	//	@UiField ListBox countries;
	//	@UiField ListBox comps;
	//	@UiField ListBox mode;
	//		
	//	@UiField CheckBox inForm;
	//	@UiField CheckBox bestYear;
	//	@UiField CheckBox bestAllTime;
	//	@UiField CheckBox average;
	//	
	//	@UiField CheckBox live;
	protected List<ICompetition> compList = null;
	protected boolean compsLoaded = false;
	
	public SeriesConfigPopupViewFieldDefinitions(final ClientFactory clientFactory) {
		if (fieldDefinitions.isEmpty()) {


			fieldDefinitions.add(new FieldDefinition<ISeriesConfiguration>() {
				//id
				private Label w;

				@Override
				public void bind (Widget in)
				{
					w = (Label) in;
				}

				@Override
				public Widget render(ISeriesConfiguration c) {
					if (c != null && c.getId() != null) {
						w.setText(c.getId().toString());
					}

					return w;
				}

				@Override
				public void clear() {
					w.setText(null);
				}

				@Override
				public ISeriesConfiguration update(ISeriesConfiguration p) {
					// can't edit id
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ISeriesConfiguration>() {
				//displayName
				private TextBox w;

				@Override
				public void bind (Widget in)
				{
					w = (TextBox) in;
				}

				@Override
				public Widget render(ISeriesConfiguration c) {
					w.setText(c.getDisplayName());

					return w;
				}

				@Override
				public void clear() {
					w.setText(null);
				}

				@Override
				public ISeriesConfiguration update(ISeriesConfiguration p) {
					if (!w.getText().isEmpty())
						p.setDisplayName(w.getText());

					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ISeriesConfiguration>() {
				//comps
				private ListBox w;
				// need this bit of gorminess as the first player is passed in before the country list is completely loaded.
				private List<Long> selectedIds = new ArrayList<Long>();

				private Timer t = null;

				@Override
				public void bind (Widget in)
				{
					compsLoaded = false;
					w = (ListBox) in;
					clientFactory.getCompListAsync(new GetCompListCallback() {

						@Override
						public void onCompListFetched(List<ICompetition> comps) {
							int count = 0;
							compList = comps;
							for (ICompetition c : comps) {
								w.addItem(c.getShortName(), c.getId().toString());
								// check to see if the one we are adding is the one we want selected (as set in render())
								if (selectedIds.contains(c.getId())) {
									w.setItemSelected(count, true);
								}
								count++;
							}
							compsLoaded = true;
						}
					});
				}

				@Override
				public Widget render(ISeriesConfiguration c) {

					final ISeriesConfiguration _c = c;


					t = new Timer() {
						@Override
						public void run() {
							if (compsLoaded) {
								if (w.getItemCount() > 0) {
									int i = 0;
									if (_c.getCompIds() != null && _c.getCompIds().size() > 0) {
										while (i < w.getItemCount()) {
											if (_c.getCompIds().contains(Long.valueOf(w.getValue(i)))) {
												w.setItemSelected(i, true);
											}
											++i;
										}
									}
								}
							} else {
								// go again
								t.schedule(5000);
							}
						}
					};

					t.schedule(50);
					return w;
				}





				@Override
				public void clear() {
					for (int i=0; i<w.getVisibleItemCount(); ++i) {
						w.setItemSelected(i, false);
					}
				}

				@Override
				public ISeriesConfiguration update(ISeriesConfiguration p) {
					int index = 0;
					p.getCompIds().clear();
					while (index < w.getItemCount() && !w.getValue(index).isEmpty())
					{
						if (w.isItemSelected(index)) {
							p.getCompIds().add(Long.valueOf(w.getValue(index)));
						}
						index++;
					}

					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ISeriesConfiguration>() {
				//countries
				private ListBox w;
				// need this bit of gorminess as the first player is passed in before the country list is completely loaded.
				private List<Long> selectedIds = new ArrayList<Long>();
				Timer t = null;
				boolean countriesLoaded = false;
				@Override
				public void bind (Widget in)
				{
					w = (ListBox) in;
					clientFactory.getCountryListAsync(new GetCountryListCallback() {

						@Override
						public void onCountryListFetched(List<ICountry> countries) {
							int count = 0;
							for (ICountry c : countries) {
								w.addItem(c.getName(), c.getId().toString());
								// check to see if the one we are adding is the one we want selected (as set in render())
								if (selectedIds.contains(c.getId())) {
									w.setItemSelected(count, true);
								}
								count++;
							}
							countriesLoaded = true;
						}
					});
				}

				@Override
				public Widget render(ISeriesConfiguration c) {

					final ISeriesConfiguration _c = c;


					t = new Timer() {
						@Override
						public void run() {
							if (countriesLoaded) {
								if (w.getItemCount() > 0) {
									int i = 0;
									if (_c.getCountryIds() != null && _c.getCountryIds().size() > 0) {
										while (i < w.getItemCount()) {
											if (_c.getCountryIds().contains(Long.valueOf(w.getValue(i)))) {
												w.setItemSelected(i, true);
											}
											++i;
										}
									}
								}
							} else {
								// go again
								t.schedule(5000);
							}
						}
					};

					t.schedule(50);
					return w;
				}

				@Override
				public void clear() {
					for (int i=0; i<w.getVisibleItemCount(); ++i) {
						w.setItemSelected(i, false);
					}
					//

				}

				@Override
				public ISeriesConfiguration update(ISeriesConfiguration p) {
					int index = 0;
					p.getCountryIds().clear();
					while (index < w.getItemCount() && !w.getValue(index).isEmpty())
					{
						if (w.isItemSelected(index)) {
							p.getCountryIds().add(Long.valueOf(w.getValue(index)));
						}
						index++;
					}

					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ISeriesConfiguration>() {
				//mode
				private ListBox w;
				// need this bit of gorminess as the first player is passed in before the country list is completely loaded.
				//private List<Long> selectedIds = new ArrayList<Long>();

				@Override
				public void bind (Widget in)
				{
					w = (ListBox) in;
					for (RatingMode mode : RatingMode.values()) {
						w.addItem(mode.toString());
					}
					//					w.addItem("BY_POSITION");
					//					w.addItem("BY_COUNTRY");
					//					w.addItem("BY_TEAM");
					//					w.addItem("BY_MATCH");
					//					w.addItem("BY_LAST_MATCH");
				}

				@Override
				public Widget render(ISeriesConfiguration c) {
					if (w.getItemCount() > 0) {
						int i = 0;
						//String index = w.getItemText(i);
						if (c.getMode() != null) {
							for (RatingMode mode : RatingMode.values()) {
								if (c.getMode().equals(mode)) {
									w.setItemSelected(i, true);
									break;
								}
								++i;
							}
						}
						//						if (c.getMode() != null) {
						//							if (c.getMode().equals(RatingMode.BY_COMP)) {
						//								w.setItemSelected(0, true);
						//							} else if (c.getMode().equals(RatingMode.BY_POSITION)) {
						//								w.setItemSelected(1, true);
						//							}  else if (c.getMode().equals(RatingMode.BY_COUNTRY)) {
						//								w.setItemSelected(2, true);
						//							}  else if (c.getMode().equals(RatingMode.BY_TEAM)) {
						//								w.setItemSelected(3, true);
						//							}  else if (c.getMode().equals(RatingMode.BY_MATCH)) {
						//								w.setItemSelected(4, true);
						//							}
						//						}
					} else {
						//selectedIds.addAll(c.getCountryIds());
					}

					return w;
				}

				@Override
				public void clear() {
					for (int i=0; i<w.getVisibleItemCount(); ++i) {
						w.setItemSelected(i, false);
					}
				}

				@Override
				public ISeriesConfiguration update(ISeriesConfiguration p) {
					int i = w.getSelectedIndex();

					if (i == 0) {
						p.setMode(RatingMode.BY_COMP);
					} else if (i == 1) {
						p.setMode(RatingMode.BY_POSITION);
					} else if (i == 2) {
						p.setMode(RatingMode.BY_COUNTRY);
					} else if (i == 3) {
						p.setMode(RatingMode.BY_TEAM);
					} else if (i == 4) {
						p.setMode(RatingMode.BY_MATCH);
					}

					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ISeriesConfiguration>() {
				//host Comp
				private ListBox w;
				Timer t = null;
				// need this bit of gorminess as the first player is passed in before the country list is completely loaded.
				//private List<Long> selectedIds = new ArrayList<Long>();

				@Override
				public void bind (Widget in)
				{
					w = (ListBox) in;
					
					// compList gets loaded above, we just have to wait to render


					t = new Timer() {
						@Override
						public void run() {
							if (compsLoaded) {
								for (ICompetition c : compList) {
									w.addItem(c.getShortName(), c.getId().toString());
								}
								t.cancel();
							}
						}
					};

					t.schedule(2000);

				}

				@Override
				public Widget render(ISeriesConfiguration c) {
					final ISeriesConfiguration _c = c;


					t = new Timer() {
						@Override
						public void run() {
							if (compsLoaded) {
								if (w.getItemCount() > 0) {
									int i = 0;
									if (_c.getCompIds() != null && _c.getCompIds().size() > 0) {
										while (i < w.getItemCount()) {
											if (_c.getCompIds().contains(Long.valueOf(w.getValue(i)))) {
												w.setItemSelected(i, true);
											}
											++i;
										}
									}
								}
							} else {
								// go again
								t.schedule(5000);
							}
						}
					};

					t.schedule(50);
					return w;
				}

				@Override
				public void clear() {
					for (int i=0; i<w.getVisibleItemCount(); ++i) {
						w.setItemSelected(i, false);
					}
				}

				@Override
				public ISeriesConfiguration update(ISeriesConfiguration p) {
					int index = 0;
					p.setHostCompId(null);
					while (index < w.getItemCount() && !w.getValue(index).isEmpty())
					{
						if (w.isItemSelected(index)) {
							p.setHostCompId(Long.valueOf(w.getValue(index)));
							break;
						}
						index++;
					}

					return p;
				}

			});
			
			fieldDefinitions.add(new FieldDefinition<ISeriesConfiguration>() {
				//startDate
				private ListBox w;
				// need this bit of gorminess as the first player is passed in before the country list is completely loaded.
				//private List<Long> selectedIds = new ArrayList<Long>();
				private List<UniversalRound> rounds =  null;
				boolean roundsLoaded = false;
				Timer t = null;

				@Override
				public void bind (Widget in)
				{
					w = (ListBox) in;
					clientFactory.getUniversalRoundsListAsync(20, new GetUniversalRoundsListCallback() {

						@Override
						public void onUniversalRoundListFetched(List<UniversalRound> urs) {
							rounds = urs;
							//int count = 0;
							for (UniversalRound c : urs) {
								w.addItem(c.shortDesc, Integer.toString(c.ordinal));
							}
							roundsLoaded = true;
						}
					});
				}

				@Override
				public Widget render(ISeriesConfiguration c) {

					final ISeriesConfiguration _c = c;


					t = new Timer() {
						@Override
						public void run() {
							if (roundsLoaded) {
								if (w.getItemCount() > 0) {
									int i = 0;
									if (_c.getTargetRound() != null) {
										while (i < w.getItemCount()) {
											if (_c.getTargetRound().ordinal == Integer.valueOf(w.getValue(i))) {
												w.setItemSelected(i, true);
												break;
											}
											++i;
										}
									}
								}
							} else {
								// go again
								t.schedule(5000);
							}
						}
					};

					t.schedule(50);
					return w;
				}

				@Override
				public void clear() {
					for (int i=0; i<w.getVisibleItemCount(); ++i) {
						w.setItemSelected(i, false);
					}
				}

				@Override
				public ISeriesConfiguration update(ISeriesConfiguration p) {
					int index = w.getSelectedIndex();
					p.setTargetRound(rounds.get(index));
					p.setTargetRoundOrdinal(Integer.parseInt(w.getValue(index),10));	

					return p;
				}

			});





			//			fieldDefinitions.add(new FieldDefinition<ISeriesConfiguration>() {
			//				//startDate
			//				private ListBox w;
			//				// need this bit of gorminess as the first player is passed in before the country list is completely loaded.
			//				private List<Long> selectedIds = new ArrayList<Long>();
			//				private List<UniversalRound> rounds =  null;
			//				@Override
			//				public void bind (Widget in)
			//				{
			//					w = (ListBox) in;
			//					clientFactory.getUniversalRoundsListAsync(20, new GetUniversalRoundsListCallback() {
			//
			//						@Override
			//						public void onUniversalRoundListFetched(List<UniversalRound> urs) {
			//							rounds = urs;
			//							//int count = 0;
			//							for (UniversalRound c : urs) {
			//								w.addItem(c.shortDesc, Integer.toString(c.ordinal));
			//								// check to see if the one we are adding is the one we want selected (as set in render())
			//								//							if (selectedIds.contains(c.getId())) {
			//								//								w.setItemSelected(count, true);
			//								//							}
			//								//count++;
			//							}
			//						}
			//					});
			//				}
			//
			//				@Override
			//				public Widget render(ISeriesConfiguration c) {
			//					int index = 0;
			//					for (UniversalRound ur: rounds) {
			//						if (ur.equals(c.getTargetRound()))
			//							break;
			//						++index;
			//					}
			//					w.setSelectedIndex(index);
			//
			//					return w;
			//				}
			//
			//				@Override
			//				public void clear() {
			//					//w.setItemSelected(0, false);
			//				}
			//
			//				@Override
			//				public ISeriesConfiguration update(ISeriesConfiguration p) {
			//					int index = w.getSelectedIndex();
			//					p.setTargetRound(rounds.get(index));
			//					p.setTargetRoundOrdinal(Integer.parseInt(w.getValue(index),10));	
			//
			//					return p;
			//				}
			//
			//			});

			fieldDefinitions.add(new FieldDefinition<ISeriesConfiguration>() {
				//Criteria.ROUND
				private CheckBox w;

				@Override
				public void bind (Widget in)
				{
					w = (CheckBox) in;
				}

				@Override
				public Widget render(ISeriesConfiguration c) {
					w.setValue(c.getActiveCriteria().contains(Criteria.ROUND));

					return w;
				}

				@Override
				public void clear() {
					w.setValue(false);
				}

				@Override
				public ISeriesConfiguration update(ISeriesConfiguration p) {
					p.getActiveCriteria().remove(Criteria.ROUND);
					if (w.getValue())
						p.getActiveCriteria().add(Criteria.ROUND);
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ISeriesConfiguration>() {
				//Criteria.IN_FORM
				private CheckBox w;

				@Override
				public void bind (Widget in)
				{
					w = (CheckBox) in;
				}

				@Override
				public Widget render(ISeriesConfiguration c) {
					w.setValue(c.getActiveCriteria().contains(Criteria.IN_FORM));

					return w;
				}

				@Override
				public void clear() {
					w.setValue(false);
				}

				@Override
				public ISeriesConfiguration update(ISeriesConfiguration p) {
					p.getActiveCriteria().remove(Criteria.IN_FORM);
					if (w.getValue())
						p.getActiveCriteria().add(Criteria.IN_FORM);
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ISeriesConfiguration>() {
				//Criteria.BEST_YEAR
				private CheckBox w;

				@Override
				public void bind (Widget in)
				{
					w = (CheckBox) in;
				}

				@Override
				public Widget render(ISeriesConfiguration c) {
					w.setValue(c.getActiveCriteria().contains(Criteria.BEST_YEAR));

					return w;
				}

				@Override
				public void clear() {
					w.setValue(false);
				}

				@Override
				public ISeriesConfiguration update(ISeriesConfiguration p) {
					p.getActiveCriteria().remove(Criteria.BEST_YEAR);
					if (w.getValue())
						p.getActiveCriteria().add(Criteria.BEST_YEAR);
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ISeriesConfiguration>() {
				//Criteria.BEST_ALLTIME
				private CheckBox w;

				@Override
				public void bind (Widget in)
				{
					w = (CheckBox) in;
				}

				@Override
				public Widget render(ISeriesConfiguration c) {
					w.setValue(c.getActiveCriteria().contains(Criteria.BEST_ALLTIME));

					return w;
				}

				@Override
				public void clear() {
					w.setValue(false);
				}

				@Override
				public ISeriesConfiguration update(ISeriesConfiguration p) {
					p.getActiveCriteria().remove(Criteria.BEST_ALLTIME);
					if (w.getValue())
						p.getActiveCriteria().add(Criteria.BEST_ALLTIME);
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ISeriesConfiguration>() {
				//Criteria.AVERAGE_IMPACT
				private CheckBox w;

				@Override
				public void bind (Widget in)
				{
					w = (CheckBox) in;
				}

				@Override
				public Widget render(ISeriesConfiguration c) {
					w.setValue(c.getActiveCriteria().contains(Criteria.AVERAGE_IMPACT));

					return w;
				}

				@Override
				public void clear() {
					w.setValue(false);
				}

				@Override
				public ISeriesConfiguration update(ISeriesConfiguration p) {
					p.getActiveCriteria().remove(Criteria.AVERAGE_IMPACT);
					if (w.getValue())
						p.getActiveCriteria().add(Criteria.AVERAGE_IMPACT);
					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ISeriesConfiguration>() {
				//live
				private CheckBox w;

				@Override
				public void bind (Widget in)
				{
					w = (CheckBox) in;
				}

				@Override
				public Widget render(ISeriesConfiguration c) {
					w.setValue(c.getLive());

					return w;
				}

				@Override
				public void clear() {
					w.setValue(false);
				}

				@Override
				public ISeriesConfiguration update(ISeriesConfiguration p) {
					p.setLive(false);
					if (w.getValue())
						p.setLive(true);
					return p;
				}

			});
		}
	}

	public List<FieldDefinition<ISeriesConfiguration>> getFieldDefinitions() {
		return fieldDefinitions;
	}
}
