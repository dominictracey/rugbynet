package net.rugby.foundation.admin.client.ui;

import java.util.List;
import java.util.Map;

import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View base interface.
 * Extends IsWidget so a view impl can easily provide its container widget.
 */
public interface SmartBar extends IsWidget {
  
	void setName(String helloName);

	void setPresenter(Presenter listener);
	void setSchemaPresenter(SchemaPresenter presenter);

	public interface Presenter {
		/**
		 * Navigate to a new Place in the browser.
		 */
		void goTo(Place place);

		/**
		 * @param id - compId picked
		 */
		void compPicked(Long id);

		void flushAllPipelineJobs();
		

	}
	
	public interface SchemaPresenter {
		/**
		 * 
		 */
		void editSchema(IMatchRatingEngineSchema schema);
	
		void createSchema();
	}

	/**
	 * @param compMap - hash table key: compId, value: ICompetition
	 */
	void setComps(Map<Long, String> compMap);
	
	/**
	 * 
	 */
	void setSchemas(List<ScrumMatchRatingEngineSchema> result);

}
