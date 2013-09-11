package net.rugby.foundation.core.server.factory.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.Round;

public class TestRoundFactory implements IRoundFactory {
	private Long roundId;
	private IMatchGroupFactory gf;
	//private ICompetitionFactory cf;
	
	@Inject
	TestRoundFactory(IMatchGroupFactory gf) {
		this.gf = gf;
	}
	
	@Override
	public void setId(Long id) {
		this.roundId = id;

	}

	@Override
	public IRound getRound() {
		Round r = new Round();
		r.setId(roundId);
		r.setMatches(new ArrayList<IMatchGroup>());

		if (roundId == null) {
			return r;
		}
		
		// Begin COMP 1
		if (roundId == 2L) {
			r.setAbbr("1");
			r.setName("Round 1");
			r.setOrdinal(1);
			r.addMatchID(100L);
			r.addMatchID(101L);
		} else if (roundId == 3L) {
			r.setAbbr("2");
			r.setName("Round 2");
			r.setOrdinal(2);
			r.addMatchID(102L);
			r.addMatchID(103L);
		} else if (roundId == 4L) {
			r.setAbbr("3");			
			r.setName("Round 3");
			r.setOrdinal(3);
			r.addMatchID(104L);
			r.addMatchID(105L);
			r.addMatchID(106L);
		} else  if (roundId == 5L) {
			r.setAbbr("F");
			r.setName("Finals");
			r.setOrdinal(4);
			r.addMatchID(107L);
			r.addMatchID(108L);
			/** BEGIN COMP 2 **/
		} else if (roundId == 12L) {
			r.setAbbr("1");
			r.setName("Round 1");
			r.setOrdinal(1);
			r.addMatchID(200L);
			r.addMatchID(201L);
			r.addMatchID(202L);
		} else if (roundId == 13L) {
			r.setAbbr("2");			
			r.setName("Round 2");
			r.setOrdinal(2);
			r.addMatchID(203L);
			r.addMatchID(204L);
			r.addMatchID(205L);
		} else  if (roundId == 14L) {
			r.setAbbr("3");
			r.setName("Round 3");
			r.setOrdinal(3);
			r.addMatchID(206L);
			r.addMatchID(207L);
			/** END COMP 2 **/
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

	@Override
	public IRound put(IRound r) {
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
	public void build(Long roundId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean delete(Long roundId) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IRoundFactory#setFactories(net.rugby.foundation.core.server.factory.ICompetitionFactory, net.rugby.foundation.core.server.factory.IMatchGroupFactory)
	 */
//	@Override
//	public void setFactories(ICompetitionFactory cf, IMatchGroupFactory mf) {
//		this.cf = cf;
////		this.mf = mf;
//		this.gf = mf;
//	}

}
