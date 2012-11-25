package net.rugby.foundation.admin.server.workflow.matchrating;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;

import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings.Home_or_Visitor;
import net.rugby.foundation.model.shared.ITeamGroup;

public class FetchTeamFromScrumReport extends Job2<ITeamGroup, Home_or_Visitor, String> {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3101992931956737933L;

	@Override
	public Value<ITeamGroup> run(Home_or_Visitor home_or_visitor, String url) {
		// TODO Auto-generated method stub
		return null;
	}

}
