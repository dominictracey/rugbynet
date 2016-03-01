/**
 * 
 */
package net.rugby.foundation.core.server.factory;

import java.util.List;

import net.rugby.foundation.model.shared.IAppUser;

/**
 * @author Dominic Tracey - This factory will return the AppUser searching either by email or database id. Calling
 * 	setEmail should clear the factory's id value and vice versa.
 *
 */
public interface IAppUserFactory {
	/**
	 * @param email
	 */
	void setEmail(String email);
	void setNickName(String nickName);
	void setId(Long id);
	IAppUser get();
	IAppUser put(IAppUser appUser);
	List<IAppUser> getDigestEmailRecips();
}
