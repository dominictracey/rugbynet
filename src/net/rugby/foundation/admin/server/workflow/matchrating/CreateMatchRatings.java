package net.rugby.foundation.admin.server.workflow.matchrating;

import java.util.List;

import net.rugby.foundation.model.shared.IMatchRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ITeamMatchStats;

import com.google.appengine.tools.pipeline.Job4;
import com.google.appengine.tools.pipeline.Value;

public class CreateMatchRatings extends Job4<List<IMatchRating>, List<IPlayerMatchStats>, List<IPlayerMatchStats>, ITeamMatchStats, ITeamMatchStats> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5676927821878573678L;


	public Value<List<IMatchRating>> run(List<IPlayerMatchStats> homePlayerStats, List<IPlayerMatchStats> visitorPlayerStats,
//			IPlayerMatchStats h1,
//			IPlayerMatchStats h2,
//			IPlayerMatchStats h3,
//			IPlayerMatchStats h4,
//			IPlayerMatchStats h5,
//			IPlayerMatchStats h6,
//			IPlayerMatchStats h7,
//			IPlayerMatchStats h8,
//			IPlayerMatchStats h9,
//			IPlayerMatchStats h10,
//			IPlayerMatchStats h11,
//			IPlayerMatchStats h12,
//			IPlayerMatchStats h13,
//			IPlayerMatchStats h14,
//			IPlayerMatchStats h15,
//			IPlayerMatchStats h16,
//			IPlayerMatchStats h17,
//			IPlayerMatchStats h18,
//			IPlayerMatchStats h19,
//			IPlayerMatchStats h20,
//			IPlayerMatchStats h21,
//			IPlayerMatchStats h22,
//			IPlayerMatchStats h23,
//			IPlayerMatchStats v1,
//			IPlayerMatchStats v2,
//			IPlayerMatchStats v3,
//			IPlayerMatchStats v4,
//			IPlayerMatchStats v5,
//			IPlayerMatchStats v6,
//			IPlayerMatchStats v7,
//			IPlayerMatchStats v8,
//			IPlayerMatchStats v9,
//			IPlayerMatchStats v10,
//			IPlayerMatchStats v11,
//			IPlayerMatchStats v12,
//			IPlayerMatchStats v13,
//			IPlayerMatchStats v14,
//			IPlayerMatchStats v15,
//			IPlayerMatchStats v16,
//			IPlayerMatchStats v17,
//			IPlayerMatchStats v18,
//			IPlayerMatchStats v19,
//			IPlayerMatchStats v20,
//			IPlayerMatchStats v21,
//			IPlayerMatchStats v22,
//			IPlayerMatchStats v23,
			ITeamMatchStats hStats,
			ITeamMatchStats vStats ) {
		// TODO Auto-generated method stub
		return null;
	}

}
