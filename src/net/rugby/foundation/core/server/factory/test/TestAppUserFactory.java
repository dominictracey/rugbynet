/**
 * 
 */
package net.rugby.foundation.core.server.factory.test;

import java.util.List;

import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.model.shared.AppUser;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.ITopTenUser;

/**
 * @author home
 *
 */
public class TestAppUserFactory implements IAppUserFactory {

	private Long id;
	private String email;
	private String nickname;
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.IAppUserFactory#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.email = null;
		this.id = id;
		this.nickname = null;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IAppUserFactory#setEmail(java.lang.String)
	 */
	@Override
	public void setEmail(String email) {
		this.id = null;
		this.email = email;		
		this.nickname = null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IAppUserFactory#get()
	 */
	@Override
	public IAppUser get() {
		if (id != null)
			return getById();
		else if (email != null)
			return getByEmail();
		else if (nickname != null)
			return getByNickname();
		else  // give an empty one
			return new AppUser();
	}

	/**
	 * @return
	 */
	private IAppUser getByNickname() {
		
		if (nickname.equals("A10")) {
			setId(7001L);		
		} else if (nickname.equals("A11")) {
			setId(7001L);
		} else if (nickname.equals("A12")) {
			setId(7002L);
		} else if (nickname.equals("A13")) {
			setId(7003L);
		} else if (nickname.equals("A14")) {
			setId(7004L);
		} else if (nickname.equals("A15")) {
			setId(7005L);
		} else if (nickname.equals("A16")) {
			setId(7006L);
		} else if (nickname.equals("Top Ten Content Editor")) {
			setId(8000L);
		} else if (nickname.equals("Top Ten Content Contributor")) {
			setId(8001L);
		}
		
		return getById();
		
	}

	private IAppUser getById() {
		IAppUser au = new AppUser();
		au.setActive(true);
		au.setAdmin(false);
		au.setId(id);
		au.setSuperadmin(false);
		au.setLastCompetitionId(1L);
		au.setLastClubhouseId(72L);
		au.setPwHash("a8f5f167f44f4964e6c998dee827110c");
		
		if (id == 7000) {
			au.setEmailAddress("a10@test.com");
			au.setNickname("A10");
			au.setAdmin(true);
			au.setLastClubhouseId(71L);
		} else if (id == 7001) {
			au.setEmailAddress("a11@test.com");
			au.setNickname("A11");
			au.setLastClubhouseId(71L);
		} else if (id == 7002) {
			au.setEmailAddress("a12@test.com");
			au.setNickname("A12");
			au.setLastClubhouseId(71L);
		} else if (id == 7003) {
			au.setEmailAddress("a13@test.com");
			au.setNickname("A13");
			au.setLastClubhouseId(71L);
		} else if (id == 7004) {
			au.setEmailAddress("a14@test.com");
			au.setLastClubhouseId(72L);
			au.setNickname("A14");
		} else if (id == 7005) {
			au.setEmailAddress("a15@test.com");
			au.setLastClubhouseId(72L);
			au.setNickname("A15");
		} else if (id == 7006) {
			au.setEmailAddress("a16@test.com");
			au.setLastClubhouseId(72L);
			au.setNickname("A16");
		} else if (id == 8000) {
			au.setEmailAddress("editor@test.com");
			//au.setLastClubhouseId(72L);
			au.setNickname("Top Ten Content Editor");
			if (au instanceof ITopTenUser) {
				((ITopTenUser)au).setTopTenContentContributor(true);
				((ITopTenUser)au).setTopTenContentEditor(true);
			}
		} else if (id == 8001) {
			au.setEmailAddress("contentcontributor@test.com");
			//au.setLastClubhouseId(72L);
			au.setNickname("Top Ten Content Contributor");
			if (au instanceof ITopTenUser) {
				((ITopTenUser)au).setTopTenContentContributor(true);
				((ITopTenUser)au).setTopTenContentEditor(false);
			}
		}
		
		au.setLastEntryId(id - 1000L);
		
		return au;
	}


	private IAppUser getByEmail() {

		if (email.equals("a10@test.com")) {
			setId(7000L);
		} else if (email.equals("a11@test.com")) {
			setId(7001L);
		} else if (email.equals("a12@test.com")) {
			setId(7002L);
		} else if (email.equals("a13@test.com")) {
			setId(7003L);
		} else if (email.equals("a14@test.com")) {
			setId(7004L);
		} else if (email.equals("a15@test.com")) {
			setId(7005L);
		} else if (email.equals("a16@test.com")) {
			setId(7006L);
		} else if (email.equals("editor@test.com")) {
			setId(8000L);
		} else if (email.toLowerCase().equals("contentcontributor@test.com")) {
			setId(8001L);
		}

		return getById();
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.IAppUserFactory#put(net.rugby.foundation.model.shared.IAppUser)
	 */
	@Override
	public IAppUser put(IAppUser appUser) {
		appUser.setId(99999999999L);
		return appUser;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IAppUserFactory#setNickName(java.lang.String)
	 */
	@Override
	public void setNickName(String nickName) {
		this.id = null;
		this.email = null;		
		this.nickname = nickName;
		
	}

	@Override
	public List<IAppUser> getDigestEmailRecips() {
		// TODO Auto-generated method stub
		return null;
	}



}
