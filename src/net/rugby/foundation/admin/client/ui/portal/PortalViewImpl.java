/**
 * 
 */
package net.rugby.foundation.admin.client.ui.portal;

import java.util.List;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.ui.SmartBar;
import net.rugby.foundation.admin.client.ui.playerlistview.PlayerListView;
import net.rugby.foundation.admin.client.ui.playerlistview.PlayerListView.Listener;
import net.rugby.foundation.admin.client.ui.playerlistview.PlayerListViewColumnDefinitions;
import net.rugby.foundation.admin.client.ui.playerlistview.PlayerListViewImpl;
import net.rugby.foundation.model.shared.Country;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
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

	@UiField ListBox comp;
    @UiField ListBox round;
    @UiField ListBox pos;
	@UiField ListBox country;
    @UiField ListBox team;

    @UiField Button query;
    @UiField SimplePanel jobArea;
    
	private List<T> PortalList;
	private PortalViewPresenter<T> listener;

	private ClientFactory clientFactory;

	private SmartBar smartBar;

	private List<ICompetition> comps;

	
	public PortalViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));

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
	public void setComps(List<ICompetition> result) {
		this.comps = result;
		for (ICompetition c: result) {
			comp.addItem(c.getLongName(), c.getId().toString());
		}
		
		comp.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				round.clear();
				round.addItem("All","-1");
				for (IRound r : comps.get(comp.getSelectedIndex()).getRounds()) {
					round.addItem(r.getName(),r.getId().toString());
				}
				
				team.clear();
				team.addItem("All","-1");
				for (ITeamGroup t : comps.get(comp.getSelectedIndex()).getTeams()) {
					team.addItem(t.getDisplayName(), t.getId().toString());
				}
				
				//listener.portalViewCompSelected(Long.parseLong(comp.getValue((comp.getSelectedIndex()))));			
			}
			
		});
		
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
		
	}
	
	@UiHandler("query")
	void onQueryClick(ClickEvent e) {
		Long compId = null;
		if (comp.getSelectedIndex() > -1) {
			compId = comps.get(comp.getSelectedIndex()).getId();
		}
		
		Long roundId = null;
		if (round.getSelectedIndex() > -1) {
			if (!round.getValue(round.getSelectedIndex()).equals("-1")) {
				roundId = comps.get(comp.getSelectedIndex()).getRounds().get(round.getSelectedIndex()-1).getId();
			}
		}
		
		position posi = position.NONE;
		if (pos.getSelectedIndex() > -1) {
			posi = position.getAt(pos.getSelectedIndex());
		}		
		
		Long countryId = null;
		if (country.getSelectedIndex() > -1) {
			countryId = Long.parseLong(country.getValue(country.getSelectedIndex()));
		}
		
		Long teamId = null;
		if (team.getSelectedIndex() > -1) {
			teamId = Long.parseLong(team.getValue(team.getSelectedIndex()));
		}
		
		listener.submitPortalQuery(compId, roundId, posi, countryId, teamId);
	}
	
	@Override
	public void showAggregatedMatchInfo(List<IPlayerMatchInfo> matchInfo) {
		clientFactory.getPlayerListView().setPlayers(matchInfo, null);
		jobArea.clear();
		jobArea.add(clientFactory.getPlayerListView());
		if (listener instanceof PlayerListView.Listener<?>) {
			clientFactory.getPlayerListView().setListener((Listener<IPlayerMatchInfo>) listener);
		}
		clientFactory.getPlayerListView().asWidget().setVisible(true);

	}

}

