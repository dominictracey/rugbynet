package net.rugby.foundation.admin.client.ui.playerlistview;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Tooltip;
import com.github.gwtbootstrap.client.ui.constants.Placement;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import net.rugby.foundation.admin.client.ui.ColumnDefinition;
import net.rugby.foundation.admin.client.ui.playerlistview.PlayerListView.Listener;
import net.rugby.foundation.model.shared.IPlayerRating;

public class RatingListViewColumnDefinitions<T extends IPlayerRating> {

	private static List<ColumnDefinition<IPlayerRating>> columnDefinitions =
			new ArrayList<ColumnDefinition<IPlayerRating>>();

	private static PlayerListView.Listener<IPlayerRating> listener;

	public RatingListViewColumnDefinitions() {
		if (columnDefinitions.isEmpty()) {

			columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {
				// NAME
				public Widget render(IPlayerRating c) {
					String name = c.getPlayer().getDisplayName();
					return new HTML(name);
				}

		        
				@Override
				public Column<IPlayerRating, ?> getColumn() {
					Column<IPlayerRating, ?> col = new TextColumn<IPlayerRating>() {
						@Override
						public String getValue(IPlayerRating c) {
							String name = "--";
							if (c.getPlayer() != null) {
								name = c.getPlayer().getDisplayName();
							}
							return name;
						}
					};
					col.setSortable(true);
					return col;
				}
			});


			columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {
				// RATING
				public Widget render(IPlayerRating c) {
					String name = "-";
					if (c.getRating() != null ) {
						name = c.getRating().toString();
					}
					Widget w = new HTML(name);
					Tooltip tooltip = new Tooltip();
				    tooltip.setWidget(w);
				    tooltip.setText(c.getRating().toString());
				    tooltip.setPlacement(Placement.RIGHT);
				    tooltip.reconfigure();
					return w;
				}     

				public boolean isClickable() {
					return true;
				}

				@Override
				public Column<IPlayerRating, ?> getColumn() {
					Column<IPlayerRating, ?> col = new TextColumn<IPlayerRating>() {
						@Override
						public String getValue(IPlayerRating c) {
							String name = "--";
							if (c.getRating() != null) {
								name = c.getRating().toString();
							}
							return name;
						}
					};
					col.setSortable(true);
					return col;
				}
			});

			for (int i = 0; i<10; ++i) {
				final int j = i;
				columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {
					public Widget render(IPlayerRating c) {
						String str = "--";
						if (c.getRatingComponents() != null && c.getRatingComponents().size() >= j && c.getRatingComponents().get(j) != null) {
							str = c.getRatingComponents().get(j).getScaledRating() + " (" + c.getRatingComponents().get(j).getUnscaledRating() + ")" ;
						}
						return new HTML(str);
					}
	
					@Override
					public Column<IPlayerRating, ?> getColumn() {
						return new TextColumn<IPlayerRating>() {
							@Override
							public String getValue(IPlayerRating c) {
								String str = "--";
								if (c.getRatingComponents() != null && c.getRatingComponents().size() > j && c.getRatingComponents().get(j) != null) {
									str = c.getRatingComponents().get(j).getScaledRating() + " (" + c.getRatingComponents().get(j).getUnscaledRating() + ") " + c.getRatingComponents().get(j).getMatchLabel() ;
								}
								return str;
							}
						};
					}
				});
			}
		} 	
	}

	public List<ColumnDefinition<IPlayerRating>> getColumnDefinitions() {
		return columnDefinitions;
	}

	public static PlayerListView.Listener<IPlayerRating> getListener() {
		return listener;
	}

	public static void setListener(Listener<IPlayerRating> listener2) {
		listener = listener2;
	}

	public static ArrayList<String> getHeaders() {
		ArrayList<String> headers = new ArrayList<String>();
		headers.add( "Name");
		headers.add( "Rating");		
		headers.add( "1");
		headers.add( "2");
		headers.add( "3");
		headers.add( "4");
		headers.add( "5");
		headers.add( "6");
		headers.add( "7");
		headers.add( "8");
		headers.add( "9");
		headers.add( "10");

		return headers;
	}
}
