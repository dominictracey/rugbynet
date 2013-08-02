package net.rugby.foundation.core.server.factory;

import java.util.List;

import net.rugby.foundation.model.shared.IClubhouseMembership;

public interface IClubhouseMembershipFactory {

	/**
	 * 
	 * @param id - the id of the item to fetch with get().  Clears clubhouseId and appUserId on factory.
	 */
	void setId(Long id);
	
	/**
	 * 
	 * @param clubhouseId - the id of the clubhouse to get members of. Clears appUserId and id on factory.
	 */
	void setClubhouseId(Long clubhouseId);
	
	/**
	 * 
	 * @param appUserId - the id of the appUser to see what clubhouses they belong to (to which they belong). Clears clubhouseId and id on factory.
	 */
	void setAppUserId(Long appUserId);
	
	IClubhouseMembership get();
	List<IClubhouseMembership> getList();  //throws NotSupportedException;
	
	IClubhouseMembership put(IClubhouseMembership r);
	List<IClubhouseMembership> put(List<IClubhouseMembership> r);
	
	/**
	 * Set up to getList() of a user's membership for a particular clubhouse. The list should contain zero or one members.
	 * @param clubhouseId
	 * @param appUserId
	 */
	public void setClubhouseAndAppUserIds(Long clubhouseId, Long appUserId);
	

	boolean deleteForClubhouse(Long id);
}
