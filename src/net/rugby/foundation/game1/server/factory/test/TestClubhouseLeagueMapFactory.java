/**
 * 
 */
package net.rugby.foundation.game1.server.factory.test;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory;
import net.rugby.foundation.game1.shared.ClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;

/**
 * @author home
 *
 */
public class TestClubhouseLeagueMapFactory implements IClubhouseLeagueMapFactory {

	private Long id;
	private List<IClubhouseLeagueMap> list;
	
	public TestClubhouseLeagueMapFactory() {
		//list = new ArrayList<IClubhouseLeagueMap>();
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory#get()
	 */
	@Override
	public IClubhouseLeagueMap get() {
		IClubhouseLeagueMap chlm = new ClubhouseLeagueMap();
		
		if (id == null) {
			return chlm;
		}
		
		chlm.setCompId(1L);  //only one comp currently
		chlm.setId(id);
		if (id.equals(50L)) {
			chlm.setClubhouseId(70L);			
			chlm.setLeagueId(80L);
		} else if (id.equals(51L)) {
			chlm.setClubhouseId(71L);			
			chlm.setLeagueId(81L);
		}  else if (id.equals(52L)) {
			chlm.setClubhouseId(72L);			
			chlm.setLeagueId(82L);
		} else if (id.equals(53L)) {
			chlm.setClubhouseId(75L);			
			chlm.setLeagueId(83L);
			chlm.setCompId(2L);
		} else if (id.equals(54L)) {
			chlm.setClubhouseId(71L);			
			chlm.setLeagueId(84L);
			chlm.setCompId(2L);
		}  else if (id.equals(55L)) {
			chlm.setClubhouseId(72L);			
			chlm.setLeagueId(85L);
			chlm.setCompId(2L);
		}   else if (id.equals(56L)) {
			chlm.setClubhouseId(74L);			
			chlm.setLeagueId(null);
			chlm.setCompId(1L);
		}    else if (id.equals(57L)) {  // invalid one
			chlm.setClubhouseId(9999L);			
			chlm.setLeagueId(null);
			chlm.setCompId(1L);
		} 
		
		return chlm;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory#put(net.rugby.foundation.game1.shared.IClubhouseLeagueMap)
	 */
	@Override
	public IClubhouseLeagueMap put(IClubhouseLeagueMap dl) {	
		return dl;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory#setClubhouseId(java.lang.Long)
	 */
	@Override
	public void setClubhouseId(Long clubhouseId) {
		list = new ArrayList<IClubhouseLeagueMap>();
		
		if (clubhouseId.equals(70L)) {
			setId(50L);
			list.add(get());
		} else if (clubhouseId.equals(71L)) {
			setId(51L);
			list.add(get());
		} else if (clubhouseId.equals(72L)) {
			setId(52L);
			list.add(get());
		} else if (clubhouseId.equals(75L)) {
			setId(53L);
			list.add(get());
		} else if (clubhouseId.equals(74L)) {
			setId(56L);
			list.add(get());
		}

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory#setCompetitionId(java.lang.Long)
	 */
	@Override
	public void setCompetitionId(Long competitionId) {
		list = new ArrayList<IClubhouseLeagueMap>();
		
		if (competitionId.equals(1L)) {
			setId(50L);
			list.add(get());
			setId(51L);
			list.add(get());
			setId(52L);
			list.add(get());
			setId(56L);
			list.add(get());
			setId(57L);
			list.add(get());
		}  else if (competitionId.equals(2L)) {
			setId(53L);
			list.add(get());
			setId(54L);
			list.add(get());
			setId(55L);
			list.add(get());
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory#setLeagueId(java.lang.Long)
	 */
	@Override
	public void setLeagueId(Long leagueId) {
		list = new ArrayList<IClubhouseLeagueMap>();
		
		if (leagueId.equals(80L)) {
			setId(50L);
			list.add(get());
		} else if (leagueId.equals(81L)) {
			setId(51L);
			list.add(get());
		} else if (leagueId.equals(82L)) {
			setId(52L);
			list.add(get());
		} else if (leagueId.equals(83L)) {
			setId(53L);
			list.add(get());
		} else if (leagueId.equals(84L)) {
			setId(54L);
			list.add(get());
		} else if (leagueId.equals(85L)) {
			setId(55L);
			list.add(get());
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory#getList()
	 */
	@Override
	public List<IClubhouseLeagueMap> getList() {
		return list;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory#setClubhouseAndCompId(java.lang.Long, java.lang.Long)
	 */
	@Override
	public void setClubhouseAndCompId(Long compId, Long clubhouseId) {
		list = new ArrayList<IClubhouseLeagueMap>();
		
		if (compId.equals(1L)) {
			if (clubhouseId.equals(70L)) {
				setId(50L);
				list.add(get());
			} else if (clubhouseId.equals(71L)) {
				setId(51L);
				list.add(get());
			} else if (clubhouseId.equals(72L)) {
				setId(52L);
				list.add(get());
			} else if (clubhouseId.equals(74L)) {
				setId(56L);
				list.add(get());
			}	
		} else 		if (compId.equals(2L)) {
			if (clubhouseId.equals(75L)) {
				setId(53L);
				list.add(get());
			} else if (clubhouseId.equals(71L)) {
				setId(54L);
				list.add(get());
			} else if (clubhouseId.equals(72L)) {
				setId(55L);
				list.add(get());
			}	
		}

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory#delete()
	 */
	@Override
	public Boolean delete() {
		return true;
	}
}
