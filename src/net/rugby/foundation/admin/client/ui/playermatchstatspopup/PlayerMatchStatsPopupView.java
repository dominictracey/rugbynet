package net.rugby.foundation.admin.client.ui.playermatchstatspopup;

import java.util.List;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.ui.FieldDefinition;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget. 
 */
public interface PlayerMatchStatsPopupView<T> extends IsWidget
{
	void setTarget(T target);
	void setPresenter(PlayerMatchStatsPopupViewPresenter<T> listener);
	void setFieldDefinitions(List<FieldDefinition<T>> fieldDefinitions);
	void clear();
	
	public interface PlayerMatchStatsPopupViewPresenter<T>
	{
		ClientFactory getClientFactory();
		void onSavePlayerMatchStatsClicked(T player);
		void onCancelEditPlayerMatchStatsClicked();

	}
}