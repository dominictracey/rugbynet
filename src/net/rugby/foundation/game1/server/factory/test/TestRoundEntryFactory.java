package net.rugby.foundation.game1.server.factory.test;

import java.util.HashSet;
import java.util.Set;

import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.game1.server.factory.IMatchEntryFactory;
import net.rugby.foundation.game1.server.factory.IRoundEntryFactory;
import net.rugby.foundation.game1.shared.IMatchEntry;
import net.rugby.foundation.game1.shared.IRoundEntry;
import net.rugby.foundation.game1.shared.RoundEntry;
import com.google.inject.Inject;


public class TestRoundEntryFactory implements IRoundEntryFactory {
	private Long id;
	private IMatchEntryFactory imf;
	private IMatchGroupFactory mgf;
	
	@Inject
	TestRoundEntryFactory(IMatchEntryFactory imf, IMatchGroupFactory mgf) {
		this.imf = imf;
		this.mgf = mgf;
	}
	
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IRoundEntryFactory#getRoundEntry()
	 */
	@Override
	public IRoundEntry getRoundEntry() {
		
		IRoundEntry re = new RoundEntry();
		re.setId(id);

		if (id == 5001) {
			re.setRoundId(2L);
			re.setTieBreakerMatchId(101L);
			re.setTieBreakerVisitScore(12);
			re.setTieBreakerHomeScore(19);
			re.getMatchPickIdList().add(6000100L);
			re.getMatchPickIdList().add(6000101L);
		} else if (id == 5002) {
			re.setRoundId(3L);
			re.setTieBreakerMatchId(103L);
			re.setTieBreakerVisitScore(3);
			re.setTieBreakerHomeScore(12);
			re.getMatchPickIdList().add(6000102L);
			re.getMatchPickIdList().add(6000103L);
		} else if (id == 5101) {
			re.setRoundId(2L);
			re.setTieBreakerMatchId(101L);
			re.setTieBreakerVisitScore(23);
			re.setTieBreakerHomeScore(42);
			re.getMatchPickIdList().add(6001100L);
			re.getMatchPickIdList().add(6001101L);
		} else if (id == 5102) {
			re.setRoundId(3L);
			re.setTieBreakerMatchId(103L);
			re.setTieBreakerVisitScore(33);
			re.setTieBreakerHomeScore(42);
			re.getMatchPickIdList().add(6001102L);
			re.getMatchPickIdList().add(6001103L);
		} else if (id == 5201) {
			re.setRoundId(2L);
			re.setTieBreakerMatchId(101L);
			re.setTieBreakerVisitScore(13);
			re.setTieBreakerHomeScore(19);
			re.getMatchPickIdList().add(6002100L);
			re.getMatchPickIdList().add(6002101L);
		} else if (id == 5301) {
			re.setRoundId(2L);
			re.setTieBreakerMatchId(101L);
			re.setTieBreakerVisitScore(23);
			re.setTieBreakerHomeScore(39);
			re.getMatchPickIdList().add(6003100L);
			re.getMatchPickIdList().add(6003101L);
		} else if (id == 5302) {
			re.setRoundId(3L);
			re.setTieBreakerMatchId(103L);
			re.setTieBreakerVisitScore(33);
			re.setTieBreakerHomeScore(42);
			re.getMatchPickIdList().add(6003102L);
			re.getMatchPickIdList().add(6003103L);
		} else if (id == 5402) {
			re.setRoundId(3L);
			re.setTieBreakerMatchId(103L);
			re.setTieBreakerVisitScore(16);
			re.setTieBreakerHomeScore(28);
			re.getMatchPickIdList().add(6004102L);
			re.getMatchPickIdList().add(6004103L);
		} else if (id == 5502) {
			re.setRoundId(3L);
			re.setTieBreakerMatchId(103L);
			re.setTieBreakerVisitScore(11);
			re.setTieBreakerHomeScore(56);
			re.getMatchPickIdList().add(6005102L);
			re.getMatchPickIdList().add(6005103L);
		} else if (id == 5601) {
			re.setRoundId(2L);
			re.setTieBreakerMatchId(101L);
			re.setTieBreakerVisitScore(23);
			re.setTieBreakerHomeScore(24);
			re.getMatchPickIdList().add(6006100L);
			re.getMatchPickIdList().add(6006101L);
		} else if (id == 5701) {
			re.setRoundId(2L);
			re.setTieBreakerMatchId(101L);
			re.setTieBreakerVisitScore(3);
			re.setTieBreakerHomeScore(24);
			re.getMatchPickIdList().add(6007100L);
			re.getMatchPickIdList().add(6007101L);
		} else if (id == 5702) {
			re.setRoundId(3L);
			re.setTieBreakerMatchId(103L);
			re.setTieBreakerVisitScore(33);
			re.setTieBreakerHomeScore(41);
			re.getMatchPickIdList().add(6007102L);
			re.getMatchPickIdList().add(6007103L);
		} else if (id == 15101) {
			re.setRoundId(12L);
			re.setTieBreakerMatchId(202L);
			re.setTieBreakerVisitScore(20);
			re.setTieBreakerHomeScore(10);
			re.getMatchPickIdList().add(6100200L);
			re.getMatchPickIdList().add(6100201L);
			re.getMatchPickIdList().add(6100202L);
		} else if (id == 15102) {
			re.setRoundId(13L);
			re.setTieBreakerMatchId(205L);
			re.setTieBreakerVisitScore(14);
			re.setTieBreakerHomeScore(42);
			re.getMatchPickIdList().add(6100203L);
			re.getMatchPickIdList().add(6100204L);
			re.getMatchPickIdList().add(6100205L);
		} else if (id == 15103) {
			re.setRoundId(14L);
			re.setTieBreakerMatchId(207L);
			re.setTieBreakerVisitScore(14);
			re.setTieBreakerHomeScore(42);
			re.getMatchPickIdList().add(6100206L);
			re.getMatchPickIdList().add(6100207L);
		} else if (id == 15201) {
			re.setRoundId(12L);
			re.setTieBreakerMatchId(202L);
			re.setTieBreakerVisitScore(26);
			re.setTieBreakerHomeScore(21);
			re.getMatchPickIdList().add(6101200L);
			re.getMatchPickIdList().add(6101201L);
			re.getMatchPickIdList().add(6101202L);
		} else if (id == 15202) {
			re.setRoundId(13L);
			re.setTieBreakerMatchId(205L);
			re.setTieBreakerVisitScore(13);
			re.setTieBreakerHomeScore(39);
			re.getMatchPickIdList().add(6101203L);
			re.getMatchPickIdList().add(6101204L);
			re.getMatchPickIdList().add(6101205L);
		} else if (id == 15301) {
			re.setRoundId(12L);
			re.setTieBreakerMatchId(202L);
			re.setTieBreakerVisitScore(30);
			re.setTieBreakerHomeScore(42);
			re.getMatchPickIdList().add(6102200L);
			re.getMatchPickIdList().add(6102201L);
			re.getMatchPickIdList().add(6102202L);
		} else if (id == 15302) {
			re.setRoundId(13L);
			re.setTieBreakerMatchId(205L);
			re.setTieBreakerVisitScore(3);
			re.setTieBreakerHomeScore(6);
			re.getMatchPickIdList().add(6102203L);
			re.getMatchPickIdList().add(6102204L);
			re.getMatchPickIdList().add(6102205L);
		} else if (id == 15401) {
			re.setRoundId(12L);
			re.setTieBreakerMatchId(202L);
			re.setTieBreakerVisitScore(16);
			re.setTieBreakerHomeScore(42);
			re.getMatchPickIdList().add(6103200L);
			re.getMatchPickIdList().add(6103201L);
			re.getMatchPickIdList().add(6103202L);
		} else if (id == 15402) {
			re.setRoundId(13L);
			re.setTieBreakerMatchId(205L);
			re.setTieBreakerVisitScore(23);
			re.setTieBreakerHomeScore(26);
			re.getMatchPickIdList().add(6103203L);
			re.getMatchPickIdList().add(6103204L);
			re.getMatchPickIdList().add(6103205L);
		} else if (id == 15501) {
			re.setRoundId(12L);
			re.setTieBreakerMatchId(202L);
			// This tiebreaker pick is one point closer than the one it's tied with (15101)
			re.setTieBreakerVisitScore(20);
			re.setTieBreakerHomeScore(11);
			re.getMatchPickIdList().add(6104200L);
			re.getMatchPickIdList().add(6104201L);
			re.getMatchPickIdList().add(6104202L);
		} else if (id == 15502) {
			re.setRoundId(13L);
			re.setTieBreakerMatchId(205L);
			re.setTieBreakerVisitScore(3);
			re.setTieBreakerHomeScore(33);
			re.getMatchPickIdList().add(6104203L);
			re.getMatchPickIdList().add(6104204L);
			re.getMatchPickIdList().add(6104205L);
		} else if (id == 15601) {
			re.setRoundId(12L);
			re.setTieBreakerMatchId(202L);
			re.setTieBreakerVisitScore(43);
			re.setTieBreakerHomeScore(56);
			re.getMatchPickIdList().add(6105200L);
			re.getMatchPickIdList().add(6105201L);
			re.getMatchPickIdList().add(6105202L);
		} else if (id == 15602) {
			re.setRoundId(13L);
			re.setTieBreakerMatchId(205L);
			re.setTieBreakerVisitScore(10);
			re.setTieBreakerHomeScore(43);
			re.getMatchPickIdList().add(6105203L);
			re.getMatchPickIdList().add(6105204L);
			re.getMatchPickIdList().add(6105205L);
		} else if (id == 15701) {
			re.setRoundId(12L);
			re.setTieBreakerMatchId(202L);
			re.setTieBreakerVisitScore(10);
			re.setTieBreakerHomeScore(43);
			re.getMatchPickIdList().add(6106200L);
			re.getMatchPickIdList().add(6106201L);
			re.getMatchPickIdList().add(6106202L);
		} else if (id == 15702) {
			re.setRoundId(13L);
			re.setTieBreakerMatchId(205L);
			re.setTieBreakerVisitScore(10);
			re.setTieBreakerHomeScore(43);
			re.getMatchPickIdList().add(6106203L);
			re.getMatchPickIdList().add(6106204L);
			re.getMatchPickIdList().add(6106205L);
		} else if (id == 15703) {
			re.setRoundId(14L);
			re.setTieBreakerMatchId(207L);
			re.setTieBreakerVisitScore(7);
			re.setTieBreakerHomeScore(31);
			re.getMatchPickIdList().add(6106206L);
			re.getMatchPickIdList().add(6106207L);
		}

		buildMatchEntryList(re);
		mgf.setId(re.getTieBreakerMatchId());
		re.setTieBreakerMatch(mgf.getGame());
		return re;
	}
	
	private void buildMatchEntryList(IRoundEntry re) {
		for (Long mid : re.getMatchPickIdList()) {
			imf.setId(mid);
			IMatchEntry ime = imf.getMatchEntry();
			re.getMatchPickMap().put(ime.getMatchId(),ime);
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IRoundEntryFactory#put(net.rugby.foundation.game1.shared.IRoundEntry)
	 */
	@Override
	public IRoundEntry put(IRoundEntry re) {
		
		return re;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IRoundEntryFactory#delete()
	 */
	@Override
	public Boolean delete() {
		
		return false;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IRoundEntryFactory#getNew()
	 */
	@Override
	public IRoundEntry getNew() {
		return new RoundEntry();
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IRoundEntryFactory#getAll()
	 */
	@Override
	public Set<IRoundEntry> getAll() {
		Set<IRoundEntry> set = new HashSet<IRoundEntry>();
		
		setId(5001L);
		set.add(getRoundEntry());
		setId(5002L);
		set.add(getRoundEntry());
		setId(5101L);
		set.add(getRoundEntry());
		setId(5102L);
		set.add(getRoundEntry());
		setId(5201L);
		set.add(getRoundEntry());
		setId(5301L);
		set.add(getRoundEntry());
		setId(5302L);
		set.add(getRoundEntry());
		setId(5402L);
		set.add(getRoundEntry());
		setId(5502L);
		set.add(getRoundEntry());
		setId(5601L);
		set.add(getRoundEntry());
		setId(5701L);
		set.add(getRoundEntry());
		setId(5702L);
		set.add(getRoundEntry());
		setId(15101L);
		set.add(getRoundEntry());
		setId(15102L);
		set.add(getRoundEntry());
		setId(15103L);
		set.add(getRoundEntry());
		setId(15201L);
		set.add(getRoundEntry());
		setId(15202L);
		set.add(getRoundEntry());
		setId(15301L);
		set.add(getRoundEntry());
		setId(15302L);
		set.add(getRoundEntry());
		setId(15401L);
		set.add(getRoundEntry());
		setId(15402L);
		set.add(getRoundEntry());
		setId(15501L);
		set.add(getRoundEntry());
		setId(15502L);
		set.add(getRoundEntry());
		setId(15601L);
		set.add(getRoundEntry());
		setId(15602L);
		set.add(getRoundEntry());
		setId(15701L);
		set.add(getRoundEntry());
		setId(15702L);
		set.add(getRoundEntry());
		setId(15703L);
		set.add(getRoundEntry());

		return set;
	}

}
