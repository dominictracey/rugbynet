package net.rugby.foundation.admin.client.ui.playerlistview;

import java.util.ArrayList;
import java.util.List;

import org.gwtbootstrap3.client.ui.Tooltip;
import org.gwtbootstrap3.client.ui.constants.Placement;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import net.rugby.foundation.admin.client.ui.ColumnDefinition;
import net.rugby.foundation.admin.client.ui.playerlistview.PlayerListView.Listener;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerRating;

public class PlayerListViewColumnDefinitions<T extends IPlayerRating> {

	private static List<ColumnDefinition<IPlayerRating>> columnDefinitions =
			new ArrayList<ColumnDefinition<IPlayerRating>>();

	private static PlayerListView.Listener<IPlayerRating> listener;
	private static IMatchGroup match;

	public PlayerListViewColumnDefinitions() {
		if (columnDefinitions.isEmpty()) {

			if (match != null) {
			columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {
				// slot
				@Override
				public Widget render(IPlayerRating c) {
					String name = c.getMatchStats().get(0).getName();
					return new HTML(name);
				}

				@Override
				public Column<IPlayerRating, ?> getColumn() {
					
					Column<IPlayerRating, ?> col =  new TextColumn<IPlayerRating>() {
						@Override
						public String getValue(IPlayerRating c) {
							String slot = "";
							if (match != null) {
								if (c.getMatchStats().get(0).getTeamId().equals(match.getHomeTeamId()))
									slot = "H" + c.getMatchStats().get(0).getSlot().toString();
								else 
									slot = "V" + c.getMatchStats().get(0).getSlot().toString();
							}
							return slot;
						}
					};
					col.setSortable(true);
					return col;
					
				}

			});
			} else {
				columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {
					// team abbr as link to TeamMatcHStats
					@Override
					public Widget render(IPlayerRating c) {
						String name = c.getMatchStats().get(0).getTeamAbbr();
						return new HTML(name);
					}

					@Override
					public Column<IPlayerRating, ?> getColumn() {
						
						Column<IPlayerRating, ?> col =  new TextColumn<IPlayerRating>() {
							@Override
							public String getValue(IPlayerRating c) {
								String teamName = "";
								if (c.getMatchStats().get(0).getTeamAbbr() != null) {
									teamName = c.getMatchStats().get(0).getTeamAbbr();
								} 
								String slot = teamName + c.getMatchStats().get(0).getSlot();
								return slot;
							}
						};
						col.setSortable(true);
						return col;
						
					}

				});
			}

			columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {

				public Widget render(IPlayerRating c) {
					String name = c.getMatchStats().get(0).getName();
					return new HTML(name);
				}

//				public boolean isClickable() {
//					return true;
//				}


		        
				@Override
				public Column<IPlayerRating, ?> getColumn() {
					Column<IPlayerRating, ?> col = new TextColumn<IPlayerRating>() {
						@Override
						public String getValue(IPlayerRating c) {
							String name = c.getMatchStats().get(0).getName();
							return name;
						}
					};
					col.setSortable(true);
					return col;
				}
			});

			columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {
				public Widget render(IPlayerRating c) {
					String name = c.getMatchStats().get(0).getId().toString();
					return new HTML(name);
				}

				public boolean isClickable() {
					return true;
				}

				@Override
				public Column<IPlayerRating, ?> getColumn() {
					return new TextColumn<IPlayerRating>() {
						@Override
						public String getValue(IPlayerRating c) {
							String name = c.getMatchStats().get(0).getId().toString();
							return name;
						}
					};
				}
			});

			columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {

				public Widget render(IPlayerRating c) {
					String name = "-";
					if (c.getRating() != null && c.getRating() != null) {
						name = c.getRating().toString();
					}
					Widget w = new HTML(name);
					Tooltip tooltip = new Tooltip();
				    tooltip.setWidget(w);
				    tooltip.setText(c.getDetails());
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
							if (c.getRating() != null && c.getRating() != null) {
								name = c.getRating().toString();
							}
							return name;
						}
					};
					col.setSortable(true);
					return col;
				}
			});


			columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {
				public Widget render(IPlayerRating c) {
					String pos = c.getMatchStats().get(0).getPosition().name();
					return new HTML(pos);
				}

				@Override
				public Column<IPlayerRating, ?> getColumn() {
					return new TextColumn<IPlayerRating>() {
						@Override
						public String getValue(IPlayerRating c) {
							return c.getMatchStats().get(0).getPosition().name();
						}
					};
				}
			});

			columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {
				public Widget render(IPlayerRating c) {
					String str = c.getMatchStats().get(0).getTries().toString()  + "/" + c.getMatchStats().get(0).getTryAssists().toString();
					return new HTML(str);
				}

				@Override
				public Column<IPlayerRating, ?> getColumn() {
					return new TextColumn<IPlayerRating>() {
						@Override
						public String getValue(IPlayerRating c) {
							String str = c.getMatchStats().get(0).getTries().toString()  + "/" + c.getMatchStats().get(0).getTryAssists().toString();
							return str;
						}
					};
				}
			});

			columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {
				public Widget render(IPlayerRating c) {
					String str = c.getMatchStats().get(0).getPoints().toString();
					return new HTML(str);
				}

				@Override
				public Column<IPlayerRating, ?> getColumn() {
					return new TextColumn<IPlayerRating>() {
						@Override
						public String getValue(IPlayerRating c) {
							String str = c.getMatchStats().get(0).getPoints().toString();
							return str;
						}
					};
				}
			});

			columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {
				public Widget render(IPlayerRating c) {
					String str = c.getMatchStats().get(0).getKicks().toString() + "/" + c.getMatchStats().get(0).getPasses().toString() + "/" +  c.getMatchStats().get(0).getRuns().toString();
					return new HTML(str);
				}

				@Override
				public Column<IPlayerRating, ?> getColumn() {
					return new TextColumn<IPlayerRating>() {
						@Override
						public String getValue(IPlayerRating c) {
							String str = c.getMatchStats().get(0).getKicks().toString() + "/" + c.getMatchStats().get(0).getPasses().toString() + "/" +  c.getMatchStats().get(0).getRuns().toString();
							return str;
						}
					};
				}
			});

			columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {
				public Widget render(IPlayerRating c) {
					String str = c.getMatchStats().get(0).getMetersRun().toString();
					return new HTML(str);
				}

				@Override
				public Column<IPlayerRating, ?> getColumn() {
					return new TextColumn<IPlayerRating>() {
						@Override
						public String getValue(IPlayerRating c) {
							String str = c.getMatchStats().get(0).getMetersRun().toString();
							return str;
						}
					};
				}
			});

			columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {
				public Widget render(IPlayerRating c) {
					String str = c.getMatchStats().get(0).getCleanBreaks().toString();
					return new HTML(str);
				}

				@Override
				public Column<IPlayerRating, ?> getColumn() {
					return new TextColumn<IPlayerRating>() {
						@Override
						public String getValue(IPlayerRating c) {
							String str = c.getMatchStats().get(0).getCleanBreaks().toString();
							return str;
						}
					};
				}
			});

			columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {
				public Widget render(IPlayerRating c) {
					String str = c.getMatchStats().get(0).getDefendersBeaten().toString();
					return new HTML(str);
				}

				@Override
				public Column<IPlayerRating, ?> getColumn() {
					return new TextColumn<IPlayerRating>() {
						@Override
						public String getValue(IPlayerRating c) {
							String str = c.getMatchStats().get(0).getDefendersBeaten().toString();
							return str;
						}
					};
				}
			});

			columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {
				public Widget render(IPlayerRating c) {
					String str = c.getMatchStats().get(0).getOffloads().toString();
					return new HTML(str);
				}

				@Override
				public Column<IPlayerRating, ?> getColumn() {
					return new TextColumn<IPlayerRating>() {
						@Override
						public String getValue(IPlayerRating c) {
							String str = c.getMatchStats().get(0).getOffloads().toString();
							return str;
						}
					};
				}
			});

			columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {
				public Widget render(IPlayerRating c) {
					String str = c.getMatchStats().get(0).getTurnovers().toString();
					return new HTML(str);
				}

				@Override
				public Column<IPlayerRating, ?> getColumn() {
					return new TextColumn<IPlayerRating>() {
						@Override
						public String getValue(IPlayerRating c) {
							String str = c.getMatchStats().get(0).getTurnovers().toString();
							return str;
						}
					};
				}
			});

			columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {
				public Widget render(IPlayerRating c) {
					String str = c.getMatchStats().get(0).getTacklesMade().toString() +"/" + c.getMatchStats().get(0).getTacklesMissed().toString();
					return new HTML(str);
				}

				@Override
				public Column<IPlayerRating, ?> getColumn() {
					return new TextColumn<IPlayerRating>() {
						@Override
						public String getValue(IPlayerRating c) {
							String str = c.getMatchStats().get(0).getTacklesMade().toString() +"/" + c.getMatchStats().get(0).getTacklesMissed().toString();
							return str;
						}
					};
				}
			});

			columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {
				public Widget render(IPlayerRating c) {
					String str = c.getMatchStats().get(0).getLineoutsWonOnThrow().toString() +"/" + c.getMatchStats().get(0).getLineoutsStolenOnOppThrow().toString();
					return new HTML(str);
				}

				@Override
				public Column<IPlayerRating, ?> getColumn() {
					return new TextColumn<IPlayerRating>() {
						@Override
						public String getValue(IPlayerRating c) {
							String str = c.getMatchStats().get(0).getLineoutsWonOnThrow().toString() +"/" + c.getMatchStats().get(0).getLineoutsStolenOnOppThrow().toString();
							return str;
						}
					};
				}
			});

			columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {
				public Widget render(IPlayerRating c) {
					String str = c.getMatchStats().get(0).getPenaltiesConceded().toString();
					return new HTML(str);
				}

				@Override
				public Column<IPlayerRating, ?> getColumn() {
					return new TextColumn<IPlayerRating>() {
						@Override
						public String getValue(IPlayerRating c) {
							String str = c.getMatchStats().get(0).getPenaltiesConceded().toString();

							return str;
						}
					};
				}
			});

			columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {
				public Widget render(IPlayerRating c) {
					String str = c.getMatchStats().get(0).getYellowCards().toString() +"/" + c.getMatchStats().get(0).getRedCards().toString();
					return new HTML(str);
				}

				@Override
				public Column<IPlayerRating, ?> getColumn() {
					return new TextColumn<IPlayerRating>() {
						@Override
						public String getValue(IPlayerRating c) {
							String str = c.getMatchStats().get(0).getYellowCards().toString() +"/" + c.getMatchStats().get(0).getRedCards().toString();

							return str;
						}
					};
				}
			});

			columnDefinitions.add(new ColumnDefinition<IPlayerRating>() {
				public Widget render(IPlayerRating c) {
					String str = "--";
					if (c.getMatchStats().get(0).getTimePlayed() != null) {
						str = c.getMatchStats().get(0).getTimePlayed().toString();
					}
					return new HTML(str);
				}

				@Override
				public Column<IPlayerRating, ?> getColumn() {
					return new TextColumn<IPlayerRating>() {
						@Override
						public String getValue(IPlayerRating c) {
							String str = "--";
							if (c.getMatchStats().get(0).getTimePlayed() != null) {
								str = c.getMatchStats().get(0).getTimePlayed().toString();
							}
							return str;
						}
					};
				}
			});
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
		//headers.add("<img src='resources/cb.jpg'>");
		headers.add( "Slot");
		headers.add( "Name");
		headers.add( "Stats");
		headers.add( "Rating");		
		headers.add( "Pos");
		headers.add( "T/A");
		headers.add( "Pts");
		headers.add( "K/P/R");
		headers.add( "MR");
		headers.add( "CB");
		headers.add( "DB");
		headers.add( "OL");
		headers.add( "TO");
		headers.add( "Tack");
		headers.add( "LO");
		headers.add( "Pen");
		headers.add( "T/R");
		headers.add( "Time");

		return headers;
	}

	public IMatchGroup getMatch() {
		return match;
	}

	public static void setMatch(IMatchGroup match) {
		PlayerListViewColumnDefinitions.match = match;
	}
}
