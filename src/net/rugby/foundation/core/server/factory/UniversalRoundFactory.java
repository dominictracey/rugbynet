package net.rugby.foundation.core.server.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.UniversalRound;


public class UniversalRoundFactory extends BaseCachingFactory<UniversalRound> implements IUniversalRoundFactory {

	private final String cacheId20 = "BRF-20";
	private final String cacheId52 = "BRF-52";
	
	@Override
	public UniversalRound get(IRound r) {
		DateTime rTime = new DateTime(r.getBegin());
		return get(rTime);
	}
	
	@Override
	public UniversalRound get(DateTime rTime) {
		DateTime midnight = rTime.withTimeAtStartOfDay();

		// get it back to midnight of the Wednesday before
		while (midnight.getDayOfWeek() != 3) {
			midnight = midnight.minusDays(1);
		}
		
		// now figure out how many weeks from January 7, 1970 (the first wednesday of rugby ;) )
		DateTime epoch = new DateTime(1970, 1, 7, 0, 0).withTimeAtStartOfDay();

		Weeks weeks = Weeks.weeksBetween(epoch, midnight);
		
		int ordinal = weeks.getWeeks();
		DateTimeFormatter fmtShort = DateTimeFormat.forStyle("M-").withLocale(Locale.UK);
		UniversalRound retval = new UniversalRound(ordinal, midnight.getYear() + "-" + midnight.getWeekOfWeekyear(), midnight.toString(fmtShort), "Week starting " + midnight.toString(fmtShort), midnight.toDate());		
		
		return retval;
	}
	
	@Override
	public UniversalRound get(int ordinal) {
		// now figure out how many weeks from January 7, 1970 (the first wednesday of rugby ;) )
		DateTime time = new DateTime(1970, 1, 7, 0, 0).withTimeAtStartOfDay();

		time = time.plusWeeks(ordinal);

		DateTimeFormatter fmtShort = DateTimeFormat.forStyle("M-").withLocale(Locale.UK);
		UniversalRound retval = new UniversalRound(ordinal, time.getYear() + "-" + time.getWeekOfWeekyear(), time.toString(fmtShort) + "", "Week starting " + time.toString(fmtShort), time.toDate());
				

		return retval;
	}
	
	
	@Override
	public List<UniversalRound> lastTwentyUniversalRounds() {
		try {
			List<UniversalRound> list = null;
	
			list = getList(cacheId20);
			

			if (list == null) {
				list = build(20);

				if (list != null) {
					putList(cacheId20, list);
				}	else {
					return null;
				}
			} 
			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public List<UniversalRound> lastYearUniversalRounds() {
		try {
			List<UniversalRound> list = null;
	
			list = getList(cacheId52);
			

			if (list == null) {
				list = build(52);

				if (list != null) {
					putList(cacheId52, list);
				}	else {
					return null;
				}
			} 
			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}
	
	private List<UniversalRound> build(int i) {
		List<UniversalRound> list = new ArrayList<UniversalRound>();
		DateTime now = DateTime.now().plusMonths(1);
		
		for (int n = 0; n < i; n++) {
			list.add(get(now));
			now = now.minusWeeks(1);
		}
		
		return list;
	}

	@Override
	public UniversalRound create() {
		// don't call this plz
		return null;
	}

	@Override
	protected UniversalRound getFromPersistentDatastore(Long id) {
		return get(id);
	}

	@Override
	protected UniversalRound putToPersistentDatastore(UniversalRound t) {
		// shouldn't be calling this?
		return t;
	}

	@Override
	protected boolean deleteFromPersistentDatastore(UniversalRound t) {
		// nor this?
		return false;
	}

	@Override
	public UniversalRound getCurrent() {
		return get(DateTime.now());
	}
}
