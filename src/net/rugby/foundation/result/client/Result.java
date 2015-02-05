package net.rugby.foundation.result.client;

import java.util.ArrayList;
import java.util.Iterator;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Div;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.core.client.Core.CompChangeListener;
import net.rugby.foundation.core.client.Core.RoundChangeListener;
import net.rugby.foundation.core.client.CoreServiceAsync;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.UniversalRound;
import net.rugby.foundation.topten.client.place.SeriesPlace;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.Window;
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

	private ArrayList<IMatchGroup> currentRound;
	private int roundIndex;
	private ICompetition comp;
	private ResultPanel resultPanel;
	private boolean drawn = false;
	private boolean isVirtualComp;

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



		Core.getCore().getComp(compId, new AsyncCallback<ICompetition>() {




			@Override
			public void onFailure(Throwable caught) {
				rootPanel.add(new HTML("Current scores unavailable"));

			}

			@Override
			public void onSuccess(final ICompetition result) {

				comp = result;

				isVirtualComp = comp.getRoundIds().size() < 1;

				resultPanel = new ResultPanel();

				resultPanel.getLeft_arrow().addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						// scroll everything away to the right with animations!
						if (isVirtualComp) {
							Core.getCore().setCurrentRoundOrdinal(Core.getCore().getCurrentRoundOrdinal()-1, true);
						} else if (roundIndex > 0) {  // don't run off the front
							Core.getCore().setCurrentRoundOrdinal(comp.getRounds().get(roundIndex-1).getUrOrdinal(), true);
						}

					}

				});

				resultPanel.getRight_arrow().addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						// scroll everything away to the left with animations!
						if (isVirtualComp) {
							Core.getCore().setCurrentRoundOrdinal(Core.getCore().getCurrentRoundOrdinal()+1, true);
						} else if (roundIndex < comp.getRounds().size()) {  // don't run off the front
							Core.getCore().setCurrentRoundOrdinal(comp.getRounds().get(roundIndex+1).getUrOrdinal(), true);
						}

					}

				});

				if (!drawn && Core.getCore().getCurrentRoundOrdinal() != -1) {
					//roundChanged(Core.getCore().getCurrentRoundOrdinal());
					Core.getCore().setCurrentRoundOrdinal(Core.getCore().getCurrentRoundOrdinal(), true);
				}

				rootPanel.add(resultPanel);

			}

		});



	}

	@Override
	public void roundChanged(final UniversalRound ur) {
		String name = "";
		console("Result.roundChanged " + ur.longDesc + " " + ur.ordinal);
		if (resultPanel.scores != null) {
			resultPanel.scores.clear();
			currentRound = null;
			if (comp != null && !isVirtualComp) {
				Iterator<IRound> it = comp.getRounds().iterator();
				roundIndex = 0;
				String weekOf = "";
				while (it.hasNext()) {
					IRound r = it.next();
					if (r.getUrOrdinal() >= ur.ordinal) {
						currentRound = r.getMatches();
						name = r.getName();
						weekOf = format.format(r.getBegin());
						break;
					}
					roundIndex++;
				}
				if (currentRound != null) {
					addPanels();
					resultPanel.header.setInnerHTML("<strong>Results for " + comp.getShortName() + " " + name + " (week of " + weekOf + ")</strong>");


				}
			} else {
				// virtual comp
				Core.getCore().getResultsForOrdinal(ur.ordinal, comp.getId(), new AsyncCallback<ArrayList<IMatchGroup>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(ArrayList<IMatchGroup> result) {
						currentRound = result;
						if (currentRound != null) {
							addPanels();
							resultPanel.header.setInnerHTML("<strong>Results for " + comp.getShortName() + " " + ur.longDesc + "</strong>");

						}

					}

				});
			}
		}



	}

	protected void addPanels() {
		if (currentRound != null) {
			for (IMatchGroup m : currentRound) {
				String homeScore = "";
				String visitScore = "";
				if (m.getSimpleScoreMatchResult() != null) {
					homeScore = Integer.toString(m.getSimpleScoreMatchResult().getHomeScore());
					visitScore = Integer.toString(m.getSimpleScoreMatchResult().getVisitScore());
				}
				
				//ICompetition hostComp = Core.getCore().getComp(, cb)

				String header = "<b>Final</b>";
				if (m.getStatus().equals(Status.SCHEDULED))
					header = format.format(m.getDate());
				else if (!m.getStatus().toString().contains("FINAL")) {
					header = "";
				}
				MatchScore score = new MatchScore();
				HTML _header = new HTML(header);

				boolean bigWinder = Window.getClientWidth() > 992;
				if (bigWinder) {
					Div compImg = new Div();
					compImg.setStylePrimaryName("comp-logo-sq");
					compImg.setStyleDependentName(comp.getAbbr(), true);
					Div compImgSize = new Div();
					compImgSize.setHeight("15px");
					compImgSize.setWidth("15px");
					compImg.add(compImgSize);

					score.getHeader().add(compImg);
					score.getHeader().add(_header); //.setText(header);

					Div homeImg = new Div();
					homeImg.setStylePrimaryName("team-logo");
					homeImg.addStyleName(m.getHomeTeam().getAbbr());
					Div homeImgSize = new Div();
					homeImgSize.setHeight("15px");
					homeImg.add(homeImgSize);
					Div visitImg = new Div();
					visitImg.setStylePrimaryName("team-logo");
					visitImg.addStyleName(m.getVisitingTeam().getAbbr());
					Div visitImgSize = new Div();
					visitImgSize.setHeight("15px");
					visitImg.add(visitImgSize);

					//				if (Window.getClientWidth() > 1200) {
					//					score.getBody().add(homeImg);
					//					score.getBody().add(new HTML(m.getHomeTeam().getShortName() + " " + homeScore + "<br/>"));
					//					score.getBody().add(visitImg);
					//					score.getBody().add(new HTML(m.getVisitingTeam().getShortName() + " " + visitScore));
					//				} else {

					score.getBody().add(homeImg);
					score.getBody().add(new HTML(m.getHomeTeam().getAbbr() + " " + homeScore + "<br/>"));
					score.getBody().add(visitImg);
					score.getBody().add(new HTML(m.getVisitingTeam().getAbbr() + " " + visitScore));
				} else {
					Div compImg = new Div();
					compImg.setStylePrimaryName("comp-logo-sq");
					compImg.setStyleDependentName(comp.getAbbr(), true);
					Div compImgSize = new Div();
					compImgSize.setHeight("15px");
					compImgSize.setWidth("15px");
					compImg.add(compImgSize);

					score.getHeader().add(compImg);
					score.getHeader().add(_header); //.setText(header);
					score.getBody().add(new HTML(m.getHomeTeam().getAbbr() + " " + homeScore + "<br/>"));
					score.getBody().add(new HTML(m.getVisitingTeam().getAbbr() + " " + visitScore));
				}
				//score.getBody().add(new HTML(m.getHomeTeam().getAbbr() + " " + homeScore + "<br/>" + m.getVisitingTeam().getAbbr() + " " + visitScore));
				//				}

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
	
	public native void console(String text)
	/*-{
	    console.log(text);
	}-*/;

}
