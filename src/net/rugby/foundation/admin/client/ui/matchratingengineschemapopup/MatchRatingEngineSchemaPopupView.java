package net.rugby.foundation.admin.client.ui.matchratingengineschemapopup;

import java.util.List;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.ui.FieldDefinition;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema20130713;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget. 
 */
public interface MatchRatingEngineSchemaPopupView<T> extends IsWidget
{
	void setTarget(T target);
	void setPresenter(MatchRatingEngineSchemaPopupViewPresenter<T> listener);
	void setFieldDefinitions(List<FieldDefinition<T>> fieldDefinitions);
	void clear();
	
	public interface MatchRatingEngineSchemaPopupViewPresenter<T>
	{
		ClientFactory getClientFactory();
		void onSaveMatchRatingEngineSchemaClicked(T schema);
		void onSaveAsCopyMatchRatingEngineSchemaClicked(T schema);
		void onCancelEditMatchRatingEngineSchemaClicked();
		void onDeleteRatingsForMatchRatingEngineSchemaClicked(T schema);
		void onDeleteMatchRatingEngineSchemaClicked(T schema);
		void onSetMatchRatingEngineSchemaAsDefaultClicked(T schema);
		void onFlushRawScoresButtonClicked(T schema);
	}
}