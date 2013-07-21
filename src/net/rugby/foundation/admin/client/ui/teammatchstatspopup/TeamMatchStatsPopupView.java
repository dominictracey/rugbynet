package net.rugby.foundation.admin.client.ui.teammatchstatspopup;

import java.util.List;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.ui.FieldDefinition;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget. 
 */
public interface TeamMatchStatsPopupView<T> extends IsWidget
{
	void setTarget(T target);
	void setPresenter(TeamMatchStatsPopupViewPresenter<T> listener);
	void setFieldDefinitions(List<FieldDefinition<T>> fieldDefinitions);
	void clear();
	
	public interface TeamMatchStatsPopupViewPresenter<T>
	{
		ClientFactory getClientFactory();
		void onSaveTeamMatchStatsClicked(T Team);
		void onCancelEditTeamMatchStatsClicked();
		void onRefetchEditTeamMatchStatsClicked(T target);
	}
}