package net.rugby.foundation.admin.server.workflow.matchrating;

import java.util.List;

import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.ITeamGroup;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;

public class FetchPlayersForTeam extends
		Job2<List<IPlayer>, ITeamGroup, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2196079562850795995L;

	@Override
	public Value<List<IPlayer>> run(ITeamGroup team, String url) {
		// TODO Auto-generated method stub
		return null;
	}

}
