package net.rugby.foundation.admin.client.ui.portal;

import java.util.List;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema;

import com.google.gwt.user.client.ui.IsWidget;

public interface PortalView<T extends IPlayerRating> extends IsWidget {
	public interface PortalViewPresenter<T> {
		
		ClientFactory getClientFactory();

		void submitPortalQuery(List<Long> compId, List<Long> roundId, List<position> posi,
				List<Long> countryId, List<Long> teamId, Long schemaId, 
				Boolean scaleTime, Boolean scaleComp, Boolean scaleStanding, Boolean scaleMinutesPlayed, Boolean instrument);
		
		void createTopTenList(TopTenSeedData data);

		void portalViewCompSelected(long parseLong);

		void deleteQuery(IRatingQuery query);

		void portalViewCompPopulate();
		
		//void setTimeSeries(boolean isTrue);

		void rerunQuery(IRatingQuery rq);

	} 


	public abstract void setPresenter(PortalViewPresenter<T> p);

	public abstract void showWait();
	public abstract void hideWait();

	public abstract PortalViewPresenter<T> getPresenter();

	public abstract void setClientFactory(ClientFactory clientFactory);

	public abstract void setComps(ICoreConfiguration conf);

	public abstract void setPositions(List<position> result);

	public abstract void setCountries(List<ICountry> result);

	public abstract void showAggregatedMatchInfo(List<IPlayerRating> matchInfo);

	ICompetition getCurrentComp();

	IRound getCurrentRound();

	public abstract void setComp(ICompetition result, boolean populate);
	
	public abstract void setRatingQuery(IRatingQuery rq);

	public abstract boolean isSetup();

	boolean clear();

	public abstract void showTimeWeightedMatchInfo(List<IPlayerRating> result);

	public abstract List<IPlayerRating> getCurrentList();

	void setSchemas(List<ScrumMatchRatingEngineSchema> result);

	

}