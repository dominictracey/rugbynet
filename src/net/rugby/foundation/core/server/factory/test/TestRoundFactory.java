package net.rugby.foundation.core.server.factory.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.Round;

public class TestRoundFactory extends BaseCachingFactory<IRound> implements IRoundFactory {

	private IMatchGroupFactory gf;

	
	@Inject
	TestRoundFactory(IMatchGroupFactory gf) {
		this.gf = gf;
	}


	@Override
	public IRound getFromPersistentDatastore(Long roundId) {
		Round r = new Round();
		r.setId(roundId);
		r.setMatches(new ArrayList<IMatchGroup>());

		if (roundId == null) {
			return r;
		}
		
		// Begin COMP 1
		if (roundId == 6L) {
			r.setAbbr("1");
			r.setName("Round 1");
			r.setOrdinal(1);
			r.addMatchID(100L);
			r.addMatchID(101L);
			r.setCompId(1L);
		} else if (roundId == 7L) {
			r.setAbbr("2");
			r.setName("Round 2");
			r.setOrdinal(2);
			r.addMatchID(102L);
			r.addMatchID(103L);
			r.setCompId(1L);
		} else if (roundId == 8L) {
			r.setAbbr("3");			
			r.setName("Round 3");
			r.setOrdinal(3);
			r.addMatchID(104L);
			r.addMatchID(105L);
			r.addMatchID(106L);
			r.setCompId(1L);
		} else  if (roundId == 9L) {
			r.setAbbr("F");
			r.setName("Finals");
			r.setOrdinal(4);
			r.addMatchID(107L);
			r.addMatchID(108L);
			r.setCompId(1L);
			/** BEGIN COMP 2 **/
		} else if (roundId == 12L) {
			r.setAbbr("1");
			r.setName("Round 1");
			r.setOrdinal(1);
			r.addMatchID(200L);
			r.addMatchID(201L);
			r.addMatchID(202L);
			r.setCompId(2L);
		} else if (roundId == 13L) {
			r.setAbbr("2");			
			r.setName("Round 2");
			r.setOrdinal(2);
			r.addMatchID(203L);
			r.addMatchID(204L);
			r.addMatchID(205L);
			r.setCompId(2L);
		} else  if (roundId == 14L) {
			r.setAbbr("3");
			r.setName("Round 3");
			r.setOrdinal(3);
			r.addMatchID(206L);
			r.addMatchID(207L);
			r.setCompId(2L);
			/** END COMP 2 **/
		}
		 // HEINEKEN CUP 2013-14
		 else  if (roundId == 15L) {
				r.setAbbr("1");
				r.setName("Round 1");
				r.setCompId(4L);
			}
		 // SUPER RUGBY 2014
		 else  if (roundId == 16L) {
				r.setAbbr("1");
				r.setName("Round 1");
				r.setCompId(5L);
			}
		
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -1000);
		r.setEnd(cal.getTime());
		cal.add(Calendar.DATE, 2000);
		r.setBegin(cal.getTime());

		for (Long gid : r.getMatchIDs()) {
			IMatchGroup g = gf.get(gid);
			r.getMatches().add(g);
			if (g.getDate().before(r.getBegin())) {
				r.setBegin(g.getDate());
			}
			
			if (g.getDate().after(r.getEnd())) {
				r.setEnd(g.getDate());
			}
		}

		return r;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IRoundFactory#find(net.rugby.foundation.model.shared.IRound)
	 */
	@Override
	public IRound find(IRound round) {
		// hard to implement
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IRoundFactory#build(java.lang.Long)
	 */
	@Override
	public void invalidate(Long roundId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean deleteFromPersistentDatastore(IRound r) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public IRound create() {
		return new Round();
	}


	@Override
	protected IRound putToPersistentDatastore(IRound t) {
		// TODO Auto-generated method stub
		return t;
	}


}
