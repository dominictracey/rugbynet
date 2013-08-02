/**
 * 
 */
package net.rugby.foundation.core.server.factory.test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.SimpleScoreMatchResult;

/**
 * @author home
 *
 */
public class TestMatchResultFactory implements IMatchResultFactory {
	private Long id;
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.IMatchResultFactory#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.IMatchResultFactory#get()
	 */
	@Override
	public IMatchResult get() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());

		if (id == 8001) { // for match 100
			//Long matchID, Date recordedDate, String source, int homeScore, int visitScore
			cal.add(Calendar.DATE, -11);
			IMatchResult r = new SimpleScoreMatchResult(100L, cal.getTime(), "TestData1", 24, 14);
			r.setStatus(Status.FINAL_HOME_WIN);
			r.setId(id);
			return r;
		} else if (id == 8002) { // for match 101
			//Long matchID, Date recordedDate, String source, int homeScore, int visitScore
			cal.add(Calendar.DATE, -10);
			IMatchResult r = new SimpleScoreMatchResult(101L, cal.getTime(), "TestData1", 14, 28);
			r.setStatus(Status.FINAL_VISITOR_WIN);
			r.setId(id);
			return r;
		} else if (id == 8003) { // for match 102
			//Long matchID, Date recordedDate, String source, int homeScore, int visitScore
			cal.add(Calendar.DATE, -3);
			IMatchResult r = new SimpleScoreMatchResult(102L, cal.getTime(), "TestData1", 32, 32);
			r.setStatus(Status.FINAL_DRAW_OT);
			r.setId(id);
			return r;
		} else if (id == 8004) {  // for match 103
			//Long matchID, Date recordedDate, String source, int homeScore, int visitScore
			cal.add(Calendar.DATE, -2);
			IMatchResult r = new SimpleScoreMatchResult(103L, cal.getTime(), "TestData1", 21, 0);
			r.setStatus(Status.FINAL_HOME_WIN);
			r.setId(id);
			return r;
		} // Competition 2
		else if (id == 8201) { // for match 200
			//Long matchID, Date recordedDate, String source, int homeScore, int visitScore
			cal.add(Calendar.DATE, -7);
			IMatchResult r = new SimpleScoreMatchResult(200L, cal.getTime(), "TestData2", 70, 10);
			r.setStatus(Status.FINAL_HOME_WIN);
			r.setId(id);
			return r;
		} else if (id == 8202) { // for match 201
			//Long matchID, Date recordedDate, String source, int homeScore, int visitScore
			cal.add(Calendar.DATE, -7);
			IMatchResult r = new SimpleScoreMatchResult(201L, cal.getTime(), "TestData2", 14, 31);
			r.setStatus(Status.FINAL_VISITOR_WIN);
			r.setId(id);
			return r;
		} else if (id == 8203) { // for match 202
			//Long matchID, Date recordedDate, String source, int homeScore, int visitScore
			cal.add(Calendar.DATE, -8);
			IMatchResult r = new SimpleScoreMatchResult(202L, cal.getTime(), "TestData2", 6,22);
			r.setStatus(Status.FINAL_VISITOR_WIN);
			r.setId(id);
			return r;
		} else if (id == 8204) { // for match 203
			//Long matchID, Date recordedDate, String source, int homeScore, int visitScore
			cal.add(Calendar.DATE, -1);
			IMatchResult r = new SimpleScoreMatchResult(203L, cal.getTime(), "TestData2", 34, 28);
			r.setStatus(Status.FINAL_HOME_WIN);
			r.setId(id);
			return r;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.IMatchResultFactory#put(net.rugby.foundation.model.shared.IMatchResult)
	 */
	@Override
	public IMatchResult put(IMatchResult g) {

		return g;
	}

	@Override
	public boolean delete(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

}
