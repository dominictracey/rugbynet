package net.rugby.foundation.result.client;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.core.client.Core.CompChangeListener;
import net.rugby.foundation.core.client.CoreServiceAsync;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Result implements EntryPoint, CompChangeListener {
	//Map<Long, TeamGroup> teamMap = null;
	private final CoreServiceAsync rpcService = Core.getInstance(); //ServicesFactory.rpcService;//GWT.create(CoreService.class);
	private final RootPanel rootPanel = RootPanel.get("resultPanel");
	//@TODO locale works??
	DateTimeFormat format = DateTimeFormat.getFormat("EEE MM-dd hh:mm");   
	
	public void onModuleLoad() {
		
		Core.getCore().registerCompChangeListener(this);
		

	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.Core.CompChangeListener#compChanged(java.lang.Long)
	 */
	@Override
	public void compChanged(final Long compId) {
		rootPanel.clear();
		
		rpcService.getComp(compId, new AsyncCallback<ICompetition>() {

			@Override
			public void onFailure(Throwable caught) {
				rootPanel.add(new HTML("Current scores unavailable"));
				
			}

			@Override
			public void onSuccess(ICompetition result) {
				IRound r = result.getPrevRound();
				
				if (r == null)
					r = result.getNextRound();
				
				if (r != null) {
					for (IMatchGroup m : r.getMatches()) {
						String homeScore = "";
						String visitScore = "";
						if (m.getSimpleScoreMatchResult() != null) {
							homeScore = Integer.toString(m.getSimpleScoreMatchResult().getHomeScore());
							visitScore = Integer.toString(m.getSimpleScoreMatchResult().getVisitScore());
						}
						HTML p1 = new HTML("<div class='resultPanel'><div class='resultPanelTop'><strong>" + format.format(m.getDate()) + "</strong></div><div class='resultPanelBottom'>" + m.getHomeTeam().getAbbr() + " " + homeScore + "<br/>"  + m.getVisitingTeam().getAbbr() + " " + visitScore +"</div></div>");								
						rootPanel.add(p1);
					}
				}
				
			}
			
		});
		
	}
}
