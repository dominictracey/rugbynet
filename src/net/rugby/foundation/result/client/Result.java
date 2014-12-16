package net.rugby.foundation.result.client;

import java.util.Iterator;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.constants.IconType;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.core.client.Core.CompChangeListener;
import net.rugby.foundation.core.client.Core.RoundChangeListener;
import net.rugby.foundation.core.client.CoreServiceAsync;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.topten.client.place.SeriesPlace;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Result implements EntryPoint, CompChangeListener, RoundChangeListener {
	//Map<Long, TeamGroup> teamMap = null;
	private final CoreServiceAsync rpcService = Core.getInstance(); //ServicesFactory.rpcService;//GWT.create(CoreService.class);
	private final RootPanel rootPanel = RootPanel.get("resultPanel");
	//@TODO locale works??
	
	//DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT); //"EEE MM-dd hh:mm");   
	DateTimeFormat format = DateTimeFormat.getFormat("EEE MMM d");   

	private IRound currentRound;
	private int roundIndex;
	private ICompetition comp;
	private ResultPanel resultPanel;
	private boolean drawn = false;
	
	public void onModuleLoad() {
		
		Core.getCore().registerCompChangeListener(this);
		Core.getCore().registerRoundChangeListener(this);
		//Core.getCore().setCurrentCompId(6092943685320704L);

	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.Core.CompChangeListener#compChanged(java.lang.Long)
	 */
	@Override
	public void compChanged(final Long compId) {
		rootPanel.clear();
		
		resultPanel = new ResultPanel();
		
		resultPanel.getLeft_arrow().addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					// scroll everything away to the right with animations!
					if (roundIndex > 0) {  // don't run off the front
						Core.getCore().setCurrentRoundOrdinal(comp.getRounds().get(roundIndex-1).getOrdinal(), true);
					}
					
				}
				
			});
		
		resultPanel.getRight_arrow().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// scroll everything away to the left with animations!
				if (roundIndex < comp.getRounds().size()) {  // don't run off the front
					Core.getCore().setCurrentRoundOrdinal(comp.getRounds().get(roundIndex+1).getOrdinal(), true);
				}
				
			}
			
		});
		
		Core.getCore().getComp(compId, new AsyncCallback<ICompetition>() {


			@Override
			public void onFailure(Throwable caught) {
				rootPanel.add(new HTML("Current scores unavailable"));
				
			}

			@Override
			public void onSuccess(final ICompetition result) {
				comp = result;
				
				if (!drawn && Core.getCore().getCurrentRoundOrdinal() != -1) {
					roundChanged(Core.getCore().getCurrentRoundOrdinal());
				}
				
				rootPanel.add(resultPanel);

			}
			
		});
		

	
	}

	@Override
	public void roundChanged(int ordinal) {
		if (resultPanel.scores != null) {
			resultPanel.scores.clear();
			currentRound = null;
			if (comp != null) {
				Iterator<IRound> it = comp.getRounds().iterator();
				roundIndex = 0;
				while (it.hasNext()) {
					IRound r = it.next();
					if (r.getUrOrdinal() >= ordinal) {
						currentRound = r;
						break;
					}
					roundIndex++;
				}
			}
		}
		
		if (currentRound != null) {
			addPanels();
			resultPanel.header.setInnerHTML("<strong>" + comp.getShortName() + " " + currentRound.getName() + " Results</strong>");
					
		}
		
	}
	
	protected void addPanels() {
		if (currentRound != null) {
			for (IMatchGroup m : currentRound.getMatches()) {
				String homeScore = "";
				String visitScore = "";
				if (m.getSimpleScoreMatchResult() != null) {
					homeScore = Integer.toString(m.getSimpleScoreMatchResult().getHomeScore());
					visitScore = Integer.toString(m.getSimpleScoreMatchResult().getVisitScore());
				}
				
				String header = "Final";
				if (m.getStatus().equals(Status.SCHEDULED))
					header = format.format(m.getDate());
				else if (!m.getStatus().toString().contains("FINAL")) {
					header = "";
				}
				
				MatchScore score = new MatchScore();
				score.getHeader().setText(header);
				score.getBody().add(new HTML(m.getHomeTeam().getAbbr() + " " + homeScore + "<br/>" + m.getVisitingTeam().getAbbr() + " " + visitScore));
				
//				HTML p1 = new HTML("<div class=\"col-md-1\">" + 
//										"<div class=\"panel panel-default\">" +
//											"<div class=\"panel-heading small no-padding\">" + header + "</div>" +
//											"<div class=\"panel-body no-padding\">" + m.getHomeTeam().getAbbr() + " " + homeScore + "<br/>"  + m.getVisitingTeam().getAbbr() + " " + visitScore +"</div></div>");
				
				score.addStyleName("col-md-2 col-xs-4");
				score.addStyleName("no-padding");
				score.getHeader().addStyleName("no-padding");
				score.getBody().addStyleName("no-padding");
				
				if (m.getGuid() != null && !m.getGuid().isEmpty()) {
					Anchor a = new Anchor();
					final IMatchGroup _m = m;
					a.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							// we have the guid of the match list and we need to get a seriesPlace for it
							Core.getCore().changeGuid(_m.getGuid());						
						}
						
					});
					a.add(score);
					resultPanel.scores.add(a);
				} else {
					resultPanel.scores.add(score);
				}
			}
		}
	}
	
}
