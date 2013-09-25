package net.rugby.foundation.admin.client.ui.portal;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.Position.position;

public interface PortalView<T extends IPlayerMatchInfo> extends IsWidget {
	public interface PortalViewPresenter<T> {
		
		ClientFactory getClientFactory();

		void submitPortalQuery(Long compId, Long roundId, position posi,
				Long countryId, Long teamId);
		void createTopTenList(TopTenSeedData data);

		void portalViewCompSelected(long parseLong);

	} 


	public abstract void setPresenter(PortalViewPresenter<T> p);

	public abstract void showWait();

	public abstract PortalViewPresenter<T> getPresenter();

	public abstract void setClientFactory(ClientFactory clientFactory);

	public abstract void setComps(ICoreConfiguration conf);

	public abstract void setPositions(List<position> result);

	public abstract void setCountries(List<ICountry> result);

	public abstract void showAggregatedMatchInfo(List<IPlayerMatchInfo> matchInfo);

	ICompetition getCurrentComp();

	IRound getCurrentRound();

	public abstract void setComp(ICompetition result);
	
	

}