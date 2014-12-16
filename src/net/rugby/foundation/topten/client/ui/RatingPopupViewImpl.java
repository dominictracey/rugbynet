package net.rugby.foundation.topten.client.ui;

import java.util.List;

import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.html.Span;

import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.PlayerRating.RatingComponent;
import net.rugby.foundation.topten.client.ClientFactory;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;


public class RatingPopupViewImpl<T extends IPlayerRating> extends DialogBox implements RatingPopupView<T> 
{
	private static NavBarViewImplUiBinder uiBinder = GWT.create(NavBarViewImplUiBinder.class);

	private ClientFactory clientFactory;
	@UiField
	protected Panel topPanel;
	@UiField
	protected Image image;
	@UiField
	protected HTML playerInfo;
	@UiField
	protected CellTable<RatingComponent> ratingInfo;
	@UiField
	protected HTML content;
	
	@UiField
	Image close; // = new Image("/resources/closeButton25.png");
	@UiField
	Span title; // = new HTML("");
	@UiField
	Column captionPanel; // = new HorizontalPanel();
	 
	private boolean columnsInitialized = false;
	interface NavBarViewImplUiBinder extends UiBinder<Widget, RatingPopupViewImpl<?>>
	{
		
	}


	public RatingPopupViewImpl()
	{
		setWidget(uiBinder.createAndBindUi(this));
		
//		Element td = getCellElement(0, 1);
//		td.removeChild((Element) td.getFirstChildElement());
//		td.appendChild(captionPanel.getElement());
		topPanel.addStyleName("col-md-4"); 
		topPanel.addStyleName("col-sm-8");
		topPanel.addStyleName("col-xs-10");
		topPanel.addStyleName("col-md-offset-2");
		topPanel.addStyleName("col-xs-offset-1");

		captionPanel.setStyleName("Caption");//width-100%
		captionPanel.addStyleName("popupCaption");//padding
		//captionPanel.add(title);
		title.addStyleName("popupCaption");//padding
		close.addStyleName("popupCloseButton");//float:right
		playerInfo.addStyleName("popupPlayerInfo");
		//captionPanel.add(close);
		content.addStyleName("popupContent");
		super.setGlassEnabled(true);
		super.setGlassStyleName("dialogGlass");
		super.setAnimationEnabled(true);
		
		ratingInfo.addColumn(new com.google.gwt.user.cellview.client.Column<RatingComponent,String>(new TextCell()){
			@Override
			public String getValue(RatingComponent r)
			{
				return r.getMatchLabel() == null ? "" : r.getMatchLabel();
			}
//			@Override
//			public String getCellStyleNames(Context context, RatingComponent value) {
//				return "compactTTL";
//
//			}
		}, "Match");
		
		ratingInfo.addColumn(new com.google.gwt.user.cellview.client.Column<RatingComponent,String>(new TextCell()){
			@Override
			public String getValue(RatingComponent r)
			{
				return Float.toString(r.getScaledRating()); 
			}
//			@Override
//			public String getCellStyleNames(Context context, RatingComponent value) {
//				return "compactTTL";
//
//			}
		}, "Match Rating");
		
		ratingInfo.addColumn(new com.google.gwt.user.cellview.client.Column<RatingComponent,String>(new TextCell()){
			@Override
			public String getValue(RatingComponent r)
			{
				return Float.toString(r.getUnscaledRating());
			}
//			@Override
//			public String getCellStyleNames(Context context, RatingComponent value) {
//				return "compactTTL";
//
//			}
		}, "Raw Rating");
	}


	@Override
	public void setRating(T rating) {
		if (rating!= null) {

			title.setText(rating.getPlayer().getDisplayName());
			playerInfo.setText("Overall Rating: " + Integer.toString(rating.getRating()));
					
			ListDataProvider<RatingComponent> dataProvider = new ListDataProvider<RatingComponent>();
			ratingInfo.setLoadingIndicator(new Image("/resources/images/ajax-loader.gif"));
			dataProvider.addDataDisplay(ratingInfo);

			List<RatingComponent> list = dataProvider.getList();
			for (RatingComponent t: rating.getRatingComponents()) {
				list.add(t);
			}
			
			ratingInfo.setVisibleRange(0, list.size());
		}
		
	}


	public ClientFactory getClientFactory() {
		return clientFactory;
	}


	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event)
	{
	 NativeEvent nativeEvent = event.getNativeEvent();
	  
	 if (!event.isCanceled()
	 && (event.getTypeInt() == Event.ONCLICK)
	 && isCloseEvent(nativeEvent))
	 {
	 this.hide();
	}
	super.onPreviewNativeEvent(event);
	}

	private boolean isCloseEvent(NativeEvent event)
	{
		return event.getEventTarget().equals(close.getElement());//compares equality of the underlying DOM elements
	}

	@Override
	public void setContent(String body) {
		content.setHTML("<hr>" + body);
		
	}

	
}