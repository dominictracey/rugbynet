package net.rugby.foundation.admin.client.ui.seriesconfiguration;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;

public interface SeriesConfigurationView<T extends ISeriesConfiguration> extends IsWidget {
	public interface SeriesConfigurationViewPresenter<T> {
		ClientFactory getClientFactory();
		
		void processSeriesConfig(Long seriesId);
		Boolean deleteSeriesConfig(Long seriesId);
		void showConfigPopup(T config);
		void editSeriesConfig(T seriesConf);
		void rollbackSeriesConfig(T seriesConf);
	} 



	public abstract void showList(List<T> tasks);

	public abstract void setPresenter(SeriesConfigurationViewPresenter<T> p);

	public abstract void showError(T task, int index, String message);

	void setColumnHeaders(ArrayList<String> headers);

	void showWait();

	void setColumnDefinitions(SeriesConfigurationViewColumnDefinitions<T> defs);

	public abstract void updateSeriesConfigurationRow(T sc);

	SeriesConfigurationViewPresenter<T> getPresenter();

	public abstract void setClientFactory(ClientFactory clientFactory);


}