/**
 * 
 */
package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory;
import net.rugby.foundation.model.shared.ClubhouseMembership;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IClubhouseMembership;

/**
 * @author home
 *
 */
public class OfyClubhouseMembershipFactory implements IClubhouseMembershipFactory, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Long clubhouseId;
	private Long appUserId;
	
	public OfyClubhouseMembershipFactory() {

	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;	
		this.clubhouseId = null;
		this.appUserId = null;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseFactory#setId(java.lang.Long)
	 */
	@Override
	public void setClubhouseId(Long clubhouseId) {
		this.id = null;
		this.appUserId = null;
		this.clubhouseId = clubhouseId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory#setAppUserId(java.lang.Long)
	 */
	@Override
	public void setAppUserId(Long appUserId) {
		this.id = null;
		this.clubhouseId = null;
		this.appUserId = appUserId;
		
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory#getCompetition()
	 */
	@Override
	public List<IClubhouseMembership> getList() {
		
		List<IClubhouseMembership> chml = new ArrayList<IClubhouseMembership>();
		Long id = null;
		String filter = "none";
		
		// @REX in a rush
		Boolean doubleFilter = false;
		if (clubhouseId != null && appUserId != null) {
			doubleFilter = true;
			
		}
		
		if (clubhouseId != null) {
			filter = "clubhouseID";
			id = clubhouseId;
		} else if (appUserId != null) {
			filter = "appUserID";
			id = appUserId;
		} 
		
		//have they set an id?
		if (filter.equals("none")) {
			return null;
		}
		
		Query<ClubhouseMembership> qchm = null;
		Objectify ofy = DataStoreFactory.getOfy();
		if (!doubleFilter) {
			qchm = ofy.query(ClubhouseMembership.class).filter(filter, id);
		} else {
			qchm = ofy.query(ClubhouseMembership.class).filter("appUserID", appUserId).filter("clubhouseID", clubhouseId);
			
		}
		
		for (ClubhouseMembership chm : qchm) {
			chml.add(chm);
		}
			
		return chml;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory#put(net.rugby.foundation.model.shared.IClubhouseMembership)
	 */
	@Override
	public IClubhouseMembership put(IClubhouseMembership chm) {
		Objectify ofy = DataStoreFactory.getOfy();
		ofy.put(chm);
		return chm;
	}



	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory#put(java.util.List)
	 */
	@Override
	public List<IClubhouseMembership> put(List<IClubhouseMembership> r) {
		Objectify ofy = DataStoreFactory.getOfy();
		ofy.put(r);
		return r;
	}



	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory#get()
	 */
	@Override
	public IClubhouseMembership get() {
		if (id == null) {
			return new ClubhouseMembership();
		}
		Objectify ofy = DataStoreFactory.getOfy();
		IClubhouseMembership chm = ofy.find(new Key<ClubhouseMembership>(ClubhouseMembership.class,id));
		return chm;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory#setClubhouseAndAppUserIds(java.lang.Long, java.lang.Long)
	 */
	@Override
	public void setClubhouseAndAppUserIds(Long clubhouseId, Long appUserId) {
		this.id = null;
		this.clubhouseId = clubhouseId;
		this.appUserId = appUserId;
		
	}

	@Override
	public boolean deleteForClubhouse(Long id) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			setClubhouseId(id);
			List<IClubhouseMembership> c = getList();

			ofy.delete(c);
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem in delete: " + ex.getLocalizedMessage());
			return false;
		}
		return true;
		
	}


}
