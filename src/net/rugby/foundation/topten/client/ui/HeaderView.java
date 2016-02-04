package net.rugby.foundation.topten.client.ui;

import java.util.HashMap;
import net.rugby.foundation.topten.client.ClientFactory;

import org.gwtbootstrap3.client.ui.ListGroupItem;
import org.gwtbootstrap3.client.ui.Nav;

public interface HeaderView {

	public abstract ListGroupItem getLoginPanel();

	public abstract void setClientFactory(ClientFactory clientFactory);

	void setContent(HashMap<String,Long> contentNameMap, boolean isEditor);

	Nav getNav();
}