/**
 * 
 */
package net.rugby.foundation.admin.client.ui.task;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.ui.ColumnDefinition;
import net.rugby.foundation.admin.client.ui.SmartBar;
import net.rugby.foundation.admin.shared.IAdminTask;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author home
 *
 */
public class TaskViewImpl<T extends IAdminTask> extends Composite implements TaskView<T> {



	interface TaskViewImplUiBinder extends UiBinder<Widget, TaskViewImpl<?>> {
	}
	
	private static TaskViewImplUiBinder uiBinder = GWT
			.create(TaskViewImplUiBinder.class);

	@UiField FlexTable taskTable;
	@UiField SimplePanel menuBarPanel;


	private TaskViewColumnDefinitions<T> columnDefinitions;
	private List<T> taskList;
	private TaskViewPresenter<T> listener;

	private ArrayList<String> headers;

	private ClientFactory clientFactory;

	private SmartBar smartBar;
	
	public TaskViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		taskTable.getRowFormatter().addStyleName(0, "groupListHeader");
		taskTable.addStyleName("groupList");
		taskTable.getCellFormatter().addStyleName(0, 1, "groupListNumericColumn");

	}

	@Override
	public void setColumnDefinitions(TaskViewColumnDefinitions<T> defs) {
		this.columnDefinitions = defs;
		defs.setListener(listener);
		setColumnHeaders(columnDefinitions.getHeaders());
	}

	@UiHandler("taskTable")
	void onTableClicked(ClickEvent event) {
		if (listener != null) {
			HTMLTable.Cell cell = taskTable.getCellForEvent(event);

			if (cell != null) {
				if (cell.getRowIndex() == 0) { // we have a check box up and click the select all
					if (cell.getCellIndex() == 0) {
						if (columnDefinitions.getColumnDefinitions().get(0).isSelectable()) {
							// select them all
							for (int i = 1; i < taskList.size()+1; ++i) {
								((CheckBox)taskTable.getWidget(i, 0).asWidget()).setValue(true);
								listener.onItemSelected(taskList.get(i-1));
							}	   			  
						}  
					}
				} else { 
					T task = taskList.get(cell.getRowIndex()-1);
					if (shouldFireClickEvent(cell)) {
						listener.onItemClicked(task, cell.getRowIndex()-1);
					}

//					if (shouldFireSelectEvent(cell)) { // only do it if we have a checkbox up
//						listener.onItemSelected(task);
//						if (listener != null) {
//							//important sanity check because we are clicking on the table and not the checkbox. And we can miss.
//							int x = cell.getRowIndex();
//							((CheckBox)taskTable.getWidget(x,0).asWidget()).setValue(listener.onItemSelected(player));
//						}
//					}
				}
			}
		}
	}


	@Override
	public void showList(List<T> taskList) {
		if (taskList != null) {
			taskTable.removeAllRows();
			this.taskList = taskList;
			setHeaders();
			String style = "leaderboardRow-odd";

			//	      Date begin = new Date();
			for (int i = 1; i < taskList.size()+1; ++i) {
				T t = taskList.get(i-1);
				for (int j = 0; j < columnDefinitions.getColumnDefinitions().size(); ++j) {
					ColumnDefinition<T> columnDefinition = columnDefinitions.getColumnDefinitions().get(j);

					//Logger.getLogger("debug").log(Level.SEVERE,"("+i+","+j+"): "+ System.currentTimeMillis());


					taskTable.setWidget(i, j, columnDefinition.render(t));
					taskTable.getRowFormatter().setStyleName(i, style);

				}
				if (style == "leaderboardRow-odd")
					style = "leaderboardRow-even";
				else
					style = "leaderboardRow-odd";

			}

			//	      Date end = new Date();

			//	      Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,begin.toLocaleString() + " end: " + end.toLocaleString());
		}
	}

	private void setHeaders() {
		int i = 0;
		for (String s : headers) {
			taskTable.setHTML(0, i++, s);	
		}

		taskTable.getRowFormatter().addStyleName(0, "leaderboardRow-header");


	}

	private boolean shouldFireClickEvent(HTMLTable.Cell cell) {
		boolean shouldFireClickEvent = false;

		if (cell != null) {
			ColumnDefinition<T> columnDefinition =
					columnDefinitions.getColumnDefinitions().get(cell.getCellIndex());

			if (columnDefinition != null) {
				shouldFireClickEvent = columnDefinition.isClickable();
			}
		}

		return shouldFireClickEvent;
	}


	@Override
	public void setColumnHeaders(ArrayList<String> headers) {
		this.headers = headers;
	}



	@Override
	public void showWait() {
		taskTable.removeAllRows();
		taskTable.setWidget(0,0,new HTML("Stand by...")); //new Image("/resources/images/ajax-loader.gif"));	
	}
	
	@Override
	public void setPresenter(TaskViewPresenter<T> p) {
		listener = p;
		if (listener instanceof SmartBar.Presenter) {
			if (!menuBarPanel.getElement().hasChildNodes()) {
				smartBar = clientFactory.getMenuBar();
				menuBarPanel.add(smartBar);
			}
			smartBar.setPresenter((SmartBar.Presenter)listener);		
		}
	}

	@Override
	public TaskViewPresenter<T> getPresenter() {
		return listener;
	}
	
	@Override
	public void showError(T task, int index, String message) {
		Window.alert(message);
		// do something cool with a row formatter!
	}

	@Override
	public void updateTaskRow(int i, T task) {
		for (int j = 0; j < columnDefinitions.getColumnDefinitions().size(); ++j) {
			ColumnDefinition<T> columnDefinition = columnDefinitions.getColumnDefinitions().get(j);
			taskTable.setWidget(i+1, j, columnDefinition.render(task));
		}		
	}

	@Override
	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

}

