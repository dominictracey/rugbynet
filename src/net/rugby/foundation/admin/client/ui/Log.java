/**
 * 
 */
package net.rugby.foundation.admin.client.ui;

import net.rugby.foundation.model.shared.IPlayer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author home
 *
 */
public class Log extends Composite {



	interface LogUiBinder extends UiBinder<Widget, Log> {
	}
	
	private static LogUiBinder uiBinder = GWT
			.create(LogUiBinder.class);

	public interface Presenter {
		void savePlayerInfo(IPlayer player);

		IPlayer getNewPlayer();
	} 
	
	public Log() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	VerticalPanel logPanel;



}
