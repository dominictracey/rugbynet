package net.rugby.foundation.admin.client.ui.seriesconfiguration;

import java.util.ArrayList;
import java.util.List;

import org.gwtbootstrap3.client.ui.Button;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import net.rugby.foundation.admin.client.ui.ColumnDefinition;
import net.rugby.foundation.admin.client.ui.seriesconfiguration.SeriesConfigPopupView.Presenter;
import net.rugby.foundation.admin.client.ui.seriesconfiguration.SeriesConfigurationView.SeriesConfigurationViewPresenter;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.model.shared.ICompetition;

public class SeriesConfigurationViewColumnDefinitions<T extends ISeriesConfiguration> {

	private List<ColumnDefinition<T>> columnDefinitions =
			new ArrayList<ColumnDefinition<T>>();

	private SeriesConfigurationViewPresenter<T> listener;

	public SeriesConfigurationViewColumnDefinitions() {
		if (columnDefinitions.isEmpty()) {

			//			protected Long id;
			//			protected Long compId;
			//			protected ConfigurationType type;
			//			protected List<Long> targets;
			//			protected Long lastRoundId;
			//			@Transient
			//			protected IRound lastRound;
			//			protected String status;



			columnDefinitions.add(new ColumnDefinition<T>() {
				//id
				public Widget render(final T c) {
					String comp = "";
					if (c != null) {
						for (ICompetition cp : c.getComps()) {
							comp += cp.getAbbr() + ", ";
						}
					}
					return new HTML(comp);
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
					String pos = c.getMode().toString();
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
					if (c.getLastRound() != null) {
						String pos = c.getLastRound().shortDesc;
						return new HTML(pos);
					} else {
						return new HTML("none");
					}
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
					if (c.getTargetRound() != null) {
						String pos = c.getTargetRound().shortDesc;
						return new HTML(pos);
					} else {
						return new HTML("none");
					}
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
					if (c != null && c.getStatus() != null) {
						String pos = c.getStatus().toString();
						return new HTML(pos);
					} else {
						return new HTML("--");
					}
				}

				@Override
				public Column<T, ?> getColumn() {
					// TODO Auto-generated method stub
					return null;
				}
			});

			columnDefinitions.add(new ColumnDefinition<T>() {
				// LastRun
				public Widget render(T c) {
					if (c.getLastRun() != null) {
						String pos = c.getLastRun().toString();
						return new HTML(pos);
					} else {
						return new HTML("--");
					}
				}

				@Override
				public Column<T, ?> getColumn() {
					// TODO Auto-generated method stub
					return null;
				}
			});

			columnDefinitions.add(new ColumnDefinition<T>() {
				// NextRun
				public Widget render(T c) {
					if (c != null && c.getNextRun() != null) {
						String pos = c.getNextRun().toString();
						return new HTML(pos);
					} else 
						return new HTML("--");
				}

				@Override
				public Column<T, ?> getColumn() {
					// TODO Auto-generated method stub
					return null;
				}
			});
			//
			//			columnDefinitions.add(new ColumnDefinition<T>() {
			//				// pipeline link
			//				public Widget render(final T c) {
			//					Anchor a =  new Anchor("Pipeline", c.getPipelineUrl(), "_blank");
			//					return a;
			//				}
			//
			//				public boolean isClickable() {
			//					return false;  // it's an a href
			//				}
			//
			//				@Override
			//				public Column<T, ?> getColumn() {
			//					// TODO Auto-generated method stub
			//					return null;
			//				}
			//			});

			columnDefinitions.add(new ColumnDefinition<T>() {
				// Process
				public Widget render(final T c) {

					Button a =  new Button("Process");
					a.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							listener.getClientFactory().getRpcService().processSeriesConfiguration(c.getId(), new AsyncCallback<String>() {

								@Override
								public void onFailure(Throwable caught) {
									Window.alert(caught.getLocalizedMessage());
								}

								@Override
								public void onSuccess(String result) {
									// now have the Activity poll to check the status
									listener.processSeriesConfig(c.getId());
								}

							});
						}

					});

					return a;
				}

				public boolean isClickable() {
					return false;  // it's a button
				}

				@Override
				public Column<T, ?> getColumn() {
					// TODO Auto-generated method stub
					return null;
				}
			});


			columnDefinitions.add(new ColumnDefinition<T>() {
				// Delete
				public Widget render(final T c) {

					Button a =  new Button("Delete");
					//if (c.getLog() != null) {
					a.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							if (Window.confirm("Are you sure you want to delete this series and all of its Top Ten Lists?")) {
								listener.getClientFactory().getRpcService().deleteSeriesConfiguration(c.getId(), new AsyncCallback<Boolean>() {

									@Override
									public void onFailure(Throwable caught) {
										Window.alert(caught.getLocalizedMessage());
									}

									@Override
									public void onSuccess(Boolean result) {
										if (result) {
											Window.alert("Deleted");

											// remove the row...
											listener.deleteSeriesConfig(c.getId());
										} else {
											Window.alert("No bueno, senor");
										}
									}

								});
							}
						}
					});

					//} else {
					//	a.setEnabled(false);
					//}
					return a;
				}

				public boolean isClickable() {
					return false;  // it's a button
				}

				@Override
				public Column<T, ?> getColumn() {
					// TODO Auto-generated method stub
					return null;
				}
			});

			columnDefinitions.add(new ColumnDefinition<T>() {
				// Edit
				public Widget render(final T c) {

					Button a =  new Button("Edit");
					//if (c.getLog() != null) {
					a.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							listener.getClientFactory().getRpcService().getSeriesConfiguration(c.getId(), new AsyncCallback<ISeriesConfiguration>() {

								@Override
								public void onFailure(Throwable caught) {
									Window.alert(caught.getLocalizedMessage());
								}

								@Override
								public void onSuccess(ISeriesConfiguration result) {
									listener.getClientFactory().getSeriesConfigrPopupView().setPresenter((Presenter<ISeriesConfiguration>) listener);
									listener.getClientFactory().getSeriesConfigrPopupView().setConfig(result);
									((DialogBox)listener.getClientFactory().getSeriesConfigrPopupView()).center();
								}

							});
						}

					});
					//} else {
					//	a.setEnabled(false);
					//}
					return a;
				}

				public boolean isClickable() {
					return false;  // it's a button
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
		headers.add("Comp");
		headers.add( "Type");
		headers.add( "Last Round");
		headers.add("Target Round");
		headers.add( "Status");
		headers.add( "Last Run");
		headers.add( "Next Run");
		headers.add(" ");
		headers.add(" ");
		headers.add(" ");
		return headers;
	}

	public void setListener(SeriesConfigurationViewPresenter<T> listener2) {
		this.listener =  listener2;
	}
}
