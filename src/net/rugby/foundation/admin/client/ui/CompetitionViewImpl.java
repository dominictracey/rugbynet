package net.rugby.foundation.admin.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ISimpleScoreMatchResult;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.SimpleScoreMatchResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
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
	@UiField Button save;
	@UiField Button fetch;
//	@UiField Button load;
	@UiField Label status;
	@UiField Tree compTree;
	@UiField SimplePanel editArea;
	@UiField SimplePanel jobArea;
	@UiField Button createAdmin;
	@UiField Button sanityCheck;
	
	Presenter listener = null;
	TreeItem base = null;
	TreeItem root = null;
	TreeItem teams = new TreeItem("Teams");
	TreeItem matches = new TreeItem("Matches");
	TreeItem rounds = new TreeItem("Rounds");
	
	Map<String, ITeamGroup> teamMap = null;
	List<IRound> roundMap = null;
	Map<String, IMatchGroup> matchMap = null;
	ICompetition comp = null;
	Map<String, ICompetition> compMap = null;
	EditTeam editTeam = null;
	
	private Step step;

	public CompetitionViewImpl() {
		initWidget(binder.createAndBindUi(this));
		url.setText("http://www.espnscrum.com/super-rugby-2012/rugby/series/144545.html");
		resultType.addItem("basicMatchResult");
		save.setVisible(false);
		fetch.setText("Fetch teams");
		step = Step.TEAMS;
	}

//	@UiHandler("load")
//	void onLoadClick(ClickEvent e)  {
//		listener.loadCompsClicked(base);
//	}
 	
	@UiHandler("save")
	void onSaveClick(ClickEvent e) {
		if (step == Step.TEAMS) {
			listener.saveTeamsClicked(teamMap);
			fetch.setText("Fetch Matches");		
			status.setText("Step 1a. Fetch Teams");
			step=Step.MATCHES;
		} else if (step == Step.MATCHES) {
			listener.saveMatchesClicked(matchMap);
			fetch.setText("Fetch Rounds");	
			status.setText("Step 2a. Fetch Matches");
			step = Step.ROUNDS;
		} else if (step == Step.ROUNDS) {
			listener.saveRoundsClicked(roundMap);
			fetch.setText("Fetch Comp");	
			status.setText("Step 3a. Fetch Rounds");
			step = Step.COMP;
		} else if (step == Step.COMP) {
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
		if (step == Step.TEAMS) {
			listener.fetchTeamsClicked(url.getText(), resultType.getItemText(0));
			save.setText("Save Teams");
			status.setText("Step 1b. Save Teams");
		} else if (step == Step.MATCHES) {
			listener.fetchMatchesClicked(teamMap);
			save.setText("Save Matches");
			status.setText("Step 2b. Save Matches");
		} else if (step == Step.ROUNDS) {
			listener.fetchRoundsClicked(matchMap);
			save.setText("Save Rounds");
			status.setText("Step 3b. Save Rounds");
		} else if (step == Step.COMP) {
			listener.fetchCompetitionClicked(roundMap);
			save.setText("Save Comp");
			status.setText("Step 4b. Save Comp");
		} 
		save.setEnabled(false);
		fetch.setVisible(false);
		save.setVisible(true);
	}

	@UiHandler("createAdmin")
	void onCreateAdminClick(ClickEvent e) {
		
		listener.createAdminClicked();
	}
	
	@UiHandler("sanityCheck")
	void onsanityCheckClick(ClickEvent e) {
		
		listener.sanityCheckClicked();
	}
	
	@Override
	public void setPresenter(final Presenter listener) {
		this.listener = listener;
		compTree.addSelectionHandler( new SelectionHandler<TreeItem>() {

			private EditComp editComp = null;
			private EditMatch editMatch = null;

			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
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
							if (editComp == null) {
								editComp = new EditComp();								
								editArea.add(editComp);
							}
							if (editTeam != null)
								editTeam = null;
							if (editMatch != null)
								editMatch = null;
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
							if (editTeam == null) {
								editTeam = new EditTeam();
								editArea.add(editTeam);
							}
							editTeam.setVisible(true);
							if (editComp != null)
								editComp = null;
							if (editMatch != null)
								editMatch = null;
							
							Long compId = Long.parseLong(event.getSelectedItem().getParentItem().getParentItem().getText().split("\\|")[1]);
							Long teamId = Long.parseLong(event.getSelectedItem().getText().split("\\|")[1]);
							listener.editTeamInit(editTeam, teamId, compId );
						} else { // round, so show matches
							// params are compId and roundId
							listener.roundClicked(Long.parseLong(event.getSelectedItem().getParentItem().getParentItem().getText().split("\\|")[1]),
									Long.parseLong(event.getSelectedItem().getText().split("\\|")[1]));
						}
					} else if (depth == 4) { // match clicked
						if (editComp != null) 
							editComp = null;
						if (editTeam != null)
							editTeam = null;
						
						if (editMatch == null) {
							editMatch = new EditMatch();
							editArea.clear();
							editArea.add(editMatch);
						}
						editMatch.setVisible(true);

						
						// find roundId and compId
						Long roundId = Long.parseLong(event.getSelectedItem().getParentItem().getText().split("\\|")[1]);
						Long compId = Long.parseLong(event.getSelectedItem().getParentItem().getParentItem().getParentItem().getText().split("\\|")[1]);
						Long matchId = Long.parseLong(event.getSelectedItem().getText().split("\\|")[1]);
						listener.editMatchInit(editMatch, matchId, roundId, compId);

					} else if (depth == 5) { // results?
						if (event.getSelectedItem().getText().equals("results")) {
							listener.resultsClicked(
									Long.parseLong(event.getSelectedItem().getParentItem().getParentItem().getParentItem().getParentItem().getText().split("\\|")[1]),  // compId
									Long.parseLong(event.getSelectedItem().getParentItem().getParentItem().getText().split("\\|")[1]),  // roundId
									Long.parseLong(event.getSelectedItem().getParentItem().getText().split("\\|")[1]));  //matchId
						}
					}
				}
			}
		});
		
//		compTree.addOpenHandler( new OpenHandler<TreeItem>() {
//
//			@Override
//			public void onOpen(OpenEvent<TreeItem> event) {
//				//see how far down we are
//				int depth = 0;
//				TreeItem cursor = event.getTarget();
//				while (!cursor.equals(base)) {
//					depth++;
//					cursor = cursor.getParentItem();
//				}
//				if (depth == 1) {  //comp clicked
//					if (event.getTarget().getText().equals("Competition")) {
//						// they clicked on the importer "Competition" - should say "Import" or something
//						listener.setCurrentCompId(0L);
//					}	else {
//						listener.setCurrentCompId(Long.parseLong(event.getTarget().getParentItem().getText().split("\\|")[1]));
//					}							
//				} else if (depth == 2) {  //teams or rounds clicked
//					if (event.getTarget().getText().equals("teams")) {
//						listener.setTeamId(0L); // I don't think the importer has the Ids yet
//						listener.setCurrentCompId(0L);
//					}	else {
//						listener.setRoundId(Long.parseLong(event.getTarget().getParentItem().getText().split("\\|")[1]));
//					}							
//				} else if (depth == 3) { // specific team or round clicked
//					if (event.getSelectedItem().getParentItem().getText().equals("teams")) {
//						if (editTeam == null) {
//							editTeam = new EditTeam();
//							editArea.add(editTeam);
//						}
//						editTeam.setVisible(true);
//						if (editComp != null)
//							editComp = null;
//						if (editMatch != null)
//							editMatch = null;
//						listener.editTeamInit(editTeam, Long.parseLong(event.getSelectedItem().getText().split("\\|")[1]));
//					} else { // round, so show matches
//						// params are compId and roundId
//						listener.roundClicked(Long.parseLong(event.getSelectedItem().getParentItem().getParentItem().getText().split("\\|")[1]),
//								Long.parseLong(event.getSelectedItem().getText().split("\\|")[1]));
//					}
//				} else if (depth == 4) { // match clicked
//					if (editMatch == null) {
//						editMatch = new EditMatch();
//						editArea.add(editMatch);
//					}
//					editMatch.setVisible(true);
//					if (editComp != null)
//						editComp.setVisible(false);
//					if (editTeam != null)
//						editTeam.setVisible(false);
//					listener.editMatchInit(editMatch, Long.parseLong(event.getSelectedItem().getText().split("\\|")[1]));
//
//				} else if (depth == 5) { // results?
//					if (event.getSelectedItem().getText().equals("results")) {
//						listener.resultsClicked(
//								Long.parseLong(event.getSelectedItem().getParentItem().getParentItem().getParentItem().getParentItem().getText().split("\\|")[1]),  // compId
//								Long.parseLong(event.getSelectedItem().getParentItem().getParentItem().getText().split("\\|")[1]),  // roundId
//								Long.parseLong(event.getSelectedItem().getParentItem().getText().split("\\|")[1]));  //matchId
//					}
//				}			
//			}
//		});
			
		compTree.removeItems();
		base = new TreeItem("Competitions");
		root = new TreeItem("Competition");
		compTree.addItem(base);
		base.addItem(root);
		root.addItem(teams);
		root.addItem(rounds);	
		root.addItem(matches);
	}


	@Override
	public void showTeams(Map<String, ITeamGroup> tgs) {
		teamMap = tgs;
		teams.removeItems();
		for (String tg : tgs.keySet()) {
			teams.addItem(tg);
			if (tgs.get(tg).getId() == null) {
				teams.getChild(teams.getChildCount()-1).getElement().addClassName("gnu");
			}
			
		}
		save.setEnabled(true);
		fetch.setEnabled(true);
	}

	@Override
	public void showRounds(List<IRound>  rds) {
		roundMap = rds;
		rounds.removeItems();
//		for (IRound r : rds) {
//			rounds.addItem(r.getName());
//		}
		save.setEnabled(true);
		fetch.setEnabled(true);
	}


	@Override
	public void showStatus(String message) {
		status.setText(message);
		status.setStylePrimaryName("error");		
	}


	@Override
	public void showMatches(Map<String, IMatchGroup> mgs) {
		matchMap = mgs;
		matches.removeItems();
		for (IMatchGroup m : mgs.values()) {
			matches.addItem(m.getDisplayName());
			if (mgs.get(m.getDisplayName()).getId() == null) {
				matches.getChild(matches.getChildCount()-1).getElement().addClassName("gnu");
			}
		}		
		save.setEnabled(true);
		fetch.setEnabled(true);
	}


	@Override
	public void showCompetition(ICompetition result) {
		comp = result;
		root.setText(result.getLongName() + "|" + result.getId());
		
		addRounds(result, result.getRounds());
		for (IRound r: result.getRounds()) {
			addRound(comp,r,r.getMatches());
		}
		save.setEnabled(true);
		fetch.setEnabled(true);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.CompetitionView#addComps(com.google.gwt.user.client.ui.TreeItem, java.util.ArrayList)
	 */
	@Override
	public void addComps(List<ICompetition> result) {

		if (compMap == null) {
			compMap = new HashMap<String,ICompetition>();
		}
		for (ICompetition c : result) {
			compMap.put(c.getId().toString(),c);
			
			TreeItem ti = base.addItem(c.getLongName() + "|" + c.getId());
			ti.addItem("teams");
			addTeams(c.getId(), c.getTeams());
			ti.addItem("rounds");
			addRounds(c,c.getRounds());
			for (IRound r : c.getRounds()) {
				addRound(c,r,r.getMatches());
				for (IMatchGroup m: r.getMatches()) {
					List<IMatchResult> results = new ArrayList<IMatchResult>();
					ISimpleScoreMatchResult score = m.getSimpleScoreMatchResult();
					if (score != null) {
						results.add((IMatchResult) m.getSimpleScoreMatchResult());
					}
					if (!results.isEmpty()) {
						addResults(c.getId(), r, m, results);
					}
				}
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.CompetitionView#addRounds(long, java.util.ArrayList)
	 */
	@Override
	public void addRounds(ICompetition comp, List<IRound> result) {
		int i = 0;
		while (i < base.getChildCount()) {
			if (base.getChild(i).getText().contains(comp.getLongName())) {
				base.getChild(i).getChild(1).removeItems();
				for (IRound r : result) {
					base.getChild(i).getChild(1).addItem(r.getName() + " (" + r.getBegin().toString() +") |" + r.getId());
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
					base.getChild(i).getChild(0).addItem(tg.getDisplayName() + "|" + tg.getId());
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
	public void addRound(ICompetition comp, IRound round, List<IMatchGroup> result) {
		int i = 0;
		int j = 0;
		while (i < base.getChildCount()) {
			if (base.getChild(i).getText().contains(comp.getLongName().toString())) {
				while (j < base.getChild(i).getChild(1).getChildCount()) {
					String rid = "null";
					if (round.getId() != null)
						rid = round.getId().toString().toString();
					if (base.getChild(i).getChild(1).getChild(j).getText().contains(round.getName()) && base.getChild(i).getChild(1).getChild(j).getText().contains(rid)) {
						base.getChild(i).getChild(1).getChild(j).removeItems();
						for (IMatchGroup mg : result) {
							TreeItem ti = base.getChild(i).getChild(1).getChild(j).addItem(mg.getDisplayName() + "|" + mg.getId());
							ti.addItem(mg.getDate().toString());
							if (mg.getLocked() != null) {
								if (mg.getLocked()) {
									ti.addItem("Locked");
								} else {
									ti.addItem("Not Locked");									
								}
							} else 
								ti.addItem("Not Locked");
							ti.addItem("results");
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
									TreeItem ti = base.getChild(i).getChild(1).getChild(j).getChild(k).getChild(2).addItem(mr.getRecordedDate().toString());
									if (mr instanceof SimpleScoreMatchResult) {
										ti.addItem(((ISimpleScoreMatchResult)mr).getHomeScore() + " - " + ((ISimpleScoreMatchResult)mr).getVisitScore());
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
}
