package net.rugby.foundation.admin.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import net.rugby.foundation.admin.client.ui.PlayerListView.Listener;
import net.rugby.foundation.admin.shared.IPlayerMatchInfo;

public class PlayerListViewColumnDefinitions<T> {

	private static List<ColumnDefinition<IPlayerMatchInfo>> columnDefinitions =
      new ArrayList<ColumnDefinition<IPlayerMatchInfo>>();

	private static PlayerListView.Listener<IPlayerMatchInfo> listener;
	
    public PlayerListViewColumnDefinitions() {
    	if (columnDefinitions.isEmpty()) {
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
	        public Widget render(IPlayerMatchInfo c) {
	          CheckBox checkBox = new CheckBox();
	          if (listener != null)
	        	  checkBox.setValue(listener.isSelected(c));
	          return checkBox;
	        }
	
	        public boolean isSelectable() {
	          return true;
	        }
	      });
	
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
	        public Widget render(final IPlayerMatchInfo c) {
	        	Anchor a =  new Anchor(c.getPlayerMatchStats().getName());
	        	a.addClickHandler( new ClickHandler() {
	        		@Override
	        		public void onClick(ClickEvent event) {
	        			listener.showEditPlayer(c);
	        		}	
	        	});

	        	return a;
	          //return new HTML(c.getDisplayName());
	        }
	     
	
	        public boolean isClickable() {
	          return true;
	        }
	      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(final IPlayerMatchInfo c) {
		        	Anchor a =  new Anchor(c.getPlayerMatchStats().getId().toString());
		        	a.addClickHandler( new ClickHandler() {
		        		@Override
		        		public void onClick(ClickEvent event) {
		        			listener.showEditStats(c);
		        		}	
		        	});

		        	return a;
		        }
		
		        public boolean isClickable() {
		          return true;
		        }
		  });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(final IPlayerMatchInfo c) {
		        	Anchor a = new Anchor("--");
//		        	if (c.ge() != null) {
//		        		a =  new Anchor(c.getPlayerMatchStats().getName());
//		        	}
		        	a.addClickHandler( new ClickHandler() {
		        		@Override
		        		public void onClick(ClickEvent event) {
		        			listener.showEditStats(c);
		        		}	
		        	});

		        	return a;
		          //return new HTML(c.getDisplayName());
		        }
		     
		
		        public boolean isClickable() {
		          return true;
		        }
		      });
	      
//	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
//		        public Widget render(IPlayerMatchInfo c) {
//		        	String image = "resources/arrow_Up_Green.gif";
//		        	if (c.getMovement() == Player.movement.DOWN)
//		        		image = "resources/arrow_Down_Red.gif";
//		        	else if (c.getMovement() == movement.UNCHANGED)
//		        		image = "resources/unchanged_Blue.gif";
//		        	String rating = "--";
//		        	if (c.getOverallRating() != null)
//		        		rating = c.getOverallRating().toString();
//		          return new HTML( rating + "<img src=" + image + ">");
//		        }
//		      });

	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String pos = c.getPlayerMatchStats().getPosition().name();
		          return new HTML(pos);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String rating = "--";
//		        	if (c.getMatchRating().getPlayerRating() != null)
//		        		rating = c.getMatchRating().getPlayerRating().toString();
		          return new HTML(rating);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getTries().toString()  + "/" + c.getPlayerMatchStats().getTryAssists().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getPoints().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getKicks().toString() + "/" + c.getPlayerMatchStats().getPasses().toString() + "/" +  c.getPlayerMatchStats().getRuns().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getMetersRun().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getCleanBreaks().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getDefendersBeaten().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getOffloads().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getTurnovers().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getTacklesMade().toString() +"/" + c.getPlayerMatchStats().getTacklesMissed().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getLineoutsWonOnThrow().toString() +"/" + c.getPlayerMatchStats().getLineoutsStolenOnOppThrow().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getPenaltiesConceded().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getYellowCards().toString() +"/" + c.getPlayerMatchStats().getRedCards().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = "--";
		        	if (c.getPlayerMatchStats().getTimePlayed() != null) {
		        		str = c.getPlayerMatchStats().getTimePlayed().toString();
		        	}
		          return new HTML(str);
		        }
		      });
  	} 	
    }

    public List<ColumnDefinition<IPlayerMatchInfo>> getColumnDefinitions() {
      return columnDefinitions;
    }

	public static PlayerListView.Listener<IPlayerMatchInfo> getListener() {
		return listener;
	}

	public static void setListener(Listener<IPlayerMatchInfo> listener2) {
		listener = listener2;
	}
	
	public static ArrayList<String> getHeaders() {
		ArrayList<String> headers = new ArrayList<String>();
		headers.add("<img src='resources/cb.jpg'>");
		headers.add( "Name");
		headers.add( "Stats");
		headers.add( "Rating");		
		headers.add( "Pos");
		headers.add( "Rating");
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
  }
