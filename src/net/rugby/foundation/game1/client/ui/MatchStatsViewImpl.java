package net.rugby.foundation.game1.client.ui;

import java.util.List;
import java.util.Map;

import net.rugby.foundation.game1.shared.IConfiguration;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.game1.shared.IMatchStats;
import net.rugby.foundation.model.shared.IClubhouse;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.Color;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.ImageChart;
import com.google.gwt.visualization.client.visualizations.ImageChart.AnnotationColumn;
import com.google.gwt.visualization.client.visualizations.ImageChart.AnnotationColumn.Priority;
import com.google.gwt.visualization.client.visualizations.ImageChart.Options;

import com.google.gwt.visualization.client.visualizations.corechart.PieChart;

public class MatchStatsViewImpl extends Composite implements MatchStatsView {

	interface Binder extends UiBinder<Widget, MatchStatsViewImpl> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	private Presenter listener;
	@UiField
	HTMLPanel panel;

	private Long matchId;
	private String homeColor = "#DFDFDF";
	private String awayColor = "#0E0E0E";
	private String fontColorHome = "#000000";
	private String fontColorAway = "#FFFFFF";
	private String homeName;
	private String awayName;
	private ImageChart bar;

	public class ImageChartMouseEventHandler implements MouseOverHandler, MouseOutHandler {
		  public void onMouseOver(final MouseOverEvent moe) {
		    nativeMouseIn();
		  }

		  public void onMouseOut(final MouseOutEvent moe) {
			  nativeMouseOut();
		  }
	}


	public MatchStatsViewImpl() {
		initWidget(binder.createAndBindUi(this));
	}


	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;

		listener.getMatchStats(matchId, new AsyncCallback<List<IMatchStats>>() {

			@Override
			public void onFailure(Throwable caught) {
				//ignore

			}

			@Override
			public void onSuccess(List<IMatchStats> result) {
				setData(result);

			}

		});
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.SmartBar#setComps(java.util.Map)
	 */
	@Override
	public void setData(final List<IMatchStats> stats) {
		Runnable onLoadCallback = new Runnable() {


			public void run() {

				// Create a pie chart visualization.
				bar = new ImageChart();
				Long homePicks = 0L;
				Long allPicks = 0L;
				for (IMatchStats ms : stats) {
					homePicks += ms.getNumHomePicks();
					allPicks += ms.getNumPicks();
				}
				
				if (allPicks > 0) {
	
					bar.draw(createTable(homePicks, allPicks), createOptions());
	
					//((Widget)bar).addMouseOverHandler(new ImageChartMouseEventHandler());
	
					panel.add(bar);
					//setUpToolTips();
				}
			}


		};

		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback, ImageChart.PACKAGE);


	}


	private Options createOptions() {
		Options options = Options.create();
		options.setWidth(150);
		options.setHeight(50);
		//options.setColors(getColors());
		options.setColors(homeColor,awayColor); //"#BCBCBC",
		options.set("cht", "bhs");
		options.set("chds","0,1,0,1");
		//options.set("chxt", "10,10,10,10");
		//options.set("enableEvents", "true");
		options.setShowCategoryLabels(false);
		options.setShowValueLabels(false);
		options.set("backgroundColor", "#BCBCBC");
		options.setLegend(LegendPosition.NONE);
		// home style
		ImageChart.AnnotationColumn aColHome = ImageChart.AnnotationColumn.create(2,12);
		aColHome.setPositionColumn(0);
		aColHome.setColor(fontColorHome);
		aColHome.setPriority(Priority.HIGH);
		aColHome.setDrawFlag(false);

		//visit style
		ImageChart.AnnotationColumn aColVisit = ImageChart.AnnotationColumn.create(3,12);
		aColVisit.setPositionColumn(1);
		aColVisit.setColor(fontColorAway);
		aColVisit.setPriority(Priority.HIGH);
		aColVisit.setDrawFlag(false);
		
		options.setAnnotationColumns(getAnnotationColumnArray(aColHome, aColVisit));

		//options.set3D(true);
		// options.setTitle("My Daily Activities");
		return options;
	}

	private AbstractDataTable createTable(Long homePicks, Long allPicks) {
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.NUMBER, "");
		data.addColumn(ColumnType.NUMBER, "");
		data.addColumn(ColumnType.STRING, "");
		data.addColumn(ColumnType.STRING, "");
		data.addRow();
		data.addRow();
		double homeVal = (double)homePicks/(double)allPicks;
		double visitVal = (double)(allPicks-homePicks)/(double)allPicks;
		data.setValue(0, 0, homeVal);
		//data.setValue(0, 1, 1-homeVal);
		data.setValue(1, 1, visitVal);
		//data.setValue(1, 1, 1-visitVal);
		//String col = data.getProperty(1, 0, "chco");
		String homeAnnotation = Long.toString(Math.round(homeVal*100)) + "% have picked " + homeName;
		data.setValue(0,2, homeAnnotation);
		String visitAnnotation = Long.toString(Math.round(visitVal*100)) + "% have picked " + awayName;
		data.setValue(1,3, visitAnnotation);		
		return data;
	}
	//
	//	  private native JsArrayString getOptions() /*-{ return {cht: 'bvs', chs: '300x125', colors:['#4D89F9','#C6D9FD'],
	//          chds:'0,160', chxl:'0:|oranges|apples|pears|bananas|kiwis|'}; }-*/;

	private native JsArrayString getColors() /*-{ return [this.@net.rugby.foundation.game1.client.ui.MatchStatsViewImpl::homeColor,this.@net.rugby.foundation.game1.client.ui.MatchStatsViewImpl::awayColor]; }-*/;

	private native void setUpToolTips() /*-{ 
	      // Add our over/out handlers.
//		   $wnd.google.visualization.events.addListener(this.@net.rugby.foundation.game1.client.ui.MatchStatsViewImpl::bar, 'onmouseover', barMouseOver);
//		   $wnd.google.visualization.events.addListener(this.@net.rugby.foundation.game1.client.ui.MatchStatsViewImpl::bar, 'onmouseout', barMouseOut);

		  function barMouseOver(e) {
		    this.@net.rugby.foundation.game1.client.ui.MatchStatsViewImpl::bar.setSelection([e]);
		  }

		  function barMouseOut(e) {
		    this.@net.rugby.foundation.game1.client.ui.MatchStatsViewImpl::bar.setSelection([{'row': null, 'column': null}]);
		  }


	  }-*/;

	private native void nativeMouseIn() /*-{ 
	  	this.@net.rugby.foundation.game1.client.ui.MatchStatsViewImpl::bar.setSelection([e]);
	  }-*/;

	private native void nativeMouseOut() /*-{ 
  		this.@net.rugby.foundation.game1.client.ui.MatchStatsViewImpl::bar.setSelection([{'row': null, 'column': null}]);	 
	}-*/;

	private native JsArray<AnnotationColumn> getAnnotationColumnArray(AnnotationColumn h, AnnotationColumn v) /*-{ 
		return [h,v];
	}-*/;
	
	public String getHomeColor() {
		return homeColor;
	}


	public void setHomeColor(String homeColor) {
		if (homeColor == null) return;
		
		this.homeColor = homeColor;
		
		if (homeColor == null || homeColor.toLowerCase().equals("#ffffff")) {
			setHomeFontColor("#000000");
		} else {
			setHomeFontColor("#ffffff");
		}
	}


	public String getAwayColor() {
		return awayColor;

	}


	public void setAwayColor(String awayColor) {
		if (awayColor == null) return;
		
		this.awayColor = awayColor;
		if (awayColor == null || awayColor.toLowerCase().equals("#ffffff")) {
			setAwayFontColor("#000000");
		} else {
			setAwayFontColor("#ffffff");
		}	}


	public void setHomeFontColor(String fontColor) {
		this.fontColorHome = fontColor;
	}

	public void setAwayFontColor(String fontColor) {
		this.fontColorAway = fontColor;
	}
	
	public void setHomeName(String homeName) {
		this.homeName = homeName;
	}

	public void setAwayName(String awayName) {
		this.awayName = awayName;
	}


	public void setMatchId(Long matchId) {
		this.matchId = matchId;
	}
}
