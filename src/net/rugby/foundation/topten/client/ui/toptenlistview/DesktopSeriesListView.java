package net.rugby.foundation.topten.client.ui.toptenlistview;

import java.util.HashMap;
import java.util.Map;

import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;

import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.TabPanel;
import org.gwtbootstrap3.client.ui.constants.TabPosition;
import org.gwtbootstrap3.client.ui.html.ClearFix;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class DesktopSeriesListView extends SeriesListViewImpl {
	
	protected TabPanel tabPanel;
	protected NavTabs navTabs;
	protected TabContent tabContent;
	
	protected Map<Long, TabListItem> tabIndexMap = new HashMap<Long, TabListItem>();
	protected Map<Long, TabPane> paneIndexMap = new HashMap<Long, TabPane>();
	
	
//	<!-- 			<b:TabPanel tabPosition="LEFT" ui:field="tabPanel"> -->
//	<!-- 				<b:NavTabs ui:field="navTabs"> -->
//	<!-- 				</b:NavTabs> -->
//	<!-- 				<b:TabContent ui:field="tabContent">			     -->
//	<!-- 				</b:TabContent> -->
//	<!-- 				<b.html:ClearFix /> -->
//	<!-- 			</b:TabPanel> -->
	
	public DesktopSeriesListView() {
		tabPanel = new TabPanel();
		tabPanel.setTabPosition(TabPosition.LEFT);
		tabPanel.addStyleName("overflowAuto");
		
		navTabs = new NavTabs();
		navTabs.setId("tabPanelWidget");
		tabContent = new TabContent();
		tabContent.setId("tabbedContent");
		//tabContent.addStyleName("overflowHidden");
		
		tabPanel.add(navTabs);
		tabPanel.add(tabContent);
//		tabPanel.add(new ClearFix());
		
		content.add(tabPanel);
		
		matrixGroup.setVisible(false);
	}
	
	@Override	
	public void setMatrix(IRatingMatrix matrix) {
		this.matrix = matrix;
		if (matrix != null) {
			matrixId = matrix.getId();
			navTabs.clear();
			tabContent.clear();
			tabIndexMap.clear();
			paneIndexMap.clear();
			for (IRatingQuery query : matrix.getRatingQueries()) {	
				final IRatingQuery rq = query;
				if (rq.getTopTenListId() != null) {

					final TabListItem t = new TabListItem(rq.getLabel());
					
					// the paired TabPane to show when the tab is clicked
					final TabPane pane = new TabPane();
					pane.setFade(true);

					tabIndexMap.put(rq.getId(), t);
					paneIndexMap.put(rq.getId(), pane);
					tabContent.add(pane);

					t.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							setQuery(null);
							queryId = rq.getId();
							setList(null);
							setItemId(null);
							presenter.gotoPlace(getPlace());
							pane.add(listView);
						}

					});


					
					t.setDataTargetWidget(pane);
					navTabs.add(t);
					


				}
			
			}
			criteriaButton.setText(matrix.getCriteria().getMenuName());
			presenter.process(getPlace());
		} else {
			matrixId = null;
		}

	}
	
	@Override	
	public void setQuery(IRatingQuery query) {
		this.query = query;
		if (query != null) {
			queryId = query.getId();
			
			tabIndexMap.get(query.getId()).showTab();
			paneIndexMap.get(query.getId()).add(listView);

			presenter.process(getPlace());
		} else {
			queryId = null;
		}
	}
}
