package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.core.server.factory.ICompetitionTeamFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.CompetitionTeam;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.ICompetitionTeam;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.Round;

public class OfyCompetitionTeamFactory implements ICompetitionTeamFactory, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	private Long id;
//	private final Objectify ofy;
//	
//	@Inject
//	OfyCompetitionTeamFactory() {
//		this.ofy = DataStoreFactory.getOfy();
//	}
//	
//	@Override
//	public void setId(Long id) {
//		this.id = id;
//
//	}
//
//	@Override
//	public ICompetitionTeam get() {
//		
//		//throw new Exception("Deprecated");
//		
//		CompetitionTeam ct = ofy.get(new Key<CompetitionTeam>(CompetitionTeam.class,id));
//		
//		if (ct != null) {
//			//@REX do we want to deep link (i.e. add ICompetition and ITeamGroup members to CompetitionTeam?
//		} else {
//			ct = new CompetitionTeam();
//		}
//		return ct;
//	}
//
//	@Override
//	public ICompetitionTeam put(ICompetitionTeam ct) {
//
//		if (ct != null) {
//
//		} else {
//			ct = new CompetitionTeam();
//		}		
//		
//		ofy.put(ct);
//		
//		return ct;
//	}

}
