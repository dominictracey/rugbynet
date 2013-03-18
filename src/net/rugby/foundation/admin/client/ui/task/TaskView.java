package net.rugby.foundation.admin.client.ui.task;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.shared.IAdminTask;

public interface TaskView<T extends IAdminTask> extends IsWidget {
	public interface TaskViewPresenter<T> {
		
		Boolean isSelected(T c);
		Boolean onItemSelected(T c);
		Boolean onItemClicked(T c, int row);
		void deleteSelected();
		
		ClientFactory getClientFactory();
		
		void showTask(int i, T c);
	} 



	public abstract void showList(List<T> tasks);

	public abstract void setPresenter(TaskViewPresenter<T> p);

	public abstract void showError(T task, int index, String message);

	void setColumnHeaders(ArrayList<String> headers);

	void showWait();

	void setColumnDefinitions(TaskViewColumnDefinitions<T> defs);

	public abstract void updateTaskRow(int index, T task);

	TaskViewPresenter<T> getPresenter();

	public abstract void setClientFactory(ClientFactory clientFactory);

}