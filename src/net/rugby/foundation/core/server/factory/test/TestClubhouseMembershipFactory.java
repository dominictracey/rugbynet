/**
 * 
 */
package net.rugby.foundation.core.server.factory.test;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory;
import net.rugby.foundation.model.shared.ClubhouseMembership;
import net.rugby.foundation.model.shared.IClubhouseMembership;

/**
 * @author home
 *
 */
public class TestClubhouseMembershipFactory implements
IClubhouseMembershipFactory {

	private Long clubhouseId;
	private Long appUserId;
	private Long id;

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseFactory#setId(java.lang.Long)
	 */
	@Override
	public void setClubhouseId(Long clubhouseId) {
		this.appUserId = null;
		this.clubhouseId = clubhouseId;
		this.id = null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory#setAppUserId(java.lang.Long)
	 */
	@Override
	public void setAppUserId(Long appUserId) {
		this.clubhouseId = null;
		this.appUserId = appUserId;
		this.id = null;

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory#getCompetition()
	 */
	@Override
	public List<IClubhouseMembership> getList() {

		List<IClubhouseMembership> chml = new ArrayList<IClubhouseMembership>();

		if (clubhouseId != null && appUserId != null) {
			if (clubhouseId.equals(70L)) {
					id = appUserId;
					chml.add(get());
			} else if (clubhouseId.equals(71L)) {
				id = appUserId+100L;
				chml.add(get());
			} else if (clubhouseId.equals(72L)) {
				id = appUserId+200L;
				chml.add(get());
			} else if (clubhouseId.equals(75L)) {   //heineken cup
				id = appUserId+500L;
				chml.add(get());
			}		
		} else if (clubhouseId != null) {
			if (clubhouseId.equals(70L)) {
				for (Long i = 7000L; i<7007L; ++i) {
					id = i;
					chml.add(get());
				}
			} else if (clubhouseId.equals(71L)) {
				for (Long i = 7100L; i<7105L; ++i) {
					id = i;
					chml.add(get());
				}
			} else if (clubhouseId.equals(72L)) {
				for (Long i = 7203L; i<7207L; ++i) {
					id = i;
					chml.add(get());
				}
			} else if (clubhouseId.equals(73L)) {   //heineken cup
				for (Long i = 7300L; i<7301L; ++i) {
					id = i;
					chml.add(get());
				}
			} else if (clubhouseId.equals(74L)) {   //heineken cup
				for (Long i = 7400L; i<7401L; ++i) {
					id = i;
					chml.add(get());
				}
			} else if (clubhouseId.equals(75L)) {   //heineken cup
				for (Long i = 7500L; i<7507L; ++i) {
					id = i;
					chml.add(get());
				}
			}
		} else if (appUserId != null) {
			id = appUserId; // CC
			chml.add(get());

			id = 7599L; // heineken cup
			chml.add(get());

			if (appUserId < 7005L) {
				id = appUserId + 100L;  //PRFC
				chml.add(get());
			} 
			if  (appUserId > 7002L) {
				id = appUserId + 200L;  //BRFC
				chml.add(get());
			} 
		} 

		return chml;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory#put(net.rugby.foundation.model.shared.IClubhouseMembership)
	 */
	@Override
	public IClubhouseMembership put(IClubhouseMembership chm) {
		return chm;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory#put(java.util.List)
	 */
	@Override
	public List<IClubhouseMembership> put(List<IClubhouseMembership> r) {
		return r;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.appUserId = null;
		this.clubhouseId = null;
		this.id = id;		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory#get()
	 */
	@Override
	public IClubhouseMembership get() {
		if (id == null)
			return null;

		IClubhouseMembership chm = new ClubhouseMembership();
		chm.setId(id);

		if (id < 7100) {
			chm.setUserName("Rugby.net Championships");
			chm.setClubhouseID(70L);
			chm.setAppUserID(id);
		} else if (id < 7200) {
			chm.setUserName("Portland Rugby");
			chm.setClubhouseID(71L);
			chm.setAppUserID(id - 100L);
		} else if (id < 7300) {
			chm.setUserName("Boston RFC");
			chm.setClubhouseID(72L);
			chm.setAppUserID(id-200L);
		}  else if (id < 7400) {
			chm.setUserName("Bad - no CLM");
			chm.setClubhouseID(73L);
			chm.setAppUserID(id-300L);
		}   else if (id < 7500) {
			chm.setUserName("Bad - no League");
			chm.setClubhouseID(74L);
			chm.setAppUserID(id-400L);
		} else if (id < 7600L) {
			chm.setUserName("Heineken Cup");
			chm.setClubhouseID(75L);
			chm.setAppUserID(id-500L);
		} else  {
			chm = null;
		}

		return chm;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory#setClubhouseAndAppUserIds(java.lang.Long, java.lang.Long)
	 */
	@Override
	public void setClubhouseAndAppUserIds(Long clubhouseId, Long appUserId) {
		this.clubhouseId = clubhouseId;
		this.appUserId = appUserId;
		this.id = null;

	}

}
