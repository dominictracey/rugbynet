package net.rugby.foundation.admin.client.ui.playerlistview;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.rugby.foundation.admin.client.ui.ColumnDefinition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.PlayerRating;

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


public class PlayerListViewImpl<T extends IPlayerMatchInfo> extends Composite implements PlayerListView<T>
{
	private static PlayerListViewImplUiBinder uiBinder = GWT.create(PlayerListViewImplUiBinder.class);
	private List<ColumnDefinition<T>> columnDefinitions;
	public void setColumnDefinitions(List<ColumnDefinition<T>> columnDefinitions) {
		this.columnDefinitions = columnDefinitions;
	}

	@UiTemplate("PlayerListViewImpl.ui.xml")

	interface PlayerListViewImplUiBinder extends UiBinder<Widget, PlayerListViewImpl<?>>
	{
	}

	@UiField CellTable<T> playersTable;

	private boolean columnsInitialized = false;


	//	private Presenter<T> presenter;
	private Listener<T> listener;
	private ArrayList<String> headers;
	private ListDataProvider<T> dataProvider;

	public PlayerListViewImpl()
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
							listener.showEditTeamStats(event.getValue());
						} else if (event.getColumn() == 3) {
							Window.alert(((IPlayerMatchInfo)event.getValue()).getMatchRating().getDetails()); // sigh
						} else {
							listener.showEditStats(event.getValue());
						}
					}
				}
			});
		}
	}



	@SuppressWarnings("unchecked")
	@Override
	public void setListener(Listener<T> listener) {
		this.listener = listener;
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
			if (t.getPlayerMatchStats().getId().equals(newPmi.getPlayerMatchStats().getId())) {
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
			columnSortHandler.setComparator(col,
					new Comparator<T>() {
				public int compare(T o1, T o2) {
					String o1s = o1.getPlayerMatchStats().getTeamAbbr() + " " + o1.getPlayerMatchStats().getSlot();
					String o2s = o2.getPlayerMatchStats().getTeamAbbr() + " " + o2.getPlayerMatchStats().getSlot();
					if (o1s.equals(o2s)) {
						return 0;
					}

					// Compare the team abbr columns.
					return (o2 != null) ? o1s.compareTo(o2s) : 1;

				}
			});
			playersTable.addColumnSortHandler(columnSortHandler);

			// We know that the data is sorted by slot by default.
			// @TODO doesn't differentiate by HoV
			playersTable.getColumnSortList().push(col);
		} else if (j == 1) {
			// Add a ColumnSortEvent.ListHandler to connect sorting to the
			// java.util.List.
			ListHandler<T> columnSortHandler = new ListHandler<T>(PlayerList);
		columnSortHandler.setComparator(col, new Comparator<T>() {
				public int compare(T o1, T o2) {
					if (o1.getPlayerMatchStats().getName().equals(o2.getPlayerMatchStats().getName())) {
						return 0;
					}

					// Compare the name columns.
					if (o1 != null) {
						return (o2 != null) ? o1.getPlayerMatchStats().getName().compareTo(o2.getPlayerMatchStats().getName()) : 1;
					}
					return -1;
				}
			});
			playersTable.addColumnSortHandler(columnSortHandler);
		} else if (j == 3) {
			// Add a ColumnSortEvent.ListHandler to connect sorting to the
			// java.util.List.
			ListHandler<T> columnSortHandler = new ListHandler<T>(PlayerList);
		columnSortHandler.setComparator(col, new Comparator<T>() {
				public int compare(T o1, T o2) {
					if (o1.getMatchRating() == null || ((PlayerRating) (o1.getMatchRating())).getRating() == null) {
						return 1;
					}
					
					if (o2.getMatchRating() == null || ((PlayerRating) (o2.getMatchRating())).getRating() == null) {
						return -1;
					}
					if ( ((PlayerRating) (o1.getMatchRating())).getRating().equals(((PlayerRating) (o2.getMatchRating())).getRating())) {

						return 0;
					}

					// Compare the name columns.
					if (o1 != null) {
						return (o2 != null) ? ((PlayerRating) (o1.getMatchRating())).getRating().compareTo(((PlayerRating) (o2.getMatchRating())).getRating()) : 1;
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
