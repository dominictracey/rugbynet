package net.rugby.foundation.admin.client.activity;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DialogBox;
import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.place.AdminCompPlace.Filter;
import net.rugby.foundation.admin.client.place.PortalPlace;
import net.rugby.foundation.admin.client.ui.AdminView;
import net.rugby.foundation.admin.client.ui.SmartBar;
import net.rugby.foundation.admin.client.ui.playerlistview.PlayerListView;
import net.rugby.foundation.admin.client.ui.playermatchstatspopup.PlayerMatchStatsPopupView.PlayerMatchStatsPopupViewPresenter;
import net.rugby.foundation.admin.client.ui.playerpopup.PlayerPopupView;
import net.rugby.foundation.admin.client.ui.portal.EditTTIText;
import net.rugby.foundation.admin.client.ui.portal.EditTTIText.EditTTITextPresenter;
import net.rugby.foundation.admin.client.ui.portal.PortalView;
import net.rugby.foundation.admin.client.ui.portal.PortalView.PortalViewPresenter;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.Position.position;

public class PortalActivity extends AbstractActivity implements  
AdminView.Presenter, PlayerPopupView.Presenter<IPlayer>, SmartBar.Presenter,
PlayerMatchStatsPopupViewPresenter<IPlayerMatchStats>, PortalViewPresenter<IPlayerMatchInfo>,
PlayerListView.Listener<IPlayerMatchInfo>, EditTTITextPresenter { 
	/**
	 * Used to obtain views, eventBus, placeController.
	 * Alternatively, could be injected via GIN.
	 */
	private ClientFactory clientFactory;
	private PortalPlace place;
	SelectionModel<IPlayerMatchInfo> selectionModel;
	int index; // the task line item number
	private PortalView<IPlayerMatchInfo> view;
	private EditTTIText ttltext;

	public PortalActivity(PortalPlace place, ClientFactory clientFactory) {
		selectionModel = new SelectionModel<IPlayerMatchInfo>();

		this.clientFactory = clientFactory;
		view = clientFactory.getPortalView();
		// Select the tab corresponding to the token value
		if (place.getToken() != null) {
			//			view.selectTab(2, false);

			this.place = place;
		}
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {

		view.setPresenter(this);
		panel.setWidget(view.asWidget());

		if (place != null) {
			clientFactory.getRpcService().getComps(Filter.ALL, new AsyncCallback<List<ICompetition>>() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Failed to fetch comps");
				}

				@Override
				public void onSuccess(List<ICompetition> result) {
					view.setComps(result);

					clientFactory.getRpcService().fetchPositionList(new AsyncCallback<List<position>>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Failed to fetch position list");
							
						}

						@Override
						public void onSuccess(List<position> result) {
							view.setPositions(result);
							
							clientFactory.getRpcService().fetchCountryList(new AsyncCallback<List<ICountry>>() {

								@Override
								public void onFailure(Throwable caught) {
									Window.alert("Failed to fetch country list");
									
								}

								@Override
								public void onSuccess(List<ICountry> result) {
									view.setCountries(result);
									
								}
								
							});
							
						}
						
					});
					
				}
			});

		}
	}





	@Override
	public ClientFactory getClientFactory() {
		return clientFactory;
	}


	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}


	@Override
	public void compPicked(Long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showPlayerPopup(IPlayerMatchStats target) {
		clientFactory.getPlayerPopupView().setPresenter(this);
		clientFactory.getRpcService().getPlayer(target.getPlayerId(), new AsyncCallback<IPlayer>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to fetch player to edit");
			}

			@Override
			public void onSuccess(IPlayer result) {
				clientFactory.getPlayerPopupView().setPlayer(result);
				((DialogBox)clientFactory.getPlayerPopupView()).center();
			}
		});

	}

	@Override
	public void onRefetchEditPlayerMatchStatsClicked(IPlayerMatchStats target) {
		clientFactory.getPlayerMatchStatsPopupView().setPresenter(this);

		clientFactory.getRpcService().refetchPlayerMatchStats(target, new AsyncCallback<IPlayerMatchStats>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to fetch playerMatchStats, see logs for details");
			}

			@Override
			public void onSuccess(IPlayerMatchStats result) {
				clientFactory.getPlayerMatchStatsPopupView().setTarget(result);
				((DialogBox)clientFactory.getPlayerMatchStatsPopupView()).center();
			}
		});

	}

	@Override
	public void onSavePlayerMatchStatsClicked(final IPlayerMatchStats pms) {
		clientFactory.getPlayerMatchStatsPopupView().setPresenter(this);

		clientFactory.getRpcService().savePlayerMatchStats(pms, null, new AsyncCallback<IPlayerMatchInfo>() {

			@Override
			public void onFailure(Throwable caught) {

				Window.alert("Player Stats not saved: " + caught.getMessage());
			}

			@Override
			public void onSuccess(IPlayerMatchInfo result) {

				clientFactory.getPlayerListView().updatePlayerMatchStats(result);
				((DialogBox) clientFactory.getPlayerMatchStatsPopupView()).hide();

			}
		});			
	}

	@Override
	public void onCancelEditPlayerMatchStatsClicked() {
		((DialogBox)clientFactory.getPlayerMatchStatsPopupView()).hide();
		
	}

	@Override
	public void onSaveEditPlayerClicked(IPlayer player) {

		clientFactory.getRpcService().savePlayer(player, null, new AsyncCallback<IPlayer>() {

			@Override
			public void onFailure(Throwable caught) {

				Window.alert("Player not saved: " + caught.getMessage());
			}

			@Override
			public void onSuccess(IPlayer result) {

				Window.alert("Player saved");
				((DialogBox) clientFactory.getPlayerPopupView()).hide();


			}
		});		


	}

	@Override
	public void onCancelEditPlayerClicked() {
		((DialogBox)clientFactory.getPlayerPopupView()).hide();
		
	}

	@Override
	public void submitPortalQuery(Long compId, Long roundId, position posi,
			Long countryId, Long teamId) {
		clientFactory.getRpcService().aggregatePlayerMatchRatings(compId, roundId, posi,
				countryId, teamId, new AsyncCallback<List<IPlayerMatchInfo>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(List<IPlayerMatchInfo> result) {
						clientFactory.getPortalView().showAggregatedMatchInfo(result);
						
					}
		});

		
	}

	@Override
	public boolean onItemSelected(IPlayerMatchInfo c) {
		if (selectionModel.isSelected(c)) {
			selectionModel.removeSelection(c);
		}

		else {
			selectionModel.addSelection(c);
		}

		return true;
	}

	@Override
	public boolean isSelected(IPlayerMatchInfo c) {
		return selectionModel.isSelected(c);
	}

	@Override
	public void showEditPlayer(IPlayerMatchInfo player) {
		clientFactory.getPlayerPopupView().setPresenter(this);

		clientFactory.getRpcService().getPlayer(player.getPlayerMatchStats().getPlayerId(), new AsyncCallback<IPlayer>() {

			@Override
			public void onFailure(Throwable caught) {

				Window.alert("Player info not fetched for editing: " + caught.getMessage());
			}

			@Override
			public void onSuccess(IPlayer result) {
				clientFactory.getPlayerPopupView().setPlayer(result);
				((DialogBox) clientFactory.getPlayerPopupView()).center();
			}
		});	


	}

	@Override
	public void showEditStats(IPlayerMatchInfo info) {
		clientFactory.getPlayerMatchStatsPopupView().setPresenter(this);

		clientFactory.getPlayerMatchStatsPopupView().setTarget(info.getPlayerMatchStats());
		((DialogBox) clientFactory.getPlayerMatchStatsPopupView()).center();
	}

	@Override
	public void showEditRating(IPlayerMatchInfo player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void flushAllPipelineJobs() {
		clientFactory.flushAllPipelineJobs();
		
	}

	@Override
	public void createTopTenList(TopTenSeedData data) {
		if (ttltext == null) {
			ttltext = new EditTTIText();
		}

		ttltext.setText("Top Ten List Properties");
		data.setTitle("Top Ten Performances for " + view.getCurrentComp().getShortName() + " - " + view.getCurrentRound().getName());
		ttltext.setPresenter(this);
		ttltext.showTTI(data);
	}

	@Override
	public void saveTTIText(final TopTenSeedData tti) {
		ttltext.hide();
		clientFactory.getRpcService().createTopTenList(tti, new AsyncCallback<TopTenSeedData>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Problem creating top ten list: " + caught.getMessage());
			}

			@Override
			public void onSuccess(TopTenSeedData result) {
				if (result != null) {
					Window.alert("Top ten list created: " + result.getTitle());
				} else {
					Window.alert("Problem creating top ten list for " + tti.getTitle());
				}
			}
		});	
		
	}

	@Override
	public void cancelTTITextEdit(TopTenSeedData tti) {
		ttltext.hide();
		
	}

	@Override
	public void createContent() {
		clientFactory.createContent();
	}
	

}
