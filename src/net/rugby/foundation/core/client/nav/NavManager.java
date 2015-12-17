package net.rugby.foundation.core.client.nav;

import java.util.HashMap;

import org.gwtbootstrap3.client.ui.Nav;

import net.rugby.foundation.core.client.CoreClientFactory;
import net.rugby.foundation.model.shared.LoginInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class NavManager implements INavManager {

	private Nav desktopParent = null;
	private Widget mobileParent = null;
	private IContentPresenter contentPresenter = null;
	private CoreClientFactory clientFactory = null;
	
	public NavManager() {
		
	}
	
	@Override
	public void setClientFactory(CoreClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public void setDesktopParent(Nav desktopParent) {
		this.desktopParent = desktopParent;
	}

	@Override
	public void setMobileParent(Widget mobileParent) {
		this.mobileParent = mobileParent;

	}

	@Override
	public void populateAccount() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void populateContent(final LoginInfo loginInfo) {
		final IContentPresenter _contentPresenter = contentPresenter;
		final Nav _desktopParent = desktopParent;
		
		clientFactory.getRpcService().getContentItems( new AsyncCallback<HashMap<String,Long>>() {
			@Override
			public void onFailure(Throwable caught) {
				//cb.onFailure(caught);
			}

			@Override
			public void onSuccess(HashMap<String,Long> contentNameMap) {
				
				//console("getContentItems.onSuccess");
				clientFactory.setContentNameMap(contentNameMap);
				
				if (contentPresenter == null || desktopParent == null) {
					clientFactory.console("No contentPresenter or parent. Content setup failed");
					return;
				}

				DesktopContentBuilder dcb = new DesktopContentBuilder(clientFactory, loginInfo);
				if (_contentPresenter != null) {
					dcb.setContentPresenter(_contentPresenter);
				}
				
				if (_desktopParent != null) {
					dcb.setParent(_desktopParent);
				}
				

				dcb.build(contentNameMap);		
			}
		});
	}

	@Override
	public void registerContentPresenter(IContentPresenter contentPresenter) {
		this.contentPresenter = contentPresenter;
	}

}
