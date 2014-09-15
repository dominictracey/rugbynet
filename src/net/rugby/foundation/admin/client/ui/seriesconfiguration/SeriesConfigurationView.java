package net.rugby.foundation.admin.client.ui.seriesconfiguration;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;

public interface SeriesConfigurationView<T extends ISeriesConfiguration> extends IsWidget {
	public interface SeriesConfigurationViewPresenter<T> {
		
//		Boolean isSelected(T c);
//		Boolean onItemSelected(T c);
//		Boolean onItemClicked(T c, int row);
//		void deleteSelected();
		
		ClientFactory getClientFactory();
		
		void processSeriesConfig(Long seriesId);
		Boolean peleteSeriesConfig(Long seriesId);
		T editSeriesConfig(Long seriesId);
		T saveSeriesConfig(T config);
//		void showTask(int i, T c);
	} 



	public abstract void showList(List<T> tasks);

	public abstract void setPresenter(SeriesConfigurationViewPresenter<T> p);

	public abstract void showError(T task, int index, String message);

	void setColumnHeaders(ArrayList<String> headers);

	void showWait();

	void setColumnDefinitions(SeriesConfigurationViewColumnDefinitions<T> defs);

	public abstract void updateSeriesConfigurationRow(int index, T task);

	SeriesConfigurationViewPresenter<T> getPresenter();

	public abstract void setClientFactory(ClientFactory clientFactory);

}