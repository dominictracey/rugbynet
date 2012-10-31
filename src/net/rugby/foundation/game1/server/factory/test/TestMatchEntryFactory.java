/**
 * 
 */
package net.rugby.foundation.game1.server.factory.test;

import java.util.HashSet;
import java.util.Set;

import com.google.inject.Inject;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.game1.server.factory.IMatchEntryFactory;
import net.rugby.foundation.game1.shared.IMatchEntry;
import net.rugby.foundation.game1.shared.MatchEntry;

/**
 * @author home
 *
 */
public class TestMatchEntryFactory implements IMatchEntryFactory {
	private Long id;
	private final ITeamGroupFactory tf;
	
	@Inject
	TestMatchEntryFactory(ITeamGroupFactory tf) {
		this.tf = tf;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IMatchEntryFactory#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IMatchEntryFactory#getMatchEntry()
	 */
	@Override
	public IMatchEntry getMatchEntry() {
		IMatchEntry me = new MatchEntry();
		me.setId(id);
		if (id == 6000100) {
			me.setMatchId(100L);
			me.setTeamPickedId(9001L);
		} else if (id == 6000101) {
			me.setMatchId(101L);
			me.setTeamPickedId(9003L);
		} else if (id == 6000102) {
			me.setMatchId(102L);
			me.setTeamPickedId(9001L);
		} else if (id == 6000103) {
			me.setMatchId(103L);
			me.setTeamPickedId(9006L);
		} else if (id == 6001100) {
			me.setMatchId(100L);
			me.setTeamPickedId(9002L);
		} else if (id == 6001101) {
			me.setMatchId(101L);
			me.setTeamPickedId(9003L);
		} else if (id == 6001102) {
			me.setMatchId(102L);
			me.setTeamPickedId(9004L);
		} else if (id == 6001103) {
			me.setMatchId(103L);
			me.setTeamPickedId(9006L);
		} else if (id == 6002100) {
			me.setMatchId(100L);
			me.setTeamPickedId(9002L);
		} else if (id == 6002101) {
			me.setMatchId(101L);
			me.setTeamPickedId(9003L);
		} else if (id == 6003100) {
			me.setMatchId(100L);
			me.setTeamPickedId(9001L);
		} else if (id == 6003101) {
			me.setMatchId(101L);
			me.setTeamPickedId(9004L);
		} else if (id == 6003102) {
			me.setMatchId(102L);
			me.setTeamPickedId(9001L);
		} else if (id == 6003103) {
			me.setMatchId(103L);
			me.setTeamPickedId(9005L);
		} else if (id == 6004102) {
			me.setMatchId(102L);
			me.setTeamPickedId(9004L);
		} else if (id == 6004103) {
			me.setMatchId(103L);
			me.setTeamPickedId(9006L);
		} else if (id == 6005102) {
			me.setMatchId(102L);
			me.setTeamPickedId(9001L);
		} else if (id == 6005103) {
			me.setMatchId(103L);
			me.setTeamPickedId(9005L);
		}else if (id == 6006100) {
			me.setMatchId(100L);
			me.setTeamPickedId(9002L);
		} else if (id == 6006101) {
			me.setMatchId(101L);
			me.setTeamPickedId(9004L);
		}else if (id == 6007100) {
			me.setMatchId(100L);
			me.setTeamPickedId(9002L);
		} else if (id == 6007101) {
			me.setMatchId(101L);
			me.setTeamPickedId(9004L);
		} else if (id == 6007102) {
			me.setMatchId(102L);
			me.setTeamPickedId(9004L);
		} else if (id == 6007103) {
			me.setMatchId(103L);
			me.setTeamPickedId(9005L);
			// COMP 2
		} else if (id == 6100200) {
			me.setMatchId(id - 6100000L);
			me.setTeamPickedId(9201L);
		} else if (id == 6100201) {
			me.setMatchId(id - 6100000L);
			me.setTeamPickedId(9204L);
		} else if (id == 6100202) {
			me.setMatchId(id - 6100000L);
			me.setTeamPickedId(9206L);
		} else if (id == 6100203) {
			me.setMatchId(id - 6100000L);
			me.setTeamPickedId(9207L);
		} else if (id == 6100204) {
			me.setMatchId(id - 6100000L);
			me.setTeamPickedId(9201L);
		} else if (id == 6100205) {
			me.setMatchId(id - 6100000L);
			me.setTeamPickedId(9207L);
		} else if (id == 6100206) {
			me.setMatchId(id - 6100000L);
			me.setTeamPickedId(9206L);
		} else if (id == 6100207) {
			me.setMatchId(id - 6100000L);
			me.setTeamPickedId(9205L);
		} else if (id == 6101200) {
			me.setMatchId(id - 6101000L);
			me.setTeamPickedId(9201L);
		} else if (id == 6101201) {
			me.setMatchId(id - 6101000L);
			me.setTeamPickedId(9203L);
		} else if (id == 6101202) {
			me.setMatchId(id - 6101000L);
			me.setTeamPickedId(9206L);
		} else if (id == 6101203) {
			me.setMatchId(id - 6101000L);
			me.setTeamPickedId(9208L);
		} else if (id == 6101204) {
			me.setMatchId(id - 6101000L);
			me.setTeamPickedId(9201L);
		} else if (id == 6101205) {
			me.setMatchId(id - 6101000L);
			me.setTeamPickedId(9202L);
		} else if (id == 6102200) {
			me.setMatchId(id - 6102000L);
			me.setTeamPickedId(9202L);
		} else if (id == 6102201) {
			me.setMatchId(id - 6102000L);
			me.setTeamPickedId(9204L);
		} else if (id == 6102202) {
			me.setMatchId(id - 6102000L);
			me.setTeamPickedId(9205L);
		} else if (id == 6102203) {
			me.setMatchId(id - 6102000L);
			me.setTeamPickedId(9208L);
		} else if (id == 6102204) {
			me.setMatchId(id - 6102000L);
			me.setTeamPickedId(9201L);
		} else if (id == 6102205) {
			me.setMatchId(id - 6102000L);
			me.setTeamPickedId(9207L);
		} else if (id == 6103200) {
			me.setMatchId(id - 6103000L);
			me.setTeamPickedId(9202L);
		} else if (id == 6103201) {
			me.setMatchId(id - 6103000L);
			me.setTeamPickedId(9203L);
		} else if (id == 6103202) {
			me.setMatchId(id - 6103000L);
			me.setTeamPickedId(9205L);
		} else if (id == 6103203) {
			me.setMatchId(id - 6103000L);
			me.setTeamPickedId(9207L);
		} else if (id == 6103204) {
			me.setMatchId(id - 6103000L);
			me.setTeamPickedId(9208L);
		} else if (id == 6103205) {
			me.setMatchId(id - 6103000L);
			me.setTeamPickedId(9202L);
		} else if (id == 6104200) {
			me.setMatchId(id - 6104000L);
			me.setTeamPickedId(9201L);
		} else if (id == 6104201) {
			me.setMatchId(id - 6104000L);
			me.setTeamPickedId(9204L);
		} else if (id == 6104202) {
			me.setMatchId(id - 6104000L);
			me.setTeamPickedId(9206L);
		} else if (id == 6104203) {
			me.setMatchId(id - 6104000L);
			me.setTeamPickedId(9207L);
		} else if (id == 6104204) {
			me.setMatchId(id - 6104000L);
			me.setTeamPickedId(9208L);
		} else if (id == 6104205) {
			me.setMatchId(id - 6104000L);
			me.setTeamPickedId(9202L);
		} else if (id == 6105200) {
			me.setMatchId(id - 6105000L);
			me.setTeamPickedId(9201L);
		} else if (id == 6105201) {
			me.setMatchId(id - 6105000L);
			me.setTeamPickedId(9204L);
		} else if (id == 6105202) {
			me.setMatchId(id - 6105000L);
			me.setTeamPickedId(9205L);
		} else if (id == 6105203) {
			me.setMatchId(id - 6105000L);
			me.setTeamPickedId(9208L);
		} else if (id == 6105204) {
			me.setMatchId(id - 6105000L);
			me.setTeamPickedId(9201L);
		} else if (id == 6105205) {
			me.setMatchId(id - 6105000L);
			me.setTeamPickedId(9202L);
		} else if (id == 6106200) {
			me.setMatchId(id - 6106000L);
			me.setTeamPickedId(9201L);
		} else if (id == 6106201) {
			me.setMatchId(id - 6106000L);
			me.setTeamPickedId(9203L);
		} else if (id == 6106202) {
			me.setMatchId(id - 6106000L);
			me.setTeamPickedId(9206L);
		} else if (id == 6106203) {
			me.setMatchId(id - 6106000L);
			me.setTeamPickedId(9207L);
		} else if (id == 6106204) {
			me.setMatchId(id - 6106000L);
			me.setTeamPickedId(9201L);
		} else if (id == 6106205) {
			me.setMatchId(id - 6106000L);
			me.setTeamPickedId(9207L);
		} else if (id == 6106206) {
			me.setMatchId(id - 6106000L);
			me.setTeamPickedId(9203L);
		} else if (id == 6106207) {
			me.setMatchId(id - 6106000L);
			me.setTeamPickedId(9204L);
		} else if (id == 6107200L) {  // not part of a round entry
			me.setMatchId(id - 6107000L);
			me.setTeamPickedId(9001L);
		}
		
		else return null;
		
		
		
		Long tid = me.getTeamPickedId();
		tf.setId(tid);
		me.setTeamPicked(tf.getTeam());
		return me;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IMatchEntryFactory#put(net.rugby.foundation.game1.shared.IMatchEntry)
	 */
	@Override
	public IMatchEntry put(IMatchEntry e) {

		return e;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IMatchEntryFactory#delete()
	 */
	@Override
	public Boolean delete() {

		return false;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IMatchEntryFactory#getAll()
	 */
	@Override
	public Set<IMatchEntry> getAll() {
		
		Set<IMatchEntry> set = new HashSet<IMatchEntry>();
		
		setId(6000100L);
		set.add(getMatchEntry());
		setId(6000101L);
		set.add(getMatchEntry());
		setId(6000102L);
		set.add(getMatchEntry());
		setId(6000103L);
		set.add(getMatchEntry());
		setId(6001100L);
		set.add(getMatchEntry());
		setId(6001101L);
		set.add(getMatchEntry());
		setId(6001102L);
		set.add(getMatchEntry());
		setId(6001103L);
		set.add(getMatchEntry());
		setId(6002100L);
		set.add(getMatchEntry());
		setId(6002101L);
		set.add(getMatchEntry());
		setId(6003100L);
		set.add(getMatchEntry());
		setId(6003101L);
		set.add(getMatchEntry());
		setId(6003102L);
		set.add(getMatchEntry());
		setId(6003103L);
		set.add(getMatchEntry());
		setId(6004102L);
		set.add(getMatchEntry());
		setId(6004103L);
		set.add(getMatchEntry());
		setId(6005102L);
		set.add(getMatchEntry());
		setId(6005103L);
		set.add(getMatchEntry());
		setId(6006100L);
		set.add(getMatchEntry());
		setId(6006101L);
		set.add(getMatchEntry());
		setId(6007100L);
		set.add(getMatchEntry());
		setId(6007101L);
		set.add(getMatchEntry());
		setId(6007102L);
		set.add(getMatchEntry());
		setId(6007103L);
		set.add(getMatchEntry());
			// COMP 2
		setId(6100200L);
		set.add(getMatchEntry());
		setId(6100201L);
		set.add(getMatchEntry());
		setId(6100202L);
		set.add(getMatchEntry());
		setId(6100203L);
		set.add(getMatchEntry());
		setId(6100204L);
		set.add(getMatchEntry());
		setId(6100205L);
		set.add(getMatchEntry());
		setId(6100206L);
		set.add(getMatchEntry());
		setId(6100207L);
		set.add(getMatchEntry());
		setId(6101200L);
		set.add(getMatchEntry());
		setId(6101201L);
		set.add(getMatchEntry());
		setId(6101202L);
		set.add(getMatchEntry());
		setId(6101203L);
		set.add(getMatchEntry());
		setId(6101204L);
		set.add(getMatchEntry());
		setId(6101205L);
		set.add(getMatchEntry());
		setId(6102200L);
		set.add(getMatchEntry());
		setId(6102201L);
		set.add(getMatchEntry());
		setId(6102202L);
		set.add(getMatchEntry());
		setId(6102203L);
		set.add(getMatchEntry());
		setId(6102204L);
		set.add(getMatchEntry());
		setId(6102205L);
		set.add(getMatchEntry());
		setId(6103200L);
		set.add(getMatchEntry());
		setId(6103201L);
		set.add(getMatchEntry());
		setId(6103202L);
		set.add(getMatchEntry());
		setId(6103203L);
		set.add(getMatchEntry());
		setId(6103204L);
		set.add(getMatchEntry());
		setId(6103205L);
		set.add(getMatchEntry());
		setId(6104200L);
		set.add(getMatchEntry());
		setId(6104201L);
		set.add(getMatchEntry());
		setId(6104202L);
		set.add(getMatchEntry());
		setId(6104203L);
		set.add(getMatchEntry());
		setId(6104204L);
		set.add(getMatchEntry());
		setId(6104205L);
		set.add(getMatchEntry());
		setId(6105200L);
		set.add(getMatchEntry());
		setId(6105201L);
		set.add(getMatchEntry());
		setId(6105202L);
		set.add(getMatchEntry());
		setId(6105203L);
		set.add(getMatchEntry());
		setId(6105204L);
		set.add(getMatchEntry());
		setId(6105205L);
		set.add(getMatchEntry());
		setId(6106200L);
		set.add(getMatchEntry());
		setId(6106201L);
		set.add(getMatchEntry());
		setId(6106202L);
		set.add(getMatchEntry());
		setId(6106203L);
		set.add(getMatchEntry());
		setId(6106204L);
		set.add(getMatchEntry());
		setId(6106205L);
		set.add(getMatchEntry());
		setId(6106206L);
		set.add(getMatchEntry());
		setId(6106207L);
		set.add(getMatchEntry());
		// bad ones
		setId(6107200L);
		set.add(getMatchEntry());

		return set;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IMatchEntryFactory#getForMatch(java.lang.Long)
	 */
	@Override
	public Set<IMatchEntry> getForMatch(Long matchId) {
		Set<IMatchEntry> mel = new HashSet<IMatchEntry>();
		if (matchId.equals(100L)) {
			setId(6000100L);
			mel.add(getMatchEntry());
			setId(6001100L);
			mel.add(getMatchEntry());
			setId(6002100L);
			mel.add(getMatchEntry());
			setId(6003100L);
			mel.add(getMatchEntry());
			setId(6006100L);
			mel.add(getMatchEntry());
			setId(6007100L);
			mel.add(getMatchEntry());
		} else if (matchId.equals(101L)) {
			setId(6000101L);
			mel.add(getMatchEntry());
			setId(6001101L);
			mel.add(getMatchEntry());
			setId(6002101L);
			mel.add(getMatchEntry());
			setId(6003101L);
			mel.add(getMatchEntry());
			setId(6006101L);
			mel.add(getMatchEntry());
			setId(6007101L);
			mel.add(getMatchEntry());
		} else if (matchId.equals(102L)) {
			setId(6000102L);
			mel.add(getMatchEntry());
			setId(6001102L);
			mel.add(getMatchEntry());
			setId(6003102L);
			mel.add(getMatchEntry());
			setId(6004102L);
			mel.add(getMatchEntry());
			setId(6005102L);
			mel.add(getMatchEntry());
			setId(6007102L);
			mel.add(getMatchEntry());
		}  else if (matchId.equals(103L)) {
			setId(6000103L);
			mel.add(getMatchEntry());
			setId(6001103L);
			mel.add(getMatchEntry());
			setId(6003103L);
			mel.add(getMatchEntry());
			setId(6004103L);
			mel.add(getMatchEntry());
			setId(6005103L);
			mel.add(getMatchEntry());
			setId(6007103L);
			mel.add(getMatchEntry());
		} 
		return mel;
	}

}
