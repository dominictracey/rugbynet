package net.rugby.foundation.admin.client.ui.task;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import net.rugby.foundation.admin.client.ui.ColumnDefinition;
import net.rugby.foundation.admin.client.ui.task.TaskView.TaskViewPresenter;
import net.rugby.foundation.admin.shared.IAdminTask;

public class TaskViewColumnDefinitions<T extends IAdminTask> {

	private List<ColumnDefinition<T>> columnDefinitions =
			new ArrayList<ColumnDefinition<T>>();

	private TaskViewPresenter<T> listener;

	public TaskViewColumnDefinitions() {
		if (columnDefinitions.isEmpty()) {

			//    		private Long id;
			//    		private Action action;
			//    		private Long adminId;
			//    		private Date created;
			//    		private Date completed;
			//    		private Status status;
			//    		private Priority priority;
			//    		private String summary;
			//    		private String details;
			//    		private List<String> log;
			//    		
			//    		private String promise;
			//    		private String pipelineRoot;
			//    		private String pipelineJob;

			columnDefinitions.add(new ColumnDefinition<T>() {
				public Widget render(T c) {
					CheckBox checkBox = new CheckBox();
					if (listener != null)
						checkBox.setValue(listener.isSelected(c));
					return checkBox;
				}

				public boolean isSelectable() {
					return true;
				}

				@Override
				public Column<T, ?> getColumn() {
					// TODO Auto-generated method stub
					return null;
				}
			});

			columnDefinitions.add(new ColumnDefinition<T>() {
				//id
				public Widget render(final T c) {
					String pos = c.getSummary();
					return new HTML(pos);
				}     

				public boolean isClickable() {
					return true;
				}

				@Override
				public Column<T, ?> getColumn() {
					// TODO Auto-generated method stub
					return null;
				}
			});

			columnDefinitions.add(new ColumnDefinition<T>() {
				// summary
				public Widget render(T c) {
					String pos = c.getDetails();
					return new HTML(pos);
				}

				@Override
				public Column<T, ?> getColumn() {
					// TODO Auto-generated method stub
					return null;
				}
			});

			columnDefinitions.add(new ColumnDefinition<T>() {
				// status
				public Widget render(T c) {
					String pos = c.getStatus().toString();
					return new HTML(pos);
				}

				@Override
				public Column<T, ?> getColumn() {
					// TODO Auto-generated method stub
					return null;
				}
			});

			columnDefinitions.add(new ColumnDefinition<T>() {
				// created
				public Widget render(T c) {
					String pos = c.getCreated().toString();
					return new HTML(pos);
				}

				@Override
				public Column<T, ?> getColumn() {
					// TODO Auto-generated method stub
					return null;
				}
			});

			columnDefinitions.add(new ColumnDefinition<T>() {
				// pipeline link
				public Widget render(final T c) {
					Anchor a =  new Anchor("Pipeline", c.getPipelineUrl(), "_blank");
					return a;
				}

				public boolean isClickable() {
					return false;  // it's an a href
				}

				@Override
				public Column<T, ?> getColumn() {
					// TODO Auto-generated method stub
					return null;
				}
			});

			columnDefinitions.add(new ColumnDefinition<T>() {
				// log
				public Widget render(final T c) {

					Button a =  new Button("Log");
					if (c.getLog() != null) {
						a.addClickHandler(new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								if (c.getLog() != null) {
									Window.alert(c.getLog().toString());
								}
							}

						});
					} else {
						a.setEnabled(false);
					}
					return a;
				}

				public boolean isClickable() {
					return false;  // it's an a href
				}

				@Override
				public Column<T, ?> getColumn() {
					// TODO Auto-generated method stub
					return null;
				}
			});
		} 	
	}

	public List<ColumnDefinition<T>> getColumnDefinitions() {
		return columnDefinitions;
	}

	public ArrayList<String> getHeaders() {
		ArrayList<String> headers = new ArrayList<String>();
		headers.add("<img src='resources/cb.jpg'>");
		headers.add( "Task");
		headers.add( "Summary");
		headers.add( "Status");
		headers.add( "Created");
		headers.add( "Pipeline");
		headers.add("Details");


		return headers;
	}

	public void setListener(TaskViewPresenter<T> listener2) {
		this.listener =  listener2;
	}
}
