package net.rugby.foundation.admin.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.place.AdminCompPlace.Filter;
import net.rugby.foundation.admin.client.ui.playerlistview.PlayerListView;
import net.rugby.foundation.admin.client.ui.playerlistview.PlayerListViewColumnDefinitions;
import net.rugby.foundation.admin.client.ui.playerlistview.PlayerListViewImpl;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.WorkflowStatus;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ISimpleScoreMatchResult;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema;
import net.rugby.foundation.model.shared.SimpleScoreMatchResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Sample implementation of {@link AdminView}.
 */
public class CompetitionViewImpl extends Composite implements CompetitionView {

	interface Binder extends UiBinder<Widget, CompetitionViewImpl> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField TextBox url;
	@UiField ListBox resultType;	
	@UiField TextBox weeks;
	@UiField Button save;
	@UiField Button fetch;
	@UiField VerticalPanel treePane;
	@UiField Label status;
	@UiField Tree compTree;
	@UiField SimplePanel editArea;
	@UiField SimplePanel jobArea;
	@UiField Button createAdmin;
	@UiField Button sanityCheck;
	@UiField Button virtualComp;
	@UiField SimplePanel menuBarPanel;

	Presenter listener = null;
	TreeItem base = null;
	TreeItem root = null;
	TreeItem teams = new TreeItem();

	TreeItem matches = new TreeItem();
	TreeItem rounds = new TreeItem();

	Map<String, ITeamGroup> teamMap = null;
	List<IRound> roundMap = null;
	Map<String, IMatchGroup> matchMap = null;
	ICompetition comp = null;
	Map<String, ICompetition> compMap = null;
	EditTeam editTeam = null;
	PlayerListView<IPlayerRating> editMatchStats = null;

	private Step step;

	private ClientFactory clientFactory;

	private boolean isInitialized;

	private SmartBar smartBar;

	private Image waitCursor;

	private ICoreConfiguration config;

	public CompetitionViewImpl() {
		initWidget(binder.createAndBindUi(this));
		url.setText("http://www.espn.co.uk/rugby/fixtures/_/date/20160902/league/267979");

		resultType.clear();
		for (int i=0; i<ICompetition.CompetitionType.values().length; ++i) {
			resultType.addItem(ICompetition.CompetitionType.values()[i].getDisplayName());
			//			if (comp.getCompType() == ICompetition.CompetitionType.values()[i]) {
			//				resultType.setSelectedIndex(i);
			//			}
		}

		teams.setText("Teams");
		matches.setText("Matches");
		rounds.setText("Rounds");

		save.setVisible(false);
		fetch.setText("Fetch teams");
		step = Step.TEAMS;
		isInitialized = false;

	}

	@UiHandler("save")
	void onSaveClick(ClickEvent e) {
		if (step == Step.COMP) {
			listener.saveCompetitionClicked(comp, teamMap);
			status.setText("Done!");
			step = Step.TEAMS;
		} 
		fetch.setEnabled(false);
		fetch.setVisible(true);
		save.setVisible(false);
	}

	@UiHandler("fetch")
	void onFetchClick(ClickEvent e) {	
		CompetitionType compType = CompetitionType.values()[resultType.getSelectedIndex()];
		if (step == Step.TEAMS) {
			listener.fetchTeamsClicked(url.getText(), weeks.getText(), compType);
			step=Step.MATCHES;
			fetch.setText("Fetch Matches");
		} else if (step == Step.MATCHES) {
			listener.fetchMatchesClicked(teamMap, null, Integer.parseInt(weeks.getText()), 0, compType);
			step=Step.ROUNDS;
			fetch.setText("Fetch Rounds");
		} else if (step == Step.ROUNDS) {
			listener.fetchRoundsClicked(matchMap, compType);
			step=Step.COMP;
			fetch.setText("Fetch Comp");
		} else if (step == Step.COMP) {
			listener.fetchCompetitionClicked(roundMap, compType);
			save.setText("Save Comp");
			save.setVisible(true);
			fetch.setVisible(false);
			return;
		} 
		save.setEnabled(false);
		save.setVisible(false);
		fetch.setVisible(true);
		fetch.setEnabled(false);
	}

	@UiHandler("createAdmin")
	void onCreateAdminClick(ClickEvent e) {

		listener.createAdminClicked();
	}

	@UiHandler("sanityCheck")
	void onsanityCheckClick(ClickEvent e) {

		listener.sanityCheckClicked();
	}

	@UiHandler("virtualComp")
	void onVirtualCompClick(ClickEvent e) {

		listener.virtualCompClicked();
	}

	@Override
	public void setPresenter(final Presenter listener) {
		this.listener = listener;


		if (listener instanceof SmartBar.Presenter) {
			if (!menuBarPanel.getElement().hasChildNodes()) {
				smartBar = clientFactory.getMenuBar();
				menuBarPanel.add(smartBar);
			}
			smartBar.setPresenter((SmartBar.Presenter)listener);	
		}	

		if (listener instanceof SmartBar.SchemaPresenter) {
			smartBar.setSchemaPresenter((SmartBar.SchemaPresenter)listener);	
		}

		compTree.addSelectionHandler( new SelectionHandler<TreeItem>() {

			private EditComp editComp = null;
			private EditMatch editMatch = null;
			private EditRound editRound = null;
			private List<ColumnDefinition<IPlayerRating>> playerListViewColumnDefinitions;

			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				if (event != null && event.getSelectedItem() != null) {
					if (event.getSelectedItem().equals(teams)) {
						//listener.teamsClicked();
					} else if (event.getSelectedItem().equals(rounds)) {
						//listener.roundsClicked();					
					} else if (event.getSelectedItem().getParentItem().equals(rounds)) {
						//listener.roundClicked(event.getSelectedItem().getText());
					} else {
						//see how far down we are
						int depth = 0;
						TreeItem cursor = event.getSelectedItem();
						while (!cursor.equals(base)) {
							depth++;
							cursor = cursor.getParentItem();
						}
						if (depth == 1) {  //comp clicked
							if (event.getSelectedItem().getText().equals("Competition")) {
								// they clicked on the importer "Competition" - should say "Import" or something
							}	else {
								editArea.clear();
								if (editComp == null) {
									editComp = new EditComp();								
								}

								editArea.add(editComp);
								editComp.setVisible(true);
								listener.compClicked(editComp, Long.parseLong(event.getSelectedItem().getText().split("\\|")[1]));
							}							
						} else if (depth == 2) {  //teams or rounds clicked
													if (event.getSelectedItem().getText().equals("teams")) {
														listener.teamsClicked(Long.parseLong(event.getSelectedItem().getParentItem().getText().split("\\|")[1]));
													}	else {
														listener.roundsClicked(Long.parseLong(event.getSelectedItem().getParentItem().getText().split("\\|")[1]));
													}							
						} else if (depth == 3) { // specific team or round clicked
							if (event.getSelectedItem().getParentItem().getText().equals("teams")) {
								editArea.clear();
								if (editTeam == null) {
									editTeam = new EditTeam();
								}
								editArea.add(editTeam);
								editTeam.setVisible(true);

								Long compId = Long.parseLong(event.getSelectedItem().getParentItem().getParentItem().getText().split("\\|")[1]);
								Long teamId = Long.parseLong(event.getSelectedItem().getText().split("\\|")[1]);
								listener.editTeamInit(editTeam, teamId, compId );
							} else { // round, so show matches
								// params are compId and roundId
								editArea.clear();
								if (editRound == null) {
									editRound = new EditRound();
								}
								editArea.add(editRound);
								editRound.setVisible(true);

								listener.roundClicked(editRound, Long.parseLong(event.getSelectedItem().getParentItem().getParentItem().getText().split("\\|")[1]),
										Long.parseLong(event.getSelectedItem().getText().split("\\|")[1]));
							}
						} else if (depth == 4) { // match clicked
							editArea.clear();

							if (editMatch == null) {
								editMatch = new EditMatch();
							}
							editArea.add(editMatch);
							editMatch.setVisible(true);

							if (editMatchStats == null) {
								editMatchStats = new PlayerListViewImpl<IPlayerRating>();
								if (playerListViewColumnDefinitions == null) {
									PlayerListViewColumnDefinitions<?> plvcd =  new PlayerListViewColumnDefinitions<IPlayerRating>();
									playerListViewColumnDefinitions = plvcd.getColumnDefinitions();
								}

								editMatchStats.setColumnDefinitions(playerListViewColumnDefinitions);
								editMatchStats.setColumnHeaders(PlayerListViewColumnDefinitions.getHeaders());

								//							editMatchStats = clientFactory.getPlayerListView();

								jobArea.add(editMatchStats);
								editMatchStats.asWidget().setVisible(true);

							}

							editMatchStats.showWait();

							// find roundId and compId
							Long roundId = Long.parseLong(event.getSelectedItem().getParentItem().getText().split("\\|")[1]);
							Long compId = Long.parseLong(event.getSelectedItem().getParentItem().getParentItem().getParentItem().getText().split("\\|")[1]);
							Long matchId = Long.parseLong(event.getSelectedItem().getText().split("\\|")[1]);
							listener.editMatchInit(editMatch, editMatchStats, matchId, roundId, compId);

						} 
					}
				}
			}
		});


	}


	@Override
	public void showTeams(Map<String, ITeamGroup> tgs) {
		teamMap = tgs;
		teams.removeItems();
		for (String tg : tgs.keySet()) {
			teams.addTextItem(tg);
			if (tgs.get(tg).getId() == null) {
				teams.getChild(teams.getChildCount()-1).getElement().addClassName("gnu");
			}

		}
		fetch.setEnabled(true);
	}

	@Override
	public void showRounds(List<IRound>  rds) {
		roundMap = rds;
		rounds.removeItems();
		fetch.setEnabled(true);
	}


	@Override
	public void showStatus(String message) {
		status.setText(message);
		status.setStylePrimaryName("error");		
	}


	@Override
	public void showMatches(Map<String, IMatchGroup> mgs) {
		if (mgs == null) {
			status.setText("No matches found");
		} else {

			matchMap = mgs;
			matches.removeItems();
			for (IMatchGroup m : mgs.values()) {
				matches.addTextItem(m.getDisplayName());
				if (m.getId() == null) {
					matches.getChild(matches.getChildCount()-1).getElement().addClassName("gnu");
				}
			}		
			fetch.setEnabled(true);
		}
	}

	@Override
	public void addMatches(Map<String, IMatchGroup> mgs) {
		if (matchMap == null) {
			matchMap = new HashMap<String, IMatchGroup>();
		}
		matchMap.putAll(mgs);

		for (IMatchGroup m : matchMap.values()) {
			matches.addTextItem(m.getDisplayName());
			if (m.getId() == null) {
				matches.getChild(matches.getChildCount()-1).getElement().addClassName("gnu");
			}
		}		
	}

	/**
	 * when the user clicks Fetch Competition button, the server goes and gets the comp info 
	 * and the activity calls this method from it's onSuccess.
	 */
	@Override
	public void showCompetition(ICompetition result) {
		comp = result;
		String longName = result.getLongName() == null ? "Change me" : result.getLongName();
		root.setText(longName + "|" + result.getId());

		addRounds(result, result.getRounds());
		for (IRound r: result.getRounds()) {
			addRound(comp.getId(),r.getId(),r.getMatches());
		}
		save.setEnabled(true);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.CompetitionView#addComps(com.google.gwt.user.client.ui.TreeItem, java.util.ArrayList)
	 */
	@Override
	public void addComps(List<ICompetition> result) {

		try {
			Notify.notify("Don't call addComps please!", NotifyType.DANGER);
			compTree.removeItems();
			base = new TreeItem();
			base.setText("Competitions");
			root = new TreeItem();
			root.setText("Competition");
			compTree.addItem(base);
			base.addItem(root);
			root.addItem(teams);
			root.addItem(rounds);	
			root.addItem(matches);

			if (compMap == null) {
				compMap = new HashMap<String,ICompetition>();
			}
			for (ICompetition c : result) {
				compMap.put(c.getId().toString(),c);

				TreeItem ti = base.addTextItem(c.getLongName() + "|" + c.getId());
				ti.addTextItem("teams");
				addTeams(c.getId(), c.getTeams());
				ti.addTextItem("rounds");
				addRounds(c,c.getRounds());
				for (IRound r : c.getRounds()) {
					try {
						addRound(c.getId(),r.getId(),r.getMatches());
						for (IMatchGroup m: r.getMatches()) {
							try {
								List<IMatchResult> results = new ArrayList<IMatchResult>();
								ISimpleScoreMatchResult score = m.getSimpleScoreMatchResult();
								if (score != null) {
									results.add((IMatchResult) m.getSimpleScoreMatchResult());
								}
								if (!results.isEmpty()) {
									addResults(c.getId(), r, m, results);
								}
							} catch (Throwable ex) {
								clientFactory.console("Problem with match " + m.getDisplayName());		
							}
						}
					} catch (Throwable ex) {
						clientFactory.console("Problem with Round " + r.getName());		
					}
				}
			}
		} catch (Throwable ex) {
			clientFactory.console("CompViewImpl:AddComps " + ex.getMessage());		
		}
		showWait(false);
		isInitialized = true;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.CompetitionView#addRounds(long, java.util.ArrayList)
	 */
	@Override
	public void addRounds(final ICompetition comp, final List<IRound> result) {
		int i = 0;
		while (i < base.getChildCount()) {

			if (base.getChild(i).getText().split("\\|")[0].equals(comp.getLongName())) {
				base.getChild(i).getChild(1).removeItems();
				for (IRound r : result) {
					final IRound rd = r;
					final int id = i;
					Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
						public void execute() {
							try {
								base.getChild(id).getChild(1).addTextItem(rd.getName() + " (" + rd.getBegin().toString() +") |" + rd.getId());
							} catch (Throwable ex) {
								clientFactory.console("CompViewImpl:AddRounds " + ex.getMessage());		
							}
						}
					});
				}
				break;
			}
			++i;

		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.CompetitionView#addTeams(long, java.util.ArrayList)
	 */
	@Override
	public void addTeams(Long compId, List<ITeamGroup> result) {
		int i = 0;
		while (i < base.getChildCount()) {
			if (base.getChild(i).getText().contains(compId.toString())) {
				base.getChild(i).getChild(0).removeItems();
				for (ITeamGroup tg : result) {
					base.getChild(i).getChild(0).addTextItem(tg.getDisplayName() + "|" + tg.getId());
				}
				break;
			}
			++i;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.CompetitionView#addRound(java.lang.Long, java.lang.Long, java.util.ArrayList)
	 */
	@Override
	public void addRound(Long compId, Long roundId, List<IMatchGroup> result) {
		int i = 0;
		int j = 0;
		String cid = "null";
		if (compId != null) {
			cid = compId.toString();
		}
		try {
			while (i < base.getChildCount()) {
				if (base.getChild(i).getText().contains(cid)) {
					while (j < base.getChild(i).getChild(1).getChildCount()) {
						String rid = "null";
						if (roundId != null)
							rid = roundId.toString().toString();
						TreeItem round = base.getChild(i).getChild(1).getChild(j);
						if (round.getText().contains(rid)) {
							round.removeItems();
							for (IMatchGroup mg : result) {
								Div div  = new Div();
								Icon icon = new Icon();
								icon.setType(IconType.CIRCLE);
								if (mg.getWorkflowStatus() == null || mg.getWorkflowStatus().ordinal() < WorkflowStatus.LINEUPS.ordinal()) {
									icon.setColor("lightgrey");
								} else if (mg.getWorkflowStatus() == (WorkflowStatus.LINEUPS)) {
									icon.setColor("darkgrey");
								} else if (mg.getWorkflowStatus() == (WorkflowStatus.UNDERWAY)) {
									icon.setColor("magenta");
								} else if (mg.getWorkflowStatus() == (WorkflowStatus.FINAL)) {
									icon.setColor("lightblue");
								} else if (mg.getWorkflowStatus() == (WorkflowStatus.FETCHED)) {
									icon.setColor("blue");
								} else if (mg.getWorkflowStatus() == (WorkflowStatus.RATED)) {
									icon.setColor("lightgreen");
								} else if (mg.getWorkflowStatus() == (WorkflowStatus.PROMOTED)) {
									icon.setColor("green");
								} else if (mg.getWorkflowStatus() == (WorkflowStatus.ERROR)) {
									icon.setColor("red");
								} else if (mg.getWorkflowStatus().equals(WorkflowStatus.NO_STATS)) {
									icon.setColor("black");
								}
								div.add(icon.asWidget());
								Span span = new Span(mg.getDisplayName() + "|" + mg.getId());
								div.add(span);
								TreeItem ti = round.addItem(div);
								//TreeItem ti = round.addTextItem(mg.getDisplayName() + "| " + mg.getId());
								ti.addTextItem(mg.getDate().toString());
								if (mg.getLocked() != null) {
									if (mg.getLocked()) {
										ti.addTextItem("Locked");
									} else {
										ti.addTextItem("Not Locked");									
									}
								} else 
									ti.addTextItem("Not Locked");
								ti.addTextItem("results");
							}
							break;
						}
						++j;
					}
					break;
				}
				++i;
			}

		} catch (Throwable ex) {
			clientFactory.console("CompViewImpl:AddRound " + ex.getMessage());		
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.CompetitionView#addResults(long, java.util.ArrayList)
	 */
	@Override
	public void addResults(Long compId, IRound round, IMatchGroup match, List<IMatchResult> result) {
		int i = 0;
		int j = 0;
		int k = 0;
		while (i < base.getChildCount()) {
			if (base.getChild(i).getText().contains(compId.toString())) {
				while (j < base.getChild(i).getChild(1).getChildCount()) {
					if (base.getChild(i).getChild(1).getChild(j).getText().contains(round.getId().toString()) && base.getChild(i).getChild(1).getChild(j).getText().contains(round.getName())) {
						while (k < base.getChild(i).getChild(1).getChild(j).getChildCount()) {
							if (base.getChild(i).getChild(1).getChild(j).getChild(k).getText().contains(match.getId().toString()) && base.getChild(i).getChild(1).getChild(j).getChild(k).getText().contains(match.getDisplayName())) {
								for (IMatchResult mr: result) {
									TreeItem ti = base.getChild(i).getChild(1).getChild(j).getChild(k).getChild(2).addTextItem(mr.getRecordedDate().toString());
									if (mr instanceof SimpleScoreMatchResult) {
										ti.addTextItem(((ISimpleScoreMatchResult)mr).getHomeScore() + " - " + ((ISimpleScoreMatchResult)mr).getVisitScore());
									}
								}
								break;
							}
							++k;
						}
						break;
					}
					++j;
				}
				break;
			}
			++i;
		}

	}

	@Override
	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public PlayerListView<IPlayerRating> getPlayerListView() {
		return editMatchStats;
	}

	@Override
	public boolean isAllSetup() {
		return isInitialized;
	}

	@Override
	public void setInitialized(boolean b) {
		isInitialized = b;

	}

	@Override
	public void showWait(boolean show) {
		if (show) {
			compTree.removeItems();	
			if (waitCursor == null) {
				waitCursor = new Image("/resources/images/ajax-loader.gif");
			}
			treePane.add(waitCursor); //new HTML("Stand by...")); //
		} else {
			if (waitCursor != null) {
				if (waitCursor.isAttached()) {
					treePane.remove(waitCursor);
				}
			}
		}

	}

	@Override
	public void setSchemaList(List<ScrumMatchRatingEngineSchema> result) {
		smartBar.setSchemas(result);

	}


	@Override
	public SmartBar getSmartBar() {
		return smartBar;
	}

	@Override
	public void addCompNames(ICoreConfiguration result, Filter filter) {
		try {
			config = result;
			compTree.removeItems();
			base = new TreeItem();
			base.setText("Competitions");
			root = new TreeItem();
			root.setText("Competition");
			compTree.addItem(base);
			base.addItem(root);
			root.addItem(teams);
			root.addItem(rounds);	
			root.addItem(matches);

			if (compMap == null) {
				compMap = new HashMap<String,ICompetition>();
			}
			
			List<Long> list = null;
			
			if (filter == Filter.ALL) {
				list = result.getAllComps();
			} else if (filter == Filter.UNDERWAY) {
				list = result.getCompsUnderway();
			} else if (filter == Filter.CLIENT) {
				list = result.getCompsForClient();
			}
 			for (Long cid : list) {

				TreeItem ti = base.addTextItem(config.getCompetitionMap().get(cid) + "|" + cid);
				ti.addTextItem("teams");
				//addTeams(c.getId(), c.getTeams());
				ti.addTextItem("rounds");
				//addRounds(c,c.getRounds());
//				for (IRound r : c.getRounds()) {
//					try {
//						addRound(c.getId(),r.getId(),r.getMatches());
//						for (IMatchGroup m: r.getMatches()) {
//							try {
//								List<IMatchResult> results = new ArrayList<IMatchResult>();
//								ISimpleScoreMatchResult score = m.getSimpleScoreMatchResult();
//								if (score != null) {
//									results.add((IMatchResult) m.getSimpleScoreMatchResult());
//								}
//								if (!results.isEmpty()) {
//									addResults(c.getId(), r, m, results);
//								}
//							} catch (Throwable ex) {
//								clientFactory.console("Problem with match " + m.getDisplayName());		
//							}
//						}
//					} catch (Throwable ex) {
//						clientFactory.console("Problem with Round " + r.getName());		
//					}
//				}
			}
		} catch (Throwable ex) {
			clientFactory.console("CompViewImpl:AddComps " + ex.getMessage());		
		}
		showWait(false);
		isInitialized = true;
	}
		
	
}
