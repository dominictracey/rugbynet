package net.rugby.foundation.game1.server.factory.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import net.rugby.foundation.game1.server.factory.IEntryFactory;
import net.rugby.foundation.game1.server.factory.IRoundEntryFactory;
import net.rugby.foundation.game1.shared.Entry;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.game1.shared.IRoundEntry;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRound;

import com.google.inject.Inject;


public class TestEntryFactory implements IEntryFactory {
	private Long id;
	//private Long userId;
	//private Long compId;
	private final IRoundEntryFactory ref;
	private IRound round;
	private ICompetition comp;
	private Long userId;
	private Long compId;
	private String name;
	@Inject
	TestEntryFactory(IRoundEntryFactory ref) {
		this.ref = ref;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IEntryFactory#getEntry()
	 */
	@Override
	public IEntry getEntry() {

		if (name != null && comp!= null && userId != null)
			return getEntryByName();

		IEntry e = new Entry();
		e.setRoundEntryIdList(new ArrayList<Long>());
		e.setId(id);
		e.setCompId(1L);
		e.setCreated(new Date());
		e.setLastUpdated(new Date());
		if (id == 6000L) {
			e.setName("A10-Rugby.netChampionships-1");
			e.setOwnerId(7000L);
			e.getRoundEntryIdList().add(5001L);
			e.getRoundEntryIdList().add(5002L);
			buildRoundEntryList(e);
		} else if (id == 6001L) {
			e.setName("A11-Rugby.netChampionships-1");
			e.setOwnerId(7001L);
			e.getRoundEntryIdList().add(5101L);
			e.getRoundEntryIdList().add(5102L);
			buildRoundEntryList(e);
		} else if (id == 6002L) {
			e.setName("A12-Rugby.netChampionships-1");
			e.setOwnerId(7002L);
			e.getRoundEntryIdList().add(5201L);
			buildRoundEntryList(e);
		} else if (id == 6003L) {
			e.setName("A13-Rugby.netChampionships-1");
			e.setOwnerId(7003L);
			e.getRoundEntryIdList().add(5301L);
			e.getRoundEntryIdList().add(5302L);
			buildRoundEntryList(e);
		} else if (id == 6004L) {
			e.setName("A14-Rugby.netChampionships-1");
			e.setOwnerId(7004L);
			e.getRoundEntryIdList().add(5402L);
			buildRoundEntryList(e);
		} else if (id == 6005L) {
			e.setName("A15-Rugby.netChampionships-1");
			e.setOwnerId(7005L);
			e.getRoundEntryIdList().add(5502L);
			buildRoundEntryList(e);
		} else if (id == 6006L) {
			e.setName("A16-Rugby.netChampionships-1");
			e.setOwnerId(7006L);
			e.getRoundEntryIdList().add(5601L);
			buildRoundEntryList(e);
		} else if (id == 6007L) {
			e.setName("A16-Rugby.netChampionships-2");
			e.setOwnerId(7006L);
			e.getRoundEntryIdList().add(5701L);
			e.getRoundEntryIdList().add(5702L);
			buildRoundEntryList(e);
		} else if (id == 6100L) {
			e.setName("A10-HeinekenCup-1");
			e.setOwnerId(7000L);
			e.getRoundEntryIdList().add(15101L);
			e.getRoundEntryIdList().add(15102L);
			e.getRoundEntryIdList().add(15103L);
			buildRoundEntryList(e);
		} else if (id == 6101L) {
			e.setName("A11-HeinekenCup-1");
			e.setOwnerId(7001L);
			e.getRoundEntryIdList().add(15201L);
			e.getRoundEntryIdList().add(15202L);
			buildRoundEntryList(e);
		} else if (id == 6102L) {
			e.setName("A12-HeinekenCup-1");
			e.setOwnerId(7002L);
			e.getRoundEntryIdList().add(15301L);
			e.getRoundEntryIdList().add(15302L);
			buildRoundEntryList(e);
		} else if (id == 6103L) {
			e.setName("A13-HeinekenCup-1");
			e.setOwnerId(7003L);
			e.getRoundEntryIdList().add(15401L);
			e.getRoundEntryIdList().add(15402L);
			buildRoundEntryList(e);
		} else if (id == 6104L) {
			e.setName("A14-HeinekenCup-1");
			e.setOwnerId(7004L);
			e.getRoundEntryIdList().add(15501L);
			e.getRoundEntryIdList().add(15502L);
			buildRoundEntryList(e);
		} else if (id == 6105L) {
			e.setName("A15-HeinekenCup-1");
			e.setOwnerId(7005L);
			e.getRoundEntryIdList().add(15601L);
			e.getRoundEntryIdList().add(15602L);
			buildRoundEntryList(e);
		} else if (id == 6106L) {
			e.setName("A16-HeinekenCup-1");
			e.setOwnerId(7006L);
			e.getRoundEntryIdList().add(15701L);
			e.getRoundEntryIdList().add(15702L);
			e.getRoundEntryIdList().add(15703L);
			buildRoundEntryList(e);
		}

		if (id >6050L){
			e.setCompId(2L);
		}
		return e;
	}

	/**
	 * @return
	 */
	private IEntry getEntryByName() {

//		comp = null;
//		userId = null;
		if (name == "A10-Rugby.netChampionships-1") {
			setId(6000L);
		} else if (name == "A11-Rugby.netChampionships-1") {
			setId(6001L);
		} else if (name == "A12-Rugby.netChampionships-1") {
			setId(6002L);
		} else if (name == "A13-Rugby.netChampionships-1") {
			setId(6003L);
		} else if (name == "A14-Rugby.netChampionships-1") {
			setId(6004L);
		} else if (name == "A15-Rugby.netChampionships-1") {
			setId(6005L);
		} else if (name == "A16-Rugby.netChampionships-1") {
			setId(6006L);
		} else if (name == "A16-Rugby.netChampionships-2") {
			setId(6007L);
		} else if (name == "A10-HeinekenCup-1") {
			setId(6100L);
		} else if (name == "A11-HeinekenCup-1") {
			setId(6101L);
		} else if (name == "A12-HeinekenCup-1") {
			setId(6102L);
		} else if (name == "A13-HeinekenCup-1") {
			setId(6103L);
		} else if (name == "A14-HeinekenCup-1") {
			setId(6104L);
		} else if (name == "A15-HeinekenCup-1") {
			setId(6105L);
		} else if (name == "A16-HeinekenCup-1") {
			setId(6106L);
		}

		return getEntry();
	}

	/**
	 * @param e
	 */
	private void buildRoundEntryList(IEntry e) {
		e.setRoundEntries(new HashMap<Long, IRoundEntry>());
		for (Long reid : e.getRoundEntryIdList()) {
			ref.setId(reid);
			IRoundEntry g = ref.getRoundEntry();
			e.getRoundEntries().put(g.getRoundId(),g);
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IEntryFactory#put(net.rugby.foundation.game1.shared.IEntry)
	 */
	@Override
	public IEntry put(IEntry e) {

		return e;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IEntryFactory#setUserIdAndCompId(java.lang.Long, java.lang.Long)
	 */
	@Override
	public void setUserIdAndCompId(Long userId, Long compId) {
		this.userId = userId;
		this.compId = compId;
		this.name = null;
		this.id = null;

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IEntryFactory#getEntries()
	 */
	@Override
	public ArrayList<IEntry> getEntries() {

		ArrayList<IEntry> entries = new ArrayList<IEntry>();

		//TODO Need to put them all in here
		if (userId != null && compId != null) {
			if (userId < 7006L) {
				if (compId == 1L) {
					setId(userId - 1000L);
				} else if  (compId == 2L) {
					setId(userId - 1000L + 100L);
				}
				entries.add(getEntry());
			} else if (userId.equals(7006L)) {
				if (compId == 1L) {
					setId(6006L);
					entries.add(getEntry());
					setId(6007L);
					entries.add(getEntry());
				} else if  (compId == 2L) {
					setId(6106L);
					entries.add(getEntry());
				}
			}		
			return entries;
		} 

		// round == null means get all the entries
		if ( comp != null) {
			if (comp.getId() == 1L) {
				if (round == null) {
					setId(6000L);
					entries.add(getEntry());
					setId(6001L);
					entries.add(getEntry());
					setId(6002L);
					entries.add(getEntry());
					setId(6003L);
					entries.add(getEntry());
					setId(6004L);
					entries.add(getEntry());
					setId(6005L);
					entries.add(getEntry());
					setId(6006L);
					entries.add(getEntry());
					setId(6007L);
					entries.add(getEntry());
				} else if ( round.getId() == 2L) {
					setId(6000L);
					entries.add(getEntry());
					setId(6001L);
					entries.add(getEntry());
					setId(6002L);
					entries.add(getEntry());
					setId(6003L);
					entries.add(getEntry());
					setId(6006L);
					entries.add(getEntry());
				} else if (round.getId() == 3L) {
					setId(6000L);
					entries.add(getEntry());
					setId(6001L);
					entries.add(getEntry());
					setId(6003L);
					entries.add(getEntry());
					setId(6004L);
					entries.add(getEntry());
					setId(6005L);
					entries.add(getEntry());
					setId(6007L);
					entries.add(getEntry());
				}
			}	else if (comp.getId() == 2L) {
				if (round == null) {
					setId(6100L);
					entries.add(getEntry());
					setId(6101L);
					entries.add(getEntry());
					setId(6102L);
					entries.add(getEntry());
					setId(6103L);
					entries.add(getEntry());
					setId(6104L);
					entries.add(getEntry());
					setId(6105L);
					entries.add(getEntry());
					setId(6106L);
					entries.add(getEntry());
				} 	
			}
		}

		return entries;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IEntryFactory#delete()
	 */
	@Override
	public Boolean delete() {

		return false;

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IEntryFactory#setRoundAndComp(net.rugby.foundation.model.shared.IRound, net.rugby.foundation.model.shared.ICompetition)
	 */
	@Override
	public void setRoundAndComp(IRound round, ICompetition comp) {
		this.round = round;
		this.comp = comp;
		this.userId = null;
		this.name = null;
	}

	@Override
	public void setNameAndComp(String name, ICompetition comp, Long userId) {
		this.name = name;
		this.comp = comp;
		this.userId = userId;
		this.round = null;
		this.id = null;
	}

}
