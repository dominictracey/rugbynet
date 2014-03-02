package net.rugby.foundation.admin.client.ui.playerlistview;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.rugby.foundation.admin.client.ui.ColumnDefinition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.PlayerRating.RatingComponent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.ListDataProvider;


public class RatingListViewImpl<T extends IPlayerRating> extends Composite implements PlayerListView<T>
{
	private static PlayerListViewImplUiBinder uiBinder = GWT.create(PlayerListViewImplUiBinder.class);
	private List<ColumnDefinition<T>> columnDefinitions;
	public void setColumnDefinitions(List<ColumnDefinition<T>> columnDefinitions) {
		this.columnDefinitions = columnDefinitions;
	}

	@UiTemplate("PlayerListViewImpl.ui.xml")

	interface PlayerListViewImplUiBinder extends UiBinder<Widget, RatingListViewImpl<?>>
	{
	}

	@UiField CellTable<T> playersTable;

	private boolean columnsInitialized = false;


	//	private Presenter<T> presenter;
	private RatingListener<T> listener;
	private ArrayList<String> headers;
	private ListDataProvider<T> dataProvider;

	public RatingListViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		playersTable.addStyleName("groupList");
	}


	@Override
	public void setPlayers(List<T> PlayerList, IMatchGroup match) {
		if (PlayerList != null) {
			if (!columnsInitialized) {

				for (int j = 0; j < columnDefinitions.size(); ++j) {
					Column<T,?> col = columnDefinitions.get(j).getColumn();
					playersTable.addColumn(col, headers.get(j));

				}
				columnsInitialized = true;
			}

			PlayerListViewColumnDefinitions.setMatch(match);

			dataProvider = new ListDataProvider<T>();
			playersTable.setLoadingIndicator(new Image("/resources/images/ajax-loader.gif"));
			dataProvider.addDataDisplay(playersTable);

			List<T> list = dataProvider.getList();
			for (T t: PlayerList) {
				list.add(t);
			}

			for (int j = 0; j < columnDefinitions.size(); ++j) {
				AddColumnSorters(j,playersTable.getColumn(j),list);
			}			
			playersTable.setVisibleRange(0, list.size());
			
			playersTable.addCellPreviewHandler( new Handler<T>() {

				@Override
				public void onCellPreview(CellPreviewEvent<T> event) {
					boolean isClick = "click".equals(event.getNativeEvent().getType());
					if (isClick) {
						if (event.getColumn() == 0) {
							listener.showEditPlayerFromTS(event.getValue());
						} else if (event.getColumn() > 1) {
							//Window.alert(((IPlayerRating)event.getValue()).getRatingComponents().get(event.getColumn()-2).getDetails()); // sigh
							listener.showEditStats(event.getValue(),event.getColumn()-2);
						} else if (event.getColumn() == 1) {
							Window.alert(((IPlayerRating)event.getValue()).getDetails());
						}
					} else if (event.getNativeEvent().getType().equals("mouseover")) {
						if (event.getColumn() > 1 && ((IPlayerRating)event.getValue()).getRatingComponents().size() > event.getColumn()-2) {
							RatingComponent rc = ((IPlayerRating)event.getValue()).getRatingComponents().get(event.getColumn()-2);
							playersTable.getRowElement(event.getIndex()).getCells().getItem(event.getColumn()).setTitle(rc.getStatsDetails()+"\n"+rc.getRatingDetails());
						} else if (((IPlayerRating)event.getValue()).getDetails() != null && event.getColumn() == 1 ) {
							playersTable.getRowElement(event.getIndex()).getCells().getItem(event.getColumn()).setTitle(((IPlayerRating)event.getValue()).getDetails());
						}
					}
				}
			});
		}
	}



	@SuppressWarnings("unchecked")
	@Override
	public void setListener(Listener<T> listener) {
		this.listener = (net.rugby.foundation.admin.client.ui.playerlistview.PlayerListView.RatingListener<T>) listener;
	}

	@Override
	public void setColumnHeaders(ArrayList<String> headers) {
		this.headers = headers;
	}



	@Override
	public void showWait() {
		//		playersTable.removeAllRows();
		//		playersTable.setWidget(0,0,new HTML("Stand by...")); //new Image("/resources/images/ajax-loader.gif"));

	}



	@Override
	public void updatePlayerMatchStats(T newPmi) {
		List<T> list = dataProvider.getList();

		int index = 0;
		for (T t: list) {
			index++;
			if (t.getId().equals(newPmi.getId())) {
				list.remove(t);
				list.add(index-1, newPmi);
			}
		}
	}

	private boolean AddColumnSorters(int j, Column<T,?> col, List<T> PlayerList) {
		if (j == 0) {
			// Add a ColumnSortEvent.ListHandler to connect sorting to the
			// java.util.List.
			ListHandler<T> columnSortHandler = new ListHandler<T>(PlayerList);
		columnSortHandler.setComparator(col, new Comparator<T>() {
				public int compare(T o1, T o2) {
					if (o1.getPlayer().getDisplayName().equals(o2.getPlayer().getDisplayName())) {
						return 0;
					}

					// Compare the name columns.
					if (o1 != null) {
						return (o2 != null) ? o1.getPlayer().getDisplayName().compareTo(o2.getPlayer().getDisplayName()) : 1;
					}
					return -1;
				}
			});
			playersTable.addColumnSortHandler(columnSortHandler);
		} else if (j == 1) {
			// Add a ColumnSortEvent.ListHandler to connect sorting to the
			// java.util.List.
			ListHandler<T> columnSortHandler = new ListHandler<T>(PlayerList);
		columnSortHandler.setComparator(col, new Comparator<T>() {
				public int compare(T o1, T o2) {
					if (o1.getRating() == null) {
						return 1;
					}
					
					if (o2.getRating() == null) {
						return -1;
					}
					if (o1.getRating().equals(o2.getRating())) {
						return 0;
					}

					// Compare the rating columns.
					if (o1 != null) {
						return (o2 != null) ? o1.getRating().compareTo(o2.getRating()) : 1;
					}
					return -1;
				}
			});
			playersTable.addColumnSortHandler(columnSortHandler);
		}
		return true;
	}



	@Override
	public void clear() {
		if (dataProvider != null) {
			dataProvider.setList(new ArrayList<T>());
		}
	}

}
