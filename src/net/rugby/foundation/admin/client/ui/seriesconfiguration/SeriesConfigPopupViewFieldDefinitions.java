package net.rugby.foundation.admin.client.ui.seriesconfiguration;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.ui.FieldDefinition;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IRatingQuery.MinMinutes;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.model.shared.UniversalRound;

import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.InlineRadio;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;

import com.google.gwt.user.client.ui.Widget;

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
	//protected List<ICompetition> compList = null;
	protected ICoreConfiguration config = null;
	protected List<ICountry> countryList = null;
	protected List<UniversalRound> urList = null;

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

				@Override
				public void bind (Widget in)
				{
					w = (ListBox) in;

//					assert (compList != null);
//					for (ICompetition c : compList) {
//						w.addItem(c.getShortName(), c.getId().toString());
//					}
					assert (config != null); 
					for (Long id : config.getCompsUnderway()) {
						w.addItem(config.getCompetitionMap().get(id), id.toString());
					}
					

				}

				@Override
				public Widget render(ISeriesConfiguration c) {
					clear();

					int i = 0;
					while (i < w.getItemCount()) {
						if (c.getCompIds().contains(Long.valueOf(w.getValue(i)))) {
							w.setItemSelected(i, true);
						}
						++i;
					}

					return w;
				}





				@Override
				public void clear() {
					for (int i=0; i<w.getItemCount(); ++i) {
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

				@Override
				public void bind (Widget in)
				{
					w = (ListBox) in;

					for (ICountry c : countryList) {
						w.addItem(c.getName(), c.getId().toString());
					}
				}

				@Override
				public Widget render(ISeriesConfiguration c) {

					int i = 0;
					clear();
					while (i < w.getItemCount()) {
						if (c.getCountryIds().contains(Long.valueOf(w.getValue(i)))) {
							w.setItemSelected(i, true);
						}
						++i;
					}
					return w;
				}

				@Override
				public void clear() {
					for (int i=0; i<w.getItemCount(); ++i) {
						w.setItemSelected(i, false);
					}
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


				@Override
				public void bind (Widget in)
				{
					w = (ListBox) in;

//					for (ICompetition c : compList) {
//						w.addItem(c.getShortName(), c.getId().toString());
//					}
					assert (config != null); 
					for (Long id : config.getCompsUnderway()) {
						w.addItem(config.getCompetitionMap().get(id), id.toString());
					}

				}

				@Override
				public Widget render(ISeriesConfiguration c) {
					clear();
					int i = 0;
					while (i < w.getItemCount()) {
						if (c.getHostCompId() == null) {
							break;
						}
						if (c.getHostCompId().equals(Long.valueOf(w.getValue(i)))) {
							w.setItemSelected(i, true);
							break;
						}
						++i;
					}
					return w;
				}

				@Override
				public void clear() {
					for (int i=0; i<w.getItemCount(); ++i) {
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

				@Override
				public void bind (Widget in)
				{
					w = (ListBox) in;

					for (UniversalRound c : urList) {
						w.addItem(c.shortDesc, Integer.toString(c.ordinal));
					}

				}

				@Override
				public Widget render(ISeriesConfiguration c) {

					int i = 0;
					if (c.getTargetRound() != null) {
						while (i < w.getItemCount()) {
							if (c.getTargetRound().ordinal == Integer.valueOf(w.getValue(i))) {
								w.setItemSelected(i, true);
								break;
							}
							++i;
						}
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
					int index = w.getSelectedIndex();
					p.setTargetRound(urList.get(index));
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

			fieldDefinitions.add(new FieldDefinition<ISeriesConfiguration>() {
				//minMinuteRound
				private InlineRadio w;

				@Override
				public void bind (Widget in)
				{
					w = (InlineRadio) in;
				}

				@Override
				public Widget render(ISeriesConfiguration c) {
					if (c.getActiveCriteria().contains(Criteria.AVERAGE_IMPACT)) {
						w.setEnabled(true);
						if (c.getMinMinuteType() != null && c.getMinMinuteType().equals(MinMinutes.ROUND)) {
							w.setValue(true);
						} else {
							w.setValue(false);
						}
					} else {
						w.setValue(false);
						w.setEnabled(false);
					}

					return w;
				}

				@Override
				public void clear() {
					w.setValue(false);
				}

				@Override
				public ISeriesConfiguration update(ISeriesConfiguration p) {
					if (w.getValue()) {
						p.setMinMinuteType(MinMinutes.ROUND);
					}
					
					return p;
				}

			});
			
			fieldDefinitions.add(new FieldDefinition<ISeriesConfiguration>() {
				//minMinuteTotal
				private InlineRadio w;

				@Override
				public void bind (Widget in)
				{
					w = (InlineRadio) in;
				}

				@Override
				public Widget render(ISeriesConfiguration c) {
					if (c.getActiveCriteria().contains(Criteria.AVERAGE_IMPACT)) {
						w.setEnabled(true);
						if (c.getMinMinuteType() != null && c.getMinMinuteType().equals(MinMinutes.TOTAL)) {
							w.setValue(true);
						} else {
							w.setValue(false);
						}
					} else {
						w.setValue(false);
						w.setEnabled(false);
					}

					return w;
				}

				@Override
				public void clear() {
					w.setValue(false);
				}

				@Override
				public ISeriesConfiguration update(ISeriesConfiguration p) {
					if (w.getValue()) {
						p.setMinMinuteType(MinMinutes.TOTAL);
					}
					
					return p;
				}

			});


			fieldDefinitions.add(new FieldDefinition<ISeriesConfiguration>() {
				//roundMinMinutes
				private TextBox w;

				@Override
				public void bind (Widget in)
				{
					w = (TextBox) in;
				}

				@Override
				public Widget render(ISeriesConfiguration c) {
					w.setValue(String.valueOf(c.getMinMinutes()));

					return w;
				}

				@Override
				public void clear() {
					w.setValue("0");
				}

				@Override
				public ISeriesConfiguration update(ISeriesConfiguration p) {
					p.setMinMinutes(Integer.parseInt(w.getValue()));
					return p;
				}

			});
		}
	}

	public List<FieldDefinition<ISeriesConfiguration>> getFieldDefinitions() {
		return fieldDefinitions;
	}
//
//	public List<ICompetition> getCompList() {
//		return compList;
//	}
//
//	public void setCompList(List<ICompetition> compList) {
//		this.compList = compList;
//	}

	public List<ICountry> getCountryList() {
		return countryList;
	}

	public void setCountryList(List<ICountry> countryList) {
		this.countryList = countryList;
	}

	public List<UniversalRound> getUrList() {
		return urList;
	}

	public void setUrList(List<UniversalRound> urList) {
		this.urList = urList;
	}

	public void setConfig(ICoreConfiguration config) {
		this.config = config;
	}

	public ICoreConfiguration getConfig() {
		return config;
	}
}
