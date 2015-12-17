package net.rugby.foundation.core.client.nav;

import org.gwtbootstrap3.client.ui.Nav;

import net.rugby.foundation.core.client.CoreClientFactory;
import net.rugby.foundation.model.shared.LoginInfo;

import com.google.gwt.user.client.ui.Widget;

public interface INavManager {
	
	public interface IContentPresenter {
		void show(Long contentId);
	}
	
	void setDesktopParent(Nav desktopParent);
	void setMobileParent(Widget mobileParent);
	void registerContentPresenter(IContentPresenter presenter);
	void setClientFactory(CoreClientFactory clientFactory);
	void populateAccount();
	void populateContent(LoginInfo loginInfo);
}
