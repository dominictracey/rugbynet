/**
 * 
 */
package net.rugby.foundation.admin.client.ui.portal;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.ui.SmartBar;
import net.rugby.foundation.admin.client.ui.playerlistview.PlayerListView;
import net.rugby.foundation.admin.client.ui.playerlistview.PlayerListView.Listener;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.Position.position;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author home
 *
 */
public class PortalViewImpl<T extends IPlayerMatchInfo> extends Composite implements PortalView<T> {



	interface PortalViewImplUiBinder extends UiBinder<Widget, PortalViewImpl<?>> {
	}

	private static PortalViewImplUiBinder uiBinder = GWT
			.create(PortalViewImplUiBinder.class);

	@UiField SimplePanel menuBarPanel;

	@UiField Label compLabel;
	@UiField ListBox comp;
	@UiField ListBox compAndRound;
	@UiField Label roundLabel;
	@UiField ListBox round;
	@UiField ListBox pos;
	@UiField ListBox country;
	@UiField ListBox team;

	@UiField com.github.gwtbootstrap.client.ui.Button timeSeries;
	@UiField Button query;
	@UiField Button clear;
	@UiField Button delete;
	@UiField Button topTen;
	@UiField SimplePanel jobArea;

	private List<IPlayerRating> portalList;
	private PortalViewPresenter<T> listener;

	private ClientFactory clientFactory;

	private SmartBar smartBar;

	//private List<ICompetition> comps;

	private List<Long> teamIds;

	private List<Long> countryIds;

	private List<position> posis;

	private List<Long> roundIds;

	private List<Long> compIds;

	private ICompetition currentComp;

	private IRound currentRound;

	private ICoreConfiguration config;

	private int playersPerTeam = 10;

	private IRatingQuery rq;

	private boolean isSetup = false;

	private List<IPlayerRating> ratingList;

	public PortalViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		compAndRound.setVisible(false);
	}


	@Override
	public void setPresenter(PortalViewPresenter<T> p) {
		listener = p;
		if (listener instanceof SmartBar.Presenter) {
			if (!menuBarPanel.getElement().hasChildNodes()) {
				smartBar = clientFactory.getMenuBar();
				menuBarPanel.add(smartBar);
			}
			smartBar.setPresenter((SmartBar.Presenter)listener);		
		}
	}

	@Override
	public PortalViewPresenter<T> getPresenter() {
		return listener;
	}


	@Override
	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}


	@Override
	public void showWait() {
		// TODO Auto-generated method stub

	}


	@Override
	public void setComps(ICoreConfiguration result) {
		config = result;
		//if (!timeSeries.isToggled()) {
		comp.clear();
		comp.addItem("All","-1");

		for (Long id: result.getCompetitionMap().keySet()) {
			comp.addItem(result.getCompetitionMap().get(id), id.toString());
		}

		comp.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				rq = null;
				compIds = null;
				roundIds = null;
				teamIds = null;
				round.clear();
				team.clear();
				for (int i=0; i<pos.getItemCount(); i++) {
					pos.setItemSelected(i, false);
				}

				for (int i=0; i<country.getItemCount(); i++) {
					country.setItemSelected(i, false);
				}

				listener.portalViewCompSelected(Long.parseLong(comp.getValue((comp.getSelectedIndex()))));			
			}

		});
		//} else {
			// populate the comp/round multi-pick list
		compAndRound.clear();
		for (Long id: result.getCompetitionMap().keySet()) {
			listener.portalViewCompPopulate(id);
			//compAndRound.addItem(result.getCompetitionMap().get(id), id.toString());
		}
		//}

	}

	@Override
	public void setComp(ICompetition c, boolean populate) {
		if (!populate) {
			currentComp = c;
			if (compIds == null) {
				compIds = new ArrayList<Long>();
			}
			compIds.add(c.getId());
			// select any comps in the current query

			for (int i=0; i<comp.getItemCount(); i++) {
				if (comp.getValue(i).equals(c.getId().toString())) {
					comp.setSelectedIndex(i);
				}
			}


			round.clear();
			round.addItem("All","-1");
			for (IRound r : c.getRounds()) {
				round.addItem(r.getName(),r.getId().toString());
				// select any items in the current query
				if (rq != null) {
					if (rq.getRoundIds().contains(r.getId())) {
						round.setItemSelected(round.getItemCount()-1, true);
					}
				}
			}

			team.clear();
			team.addItem("All","-1");
			for (ITeamGroup t : c.getTeams()) {
				team.addItem(t.getDisplayName(), t.getId().toString());
				// select any items in the current query
				if (rq != null) {
					if (rq.getTeamIds().contains(t.getId())) {
						team.setItemSelected(team.getItemCount()-1, true);
					}
				}
			}
		} else {
			// time series
			for (IRound r : c.getRounds()) {
				compAndRound.addItem(c.getShortName() + "/" + r.getAbbr(),r.getId().toString());
				// select any items in the current query
				if (rq != null) {
					if (rq.getRoundIds().contains(r.getId())) {
						compAndRound.setItemSelected(compAndRound.getItemCount()-1, true);
					}
				}
			}

			//			for (ITeamGroup t : c.getTeams()) {
			//				team.addItem(t.getDisplayName(), t.getId().toString());
			//				// select any items in the current query
			//				if (rq != null) {
			//					if (rq.getTeamIds().contains(t.getId())) {
			//						team.setItemSelected(team.getItemCount()-1, true);
			//					}
			//				}
			//			}
		}


	}

	@Override
	public void setPositions(List<position> result) {
		for (position posi: result) {
			pos.addItem(posi.toString());
		}
	}


	@Override
	public void setCountries(List<ICountry> result) {
		for (ICountry c: result) {
			country.addItem(c.getName(), c.getId().toString());
		}

		// this is the last thing
		isSetup = true;
	}

	@UiHandler("query")
	void onQueryClick(ClickEvent e) {

		populateVals();

		listener.submitPortalQuery(compIds, roundIds, posis, countryIds, teamIds);
	}


	private void populateVals() {
		if (!timeSeries.isToggled()) {
			compIds = new ArrayList<Long>();
			compIds.add(Long.parseLong(comp.getValue(comp.getSelectedIndex())));

			roundIds = new ArrayList<Long>();
			for (int i = 0; i < round.getItemCount(); i++) {
				if (round.isItemSelected(i)) {
					if (!round.getValue(i).equals("-1")) {
						roundIds.add(currentComp.getRounds().get(i-1).getId());
					}
				}
			}
		} else {
			//@REX do we need compIds here too?
			compIds = new ArrayList<Long>();
			roundIds = new ArrayList<Long>();
			for (int i = 0; i < compAndRound.getItemCount(); i++) {
				if (compAndRound.isItemSelected(i)) {
					if (!compAndRound.getValue(i).equals("-1")) {
						roundIds.add(Long.parseLong(compAndRound.getValue(i)));
					}
				}
			}
		}

		posis = new ArrayList<position>();
		for (int i = 0; i < pos.getItemCount(); i++) {
			if (pos.isItemSelected(i)) {
				posis.add(position.getAt(i));
			}
		}	

		countryIds = new ArrayList<Long>();
		for (int i = 0; i < country.getItemCount(); i++) {
			if (country.isItemSelected(i)) {
				countryIds.add(Long.parseLong(country.getValue(i)));
			}
		}	

		teamIds = new ArrayList<Long>();
		for (int i = 0; i < team.getItemCount(); i++) {
			if (team.isItemSelected(i)) {
				teamIds.add(Long.parseLong(team.getValue(i)));
			}
		}	

	}


	@UiHandler("topTen")
	void onTopTenClick(ClickEvent e) {

		populateVals();

		Long rqId = null;
		if (rq != null) {
			rqId = rq.getId();
		}
		
		Long compId = null;
		if (compIds != null && compIds.size() > 0) {
			compId = compIds.get(0);
		}

		TopTenSeedData data = new TopTenSeedData((List<IPlayerRating>)portalList, "", "", compId, roundIds, rqId, playersPerTeam);

		listener.createTopTenList(data);

	}

	@Override
	public void showAggregatedMatchInfo(List<IPlayerMatchInfo> matchInfo) {
		clientFactory.getPlayerListView().setPlayers(matchInfo, null);
		portalList = new ArrayList<IPlayerRating>();
		for (IPlayerMatchInfo pmi : matchInfo) {
			portalList.add(pmi.getMatchRating());
		}
		jobArea.clear();
		jobArea.add(clientFactory.getPlayerListView());
		if (listener instanceof PlayerListView.Listener<?>) {
			clientFactory.getPlayerListView().setListener((Listener<IPlayerMatchInfo>) listener);
		}
		clientFactory.getPlayerListView().asWidget().setVisible(true);
	}

	@Override
	public void showTimeWeightedMatchInfo(List<IPlayerRating> result) {
		portalList = result;
		timeSeries.addStyleName("active");
		clientFactory.getRatingListView().setPlayers(result, null);
		ratingList = result;
		jobArea.clear();
		jobArea.add(clientFactory.getRatingListView());
		if (listener instanceof PlayerListView.Listener<?>) {
			clientFactory.getRatingListView().setListener((Listener<IPlayerRating>) listener);
		}
		clientFactory.getPlayerListView().asWidget().setVisible(true);	}
	
	@Override
	public ICompetition getCurrentComp() {
		return currentComp;
	}


	public void setCurrentComp(ICompetition currentComp) {
		this.currentComp = currentComp;
	}

	@Override
	public IRound getCurrentRound() {
		return currentRound;
	}


	public void setCurrentRound(IRound currentRound) {
		this.currentRound = currentRound;
	}


	@Override
	public void setRatingQuery(IRatingQuery rq) {
		this.rq = rq;

		if (rq != null) {
			if (rq.getRoundIds().size() > 1) {
				setTimeSeries(true);
				// do the rounds
				for (int i=0; i<compAndRound.getItemCount(); i++) {
					if (rq.getRoundIds().contains(Long.parseLong(compAndRound.getValue(i)))) {
						compAndRound.setItemSelected(i, true);
					} else {
						compAndRound.setItemSelected(i, false);
					}
				}
			} else {
				setTimeSeries(false);
				// select the comp
				listener.portalViewCompSelected(rq.getCompIds().get(0));
				// this will call setComp, where we can select the comp(s), round(s) and teams.
			}
			
			// do the positions and countries
			for (int i=0; i<position.values().length; i++) {
				if (rq.getPositions().contains(position.values()[i])) {
					if (pos.getItemCount() >= i-1) {
						pos.setItemSelected(i, true);
					}
				} else {
					if (pos.getItemCount() >= i-1) {
						pos.setItemSelected(i, false);
					}
				}
			}

			for (int i=0; i<country.getItemCount(); i++) {
				if (rq.getCountryIds().contains(Long.parseLong(country.getValue(i)))) {
					country.setItemSelected(i, true);
				} else {
					country.setItemSelected(i, false);
				}
			}
		} else {
			Window.alert("The query you are accessing no longer exists or is invalid.");
		}

	}


	@Override
	public boolean isSetup() {
		return isSetup ;
	}

	@UiHandler("clear")
	void onClearClick(ClickEvent e) {
		clear();
	}

	@UiHandler("delete")
	void onDeleteClick(ClickEvent e) {
		listener.deleteQuery(rq);
	}

	@Override
	public boolean clear() {
		clientFactory.getPlayerListView().clear();

		comp.setSelectedIndex(-1);
		round.clear();
		team.clear();
		for (int i=0; i<country.getItemCount(); i++) {
			country.setItemSelected(i, false);
		}

		for (int i=0; i<pos.getItemCount(); i++) {
			pos.setItemSelected(i, false);
		}

		return true;
	}

	@UiHandler("timeSeries")
	void onTimeSeriesClick(ClickEvent e) {
		setTimeSeries(!timeSeries.isToggled()); 
	}
	
	private void setTimeSeries(boolean timeSeriesOn) {
		if (timeSeriesOn) {
			comp.setVisible(false);
			round.setVisible(false);
			compAndRound.setVisible(true);
			compLabel.setText("Comp/Round");
			roundLabel.setVisible(false);
		} else {
			comp.setVisible(true);
			round.setVisible(true);
			compAndRound.setVisible(false);
			compLabel.setText("Competition");
			roundLabel.setVisible(true);
		}
		
		listener.setTimeSeries(timeSeriesOn);
	}

}

