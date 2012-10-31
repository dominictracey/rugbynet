/**
 * 
 */
package net.rugby.foundation.game1.server.factory.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory;
import net.rugby.foundation.game1.server.factory.ILeagueFactory;
import net.rugby.foundation.game1.shared.ClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.ILeague;
import net.rugby.foundation.model.shared.DataStoreFactory;

/**
 * @author home
 *
 */
public class OfyClubhouseLeagueMapFactory implements IClubhouseLeagueMapFactory {
	private Long id;
	private Long value = null;
	private String filter = null;
	private Long value2 = null;
	private String filter2 = null;	
	
	private final Objectify ofy;
	private Long compId;
	private Long clubhouseId;
	//private Long leagueId;
	private ILeagueFactory lf;

	@Inject
	public OfyClubhouseLeagueMapFactory(ILeagueFactory lf) {
		this.ofy = DataStoreFactory.getOfy();
		this.lf = lf;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IDefaultLeagueFactory#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IDefaultLeagueFactory#get()
	 */
	@Override
	public IClubhouseLeagueMap get() {	
		IClubhouseLeagueMap dl = null;
		// call with 0L to get an empty one.
		if (compId != null && clubhouseId != null) {
			//call setClubhouseAndCompId to do a lazy create of the CLM and league
			Query<ClubhouseLeagueMap> clmq = ofy.query(ClubhouseLeagueMap.class).filter("compId",compId).filter("clubhouseId", clubhouseId);
			assert(clmq.count()==1);
			if (clmq.count() == 0) {
				Logger.getLogger(OfyClubhouseLeagueMapFactory.class.toString()).log(Level.WARNING, "get: detected missing CLM and League for clubhouse " +clubhouseId + " and comp " + compId);
			} else if (clmq.count() > 1) {
				Logger.getLogger(OfyClubhouseLeagueMapFactory.class.toString()).log(Level.WARNING, "get: detected duplicate CLM and League for clubhouse " +clubhouseId + " and comp " + compId);				
			}
			dl = clmq.get();
			if (dl == null) { // need to create
				// first create the league
				lf.setId(0L);
				ILeague league = lf.get();
				lf.put(league);
				
				// and the CLM
				dl = new ClubhouseLeagueMap(compId, clubhouseId, league.getId());
				put(dl);
			}
		} else if (id == 0L || id == null) {
			return new ClubhouseLeagueMap();
		} else {
			// call with valid id to look for
			dl = ofy.find(new Key<ClubhouseLeagueMap>(ClubhouseLeagueMap.class,id));
		}

		return dl;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IDefaultLeagueFactory#put(net.rugby.foundation.game1.shared.IDefaultLeague)
	 */
	@Override
	public IClubhouseLeagueMap put(IClubhouseLeagueMap clm) {
		ofy.put(clm);
		return clm;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory#setClubhouseId(java.lang.Long)
	 */
	@Override
	public void setClubhouseId(Long clubhouseId) {
		this.value = clubhouseId;
		this.filter = "clubhouseId";
		this.value2 = null;
		this.filter2 = null;
		
		this.compId = null;
		this.clubhouseId = null;
//		this.leagueId = null;
		this.id = null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory#setCompetitionId(java.lang.Long)
	 */
	@Override
	public void setCompetitionId(Long competitionId) {

		this.value = competitionId;	
		this.filter = "compId";
		this.value2 = null;
		this.filter2 = null;
		
		this.compId = null;
		this.clubhouseId = null;
//		this.leagueId = null;
		this.id = null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory#setLeagueId(java.lang.Long)
	 */
	@Override
	public void setLeagueId(Long leagueId) {

		this.value = leagueId;
		this.filter = "leagueId";	
		this.value2 = null;
		this.filter2 = null;
		
		this.compId = null;
		this.clubhouseId = null;
//		this.leagueId = null;
		this.id = null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory#getList()
	 */
	@Override
	public List<IClubhouseLeagueMap> getList() {
		//filter based on what search parameters were set
		if (value == null) 
			return null;
		
		
		Query<ClubhouseLeagueMap> clmq = null;
		
		if (filter2 == null) {
			clmq = ofy.query(ClubhouseLeagueMap.class).filter(filter, value);
		} else {
			clmq = ofy.query(ClubhouseLeagueMap.class).filter(filter, value).filter(filter2, value2);
		}
		
		//it would be nice to be able to return clmq.list() here...
		ArrayList<IClubhouseLeagueMap> clml = new ArrayList<IClubhouseLeagueMap>();
		
		for (ClubhouseLeagueMap clm : clmq) {
			clml.add(clm);
		}
		
		return clml;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory#setClubhouseAndCompId(java.lang.Long, java.lang.Long)
	 */
	@Override
	public void setClubhouseAndCompId(Long compId, Long clubhouseId) {
		
		this.compId = compId;
		this.clubhouseId = clubhouseId;
//		this.leagueId = null;
		this.id = null;
		
		// in case they call getList()
		this.value = compId;	
		this.filter = "compId"; 
		this.value2 = clubhouseId;
		this.filter2 = "clubhouseId";
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory#delete()
	 */
	@Override
	public Boolean delete() {
		try {
			ofy.delete(ClubhouseLeagueMap.class, id);
		} catch (Throwable e) {
			Logger.getLogger(OfyClubhouseLeagueMapFactory.class.getName()).log(Level.SEVERE,"Problems deleting CLM " + id.toString(),e);
			return false;
		}
		return true;
	}

}
