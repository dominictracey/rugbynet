package net.rugby.foundation.admin.client.ui.seriesconfiguration;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
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

				@Override
				public void bind (Widget in)
				{
					w = (ListBox) in;
					clientFactory.getCompListAsync(new GetCompListCallback() {

						@Override
						public void onCompListFetched(List<ICompetition> comps) {
							int count = 0;
							for (ICompetition c : comps) {
								w.addItem(c.getShortName(), c.getId().toString());
								// check to see if the one we are adding is the one we want selected (as set in render())
								if (selectedIds.contains(c.getId())) {
									w.setItemSelected(count, true);
								}
								count++;
							}
						}
					});
				}

				@Override
				public Widget render(ISeriesConfiguration c) {
					if (w.getItemCount() > 0) {
						int i = 0;
						String index = w.getItemText(i);
						if (c.getCountries() != null && c.getCountries().size() > 0) {
							while (!c.getCountries().get(i).getName().equals(index) && i < w.getItemCount()) {
								++i;
								index = w.getItemText(i);
							}

							w.setItemSelected(i, true);
						}
					} else {
						//selectedIds.addAll(c.getCountryIds());
					}

					return w;
				}

				@Override
				public void clear() {
					//w.setItemSelected(0, false);
				}

				@Override
				public ISeriesConfiguration update(ISeriesConfiguration p) {
					int index = 0;
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
						}
					});
				}

				@Override
				public Widget render(ISeriesConfiguration c) {
					if (w.getItemCount() > 0) {
						int i = 0;
						String index = w.getItemText(i);
						if (c.getCountries() != null && c.getCountries().size() > 0) {
							while (!c.getCountries().get(i).getName().equals(index) && i < w.getItemCount()) {
								++i;
								index = w.getItemText(i);
							}

							w.setItemSelected(i, true);
						}
					} else {
						//selectedIds.addAll(c.getCountryIds());
					}

					return w;
				}

				@Override
				public void clear() {
					//w.setItemSelected(0, false);
				}

				@Override
				public ISeriesConfiguration update(ISeriesConfiguration p) {
					int index = 0;
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
				private List<Long> selectedIds = new ArrayList<Long>();

				@Override
				public void bind (Widget in)
				{
					w = (ListBox) in;
					w.addItem("OVERALL");
					w.addItem("BY_POSITION");
					w.addItem("BY_COUNTRY");
					w.addItem("BY_TEAM");
					w.addItem("BY_NEXT_MATCH");
					w.addItem("BY_LAST_MATCH");
				}

				@Override
				public Widget render(ISeriesConfiguration c) {
					if (w.getItemCount() > 0) {
						int i = 0;
						String index = w.getItemText(i);
						if (c.getMode() != null) {
							if (c.getMode().equals(RatingMode.OVERALL)) {
								w.setItemSelected(0, true);
							} else if (c.getMode().equals(RatingMode.BY_POSITION)) {
								w.setItemSelected(1, true);
							}  else if (c.getMode().equals(RatingMode.BY_COUNTRY)) {
								w.setItemSelected(2, true);
							}  else if (c.getMode().equals(RatingMode.BY_TEAM)) {
								w.setItemSelected(3, true);
							}  else if (c.getMode().equals(RatingMode.BY_NEXT_MATCH)) {
								w.setItemSelected(4, true);
							}   else if (c.getMode().equals(RatingMode.BY_LAST_MATCH)) {
								w.setItemSelected(5, true);
							}
						}
					} else {
						//selectedIds.addAll(c.getCountryIds());
					}

					return w;
				}

				@Override
				public void clear() {
					w.setItemSelected(0, false);
				}

				@Override
				public ISeriesConfiguration update(ISeriesConfiguration p) {
					int i = w.getSelectedIndex();

					if (i == 0) {
						p.setMode(RatingMode.OVERALL);
					} else if (i == 1) {
						p.setMode(RatingMode.BY_POSITION);
					} else if (i == 2) {
						p.setMode(RatingMode.BY_COUNTRY);
					} else if (i == 3) {
						p.setMode(RatingMode.BY_TEAM);
					} else if (i == 4) {
						p.setMode(RatingMode.BY_NEXT_MATCH);
					} else if (i == 5) {
						p.setMode(RatingMode.BY_LAST_MATCH);
					}

					return p;
				}

			});

			fieldDefinitions.add(new FieldDefinition<ISeriesConfiguration>() {
				//startDate
				private ListBox w;
				// need this bit of gorminess as the first player is passed in before the country list is completely loaded.
				private List<Long> selectedIds = new ArrayList<Long>();
				private List<UniversalRound> rounds =  null;
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
						}
					});
				}

				@Override
				public Widget render(ISeriesConfiguration c) {
					int index = 0;
//					while (rounds == null) {
//						try {
//							Thread.sleep(100);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
					if (rounds != null) {
						for (UniversalRound ur: rounds) {
							if (ur.equals(c.getTargetRound()))
								break;
							++index;
						}
	
						w.setSelectedIndex(index);
					}
					return w;
				}

				@Override
				public void clear() {
					//w.setItemSelected(0, false);
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
