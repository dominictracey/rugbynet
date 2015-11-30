/**
 * 
 */
package net.rugby.foundation.topten.client.ui;

import java.util.List;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.ImageAnchor;
import org.gwtbootstrap3.client.ui.ListItem;
import org.gwtbootstrap3.client.ui.MediaBody;
import org.gwtbootstrap3.client.ui.MediaList;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.html.Paragraph;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.place.FeatureListPlace;
import net.rugby.foundation.topten.client.ui.ColumnDefinition;
import net.rugby.foundation.topten.model.shared.Feature;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author home
 *
 */
public class RecentFeaturesViewImpl extends Composite {



	interface RecentFeatureViewImplUiBinder extends UiBinder<Widget, RecentFeaturesViewImpl> {
	}
	
	private static RecentFeatureViewImplUiBinder uiBinder = GWT
			.create(RecentFeatureViewImplUiBinder.class);


	@UiField PanelHeader header;
	@UiField MediaList features;

	//private RecentFeatureViewColumnDefinitions<T> columnDefinitions;
	private List<Feature> RecentFeatureList;

	private ClientFactory clientFactory;
	
	public RecentFeaturesViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
//		RecentFeatureTable.getRowFormatter().addStyleName(0, "groupListHeader");
//		RecentFeatureTable.addStyleName("groupList");
//		RecentFeatureTable.getCellFormatter().addStyleName(0, 1, "groupListNumericColumn");
		HTML head = new HTML("<strong>Recent Features</strong>");
		header.add(head);
		//header.setText("Recent Features");
	}

//	@Override
//	public void setColumnDefinitions(RecentFeatureViewColumnDefinitions<T> defs) {
//		this.columnDefinitions = defs;
//		defs.setListener(listener);
//	}

	final int MAX_NUM_FEATURES = 4;

	public void setRecentFeatures(List<Feature> recentFeatureList) {
		if (recentFeatureList != null) {
			int count = 0;
			for (Feature f: recentFeatureList) {
				if (count < MAX_NUM_FEATURES) {
					features.add(render(f));
					++count;
				} else {
					break;
				}
			}
		}
	}

//	<!-- 					<b:ListItem> -->
//	<!-- 						<b:ImageAnchor asMediaObject="true" pull="LEFT" url="..." /> -->
//	<!-- 						<b:MediaBody> -->
//	<!-- 							<b:Heading size="..." text="..." /> -->
//	<!-- 							<b.html:Paragraph text="..." /> -->
//	<!-- 						</b:MediaBody> -->
//	<!-- 					</b:ListItem> -->
	protected ListItem render(final Feature f) {
		ListItem li = new ListItem();
		final ImageAnchor ia = new ImageAnchor();
		ia.setAsMediaObject(true);
		ia.setPull(Pull.LEFT);
		if (f.getsPlace().getCompId() != null) {
			Core.getCore().getComp(f.getsPlace().getCompId(), new AsyncCallback<ICompetition>() {

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(ICompetition result) {
					ia.setUrl("/resources/comps/" + result.getAbbr() + "/200.png");		
					ia.setHeight("32px");
					ia.setWidth("32px");
				}				
			});
			
		} else {
			ia.setUrl("/icons/favicon-32x32.png");
		}
		
		ia.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				FeatureListPlace flp = new FeatureListPlace(f.getsPlace().getCompId(),f.getsPlace().getListId(),null);
				clientFactory.getPlaceController().goTo(flp);
			}
			
		});
		li.add(ia);
		
		MediaBody mb = new MediaBody();
		Heading h = new Heading(HeadingSize.H5, f.getTitle());
		Paragraph p = new Paragraph();
		
		p.setText(f.getPublished().toLocaleString());
		
		Anchor a = new Anchor();
		a.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				FeatureListPlace flp = new FeatureListPlace(f.getsPlace().getCompId(),f.getsPlace().getListId(),null);
				clientFactory.getPlaceController().goTo(flp);
			}
			
		});
		
		a.add(h);
		mb.add(a);
		mb.add(p);
		li.add(mb);
		
		return li;
	}
//	@Override
//	public void showWait() {
////		RecentFeatureTable.removeAllRows();
////		RecentFeatureTable.setWidget(0,0,new HTML("Stand by...")); //new Image("/resources/images/ajax-loader.gif"));	
//	}
	
//	@Override
//	public void setPresenter(RecentFeatureViewPresenter<T> p) {
//		listener = p;
//		
//		if (columnDefinitions != null) {
//			columnDefinitions.setListener(p);
//		}
//	}

//	@Override
//	public RecentFeatureViewPresenter<T> getPresenter() {
//		return listener;
//	}
	
//	@Override
//	public void showError(T RecentFeature, int index, String message) {
//		Window.alert(message);
//		// do something cool with a row formatter!
//	}

//	@Override
//	public void updateRecentFeatureRow(T RecentFeature) {
//		// ignore i
//		// find the right row
//		boolean found = false;
//		int index = -1;
//		for (int k = 0; k < seriesConfigurationList.size(); ++k) {
//			if (seriesConfigurationList.get(k).getId().equals(RecentFeature.getId())) {
//				found = true;
//				index = k;
//				break;
//			}
//		}
//		if (found == true) {
//			for (int j = 0; j < columnDefinitions.getColumnDefinitions().size(); ++j) {
//				ColumnDefinition<T> columnDefinition = columnDefinitions.getColumnDefinitions().get(j);
//				RecentFeatureTable.setWidget(index+1, j, columnDefinition.render(RecentFeature));
//			}
//		} else {
//			// must be a new one, append and redraw
//			seriesConfigurationList.add(RecentFeature);
//			showList(seriesConfigurationList);
//		}
//	}
//

	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}



}

