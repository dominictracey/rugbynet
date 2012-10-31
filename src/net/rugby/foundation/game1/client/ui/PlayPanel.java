/**
 * 
 */
package net.rugby.foundation.game1.client.ui;

import java.util.Date;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.game1.shared.IMatchEntry;
import net.rugby.foundation.game1.shared.IRoundEntry;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ISimpleScoreMatchResult;
import net.rugby.foundation.model.shared.ITeamGroup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author home
 *
 */
public class PlayPanel extends Composite {

	private static PlayPanelUiBinder uiBinder = GWT
			.create(PlayPanelUiBinder.class);

	interface PlayPanelUiBinder extends UiBinder<Widget, PlayPanel> {
	}

	public interface Presenter {
		void pickMade(IEntry entry, IRound round, IMatchGroup match, ITeamGroup team);
	}

	@UiField
	HorizontalPanel matchPanel;
	//	@UiField
	CheckBox homeCheck;
	//	@UiField
	CheckBox visitCheck;
	//	@UiField
	//	Label homeName;
	//	@UiField
	//	Label visitName;
	//	@UiField
	//	Label homeScore;
	//	@UiField
	//	Label visitScore;
	@UiField
	Grid teamGrid;
	@UiField
	MatchStatsViewImpl stats;
	@UiField
	HTMLPanel statusPanel;
	@UiField
	Label outcome;
	@UiField
	HTMLPanel lockTimePanel;	
	@UiField
	Label timeLeft;

	Date Kickoff;

	Presenter listener;
	IMatchGroup match;
	IRound round;
	IEntry entry;
	//private DateTimeFormat format = DateTimeFormat.getFormat("EEE MM-dd hh:mm");   

	public PlayPanel(IMatchGroup match, IEntry entry, IRound round, Presenter listener) {
		initWidget(uiBinder.createAndBindUi(this));
		ISimpleScoreMatchResult result = match.getSimpleScoreMatchResult();
		String homeScoreVal = " ";
		String visitScoreVal = " ";
		Boolean homeWon = null;
		if (result != null) {
			homeScoreVal += Integer.toString(result.getHomeScore());
			visitScoreVal += Integer.toString(result.getVisitScore());
			if (result.getHomeScore() > result.getVisitScore()) {
				homeWon = true;
			} else if (result.getHomeScore() < result.getVisitScore()) {
				homeWon = false;
			} 
		}
		//		this.homeName.setText(match.getHomeTeam().getDisplayName());
		//		this.visitName.setText(match.getVisitingTeam().getDisplayName());
		//		homeScore.setText(homeScoreVal);
		//		visitScore.setText(visitScoreVal);
		teamGrid.resize(2,3);
		homeCheck = new CheckBox();
		homeCheck.setStylePrimaryName("teamGrid");
		homeCheck.addClickHandler(onHomePickClick);
		visitCheck = new CheckBox();
		visitCheck.setStylePrimaryName("teamGrid");
		visitCheck.addClickHandler(onVisitPickClick);
		teamGrid.setWidget(0, 0, homeCheck);
		teamGrid.setWidget(0, 1, new Label(match.getHomeTeam().getDisplayName()));
		teamGrid.setWidget(0, 2, new Label(homeScoreVal));
		teamGrid.setWidget(1, 0, visitCheck);
		teamGrid.setWidget(1, 1, new Label(match.getVisitingTeam().getDisplayName()));
		teamGrid.setWidget(1, 2, new Label(visitScoreVal));

		teamGrid.getWidget(0, 1).setStylePrimaryName("playPanelSegment");
		teamGrid.getWidget(1, 1).setStylePrimaryName("playPanelSegment");
		
		this.listener = listener;
		this.match = match;
		this.entry = entry;
		this.round = round;

		// do we have a pick for this match already?
		IRoundEntry re = entry.getRoundEntries().get(round.getId());
		if (re != null) {
			IMatchEntry me = re.getMatchPickMap().get(match.getId());
			if (me != null) {
				if (me.getTeamPickedId() != null) {
					if (me.getTeamPickedId().equals(match.getHomeTeam().getId())) {
						((CheckBox)teamGrid.getWidget(0, 0)).setValue(true);
						if (homeWon != null) {
							if (homeWon)
								setStylePrimaryName("correct");
							else
								setStylePrimaryName("incorrect");

						}
						((CheckBox)teamGrid.getWidget(1, 0)).setValue(false);
						//	visitCheck.setValue(false);
					} else if (me.getTeamPickedId().equals(match.getVisitingTeam().getId())) {
						((CheckBox)teamGrid.getWidget(0, 0)).setValue(false);
						((CheckBox)teamGrid.getWidget(1, 0)).setValue(true);
						//						homeCheck.setValue(false);
						//						visitCheck.setValue(true);
						if (homeWon != null) {
							if (!homeWon)
								setStylePrimaryName("correct");
							else
								setStylePrimaryName("incorrect");								
						}
					}
				}
			}
		}

		if (!match.getLocked() && match.getDate().after(new Date())) {
			lockTimePanel.setVisible(true);
			statusPanel.setVisible(false);
			timeLeft.setText(" " + getFriendlyTime(match.getDate()));
		} else {
			lockTimePanel.setVisible(false);
			statusPanel.setVisible(true);	
			if (match.getStatus() == Status.FINAL_DRAW ||
					match.getStatus() == Status.FINAL_HOME_WIN ||
					match.getStatus() == Status.FINAL_VISITOR_WIN) {
					
				outcome.setText("F");
			}
			if (match.getStatus() == Status.FINAL_DRAW_OT ||
					match.getStatus() == Status.FINAL_HOME_WIN_OT ||
					match.getStatus() == Status.FINAL_VISITOR_WIN_OT) {
				outcome.setText("F - OT");
			}
			if (match.getStatus() == Status.POSTPONED) {
				outcome.setText("PPD");		
			}
			if (match.getStatus() == Status.CANCELED) {
				outcome.setText("Canceled");		
			}			
			if (match.getStatus() == Status.COMPLETE_AWAITING_RESULTS ||
					match.getStatus() == Status.HALFTIME ||
					match.getStatus() == Status.UNDERWAY_FIRST_HALF ||
					match.getStatus() == Status.UNDERWAY_SECOND_HALF				) {
				outcome.setText("Underway");		
			}			

		}

		((CheckBox)teamGrid.getWidget(0, 0)).setEnabled(!match.getLocked());
		((CheckBox)teamGrid.getWidget(1, 0)).setEnabled(!match.getLocked());

		//		homeCheck.setEnabled(!match.getLocked());
		//		visitCheck.setEnabled(!match.getLocked());

		if (listener instanceof MatchStatsView.Presenter && !match.getHomeTeamId().equals(0L)) {
			stats.setHomeColor(match.getHomeTeam().getColor());
			stats.setAwayColor(match.getVisitingTeam().getColor());
			stats.setMatchId(match.getId());
			stats.setPresenter((MatchStatsView.Presenter)listener);

			stats.setHomeName(match.getHomeTeam().getAbbr());
			stats.setAwayName(match.getVisitingTeam().getAbbr());
		}


		matchPanel.addStyleName("matchPanel");
	}
	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	public PlayPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	//@UiHandler("homeCheck")
	ClickHandler onHomePickClick = new ClickHandler() {	


		@Override
		public void onClick(ClickEvent event) {
			if (homeCheck.getValue()) {
				visitCheck.setValue(false);
			}		
			listener.pickMade(entry, round, match, match.getHomeTeam() );
		}

	};

	//@UiHandler("visitCheck")
	ClickHandler onVisitPickClick = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (visitCheck.getValue()) {
				homeCheck.setValue(false);
			}

			listener.pickMade(entry, round, match, match.getVisitingTeam() );
		}
	};

	public static String getFriendlyTime(Date dateTime) {
		StringBuffer sb = new StringBuffer();
		Date current = new Date();
		long diffInSeconds = (dateTime.getTime() - current.getTime()) / 1000;

		/*long diff[] = new long[]{0, 0, 0, 0};
	    /* sec *  diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
	    /* min *  diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
	    /* hours *  diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
	    /* days * diff[0] = (diffInSeconds = (diffInSeconds / 24));
		 */
		long sec = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
		long min = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
		long hrs = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
		long days = (diffInSeconds = (diffInSeconds / 24)) >= 30 ? diffInSeconds % 30 : diffInSeconds;
		long months = (diffInSeconds = (diffInSeconds / 30)) >= 12 ? diffInSeconds % 12 : diffInSeconds;
		long years = (diffInSeconds = (diffInSeconds / 12));

		if (years > 0) {
			if (years == 1) {
				sb.append("a year");
			} else {
				sb.append(years + " years");
			}
			if (years <= 6 && months > 0) {
				if (months == 1) {
					sb.append(" and a month");
				} else {
					sb.append(" and " + months + " months");
				}
			}
		} else if (months > 0) {
			if (months == 1) {
				sb.append("a month");
			} else {
				sb.append(months + " months");
			}
			if (months <= 6 && days > 0) {
				if (days == 1) {
					sb.append(" and a day");
				} else {
					sb.append(" and " + days + " days");
				}
			}
		} else if (days > 0) {
			if (days == 1) {
				sb.append("a day");
			} else {
				sb.append(days + " days");
			}
			if (days <= 3 && hrs > 0) {
				if (hrs == 1) {
					sb.append(" and an hour");
				} else {
					sb.append(" and " + hrs + " hours");
				}
			}
		} else if (hrs > 0) {
			if (hrs == 1) {
				sb.append("an hour");
			} else {
				sb.append(hrs + " hours");
			}
			if (min > 1) {
				sb.append(" and " + min + " minutes");
			}
		} else if (min > 0) {
			if (min == 1) {
				sb.append("a minute");
			} else {
				sb.append(min + " minutes");
			}
			//	        if (sec > 1) {
			//	            sb.append(" and " + sec + " seconds");
			//	        }
		} 
		//	    else {
		//	        if (sec <= 1) {
		//	            sb.append("about a second");
		//	        } else {
		//	            sb.append("about " + sec + " seconds");
		//	        }
		//	    }

		//sb.append(" ago");


		/*String result = new String(String.format(
	    "%d day%s, %d hour%s, %d minute%s, %d second%s ago",
	    diff[0],
	    diff[0] > 1 ? "s" : "",
	    diff[1],
	    diff[1] > 1 ? "s" : "",
	    diff[2],
	    diff[2] > 1 ? "s" : "",
	    diff[3],
	    diff[3] > 1 ? "s" : ""));*/
		return sb.toString();
	}

}
