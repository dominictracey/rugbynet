package net.rugby.foundation.topten.client.ui;

import java.util.List;
import java.util.Map;

import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.topten.client.ClientFactory;

import com.github.gwtbootstrap.client.ui.NavPills;
import com.github.gwtbootstrap.client.ui.NavWidget;
import com.google.gwt.user.client.ui.HorizontalPanel;

public interface HeaderView {

	public abstract void setComps(Map<Long, String> competitionMap,
			List<Long> compsUnderway);

	public abstract void addLoginPanel(HorizontalPanel acct);

	public abstract NavPills getLoginPanel();

	public abstract void setClientFactory(ClientFactory clientFactory);

	public abstract void setHeroListInfo(String title1, String details11);

//	public abstract void setDetails(String details11);

	public abstract void setFBLikeAttribute(String name, String value);

	public abstract NavWidget getButtonBar();

	public abstract void collapseHero(boolean b);

	void setContent(List<IContent> list, boolean isEditor);

	void setDetails(String details11);
	
	void setHeroTextBig(Boolean big);

}