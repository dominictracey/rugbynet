package net.rugby.foundation.topten.client.ui;

import java.util.List;
import java.util.Map;

import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.topten.client.ClientFactory;

import org.gwtbootstrap3.client.ui.ListGroupItem;

public interface HeaderView {

	public abstract ListGroupItem getLoginPanel();

	public abstract void setClientFactory(ClientFactory clientFactory);

	void setContent(List<IContent> list, boolean isEditor);

}