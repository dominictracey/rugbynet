/**
 * 
 */
package net.rugby.foundation.admin.client.ui.promote;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.ListGroupItem;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.UnorderedList;
import org.gwtbootstrap3.extras.bootbox.client.Bootbox;
import org.gwtbootstrap3.extras.bootbox.client.callback.ConfirmCallback;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.ui.ColumnDefinition;
import net.rugby.foundation.admin.client.ui.SmartBar;
import net.rugby.foundation.admin.shared.IBlurb;
import net.rugby.foundation.topten.model.shared.ITopTenList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author home
 *
 */
public class PromoteViewImpl<T extends IBlurb> extends Composite implements PromoteView<T> {



	interface PromoteViewImplUiBinder extends UiBinder<Widget, PromoteViewImpl<?>> {
	}

	private static PromoteViewImplUiBinder uiBinder = GWT
			.create(PromoteViewImplUiBinder.class);

	@UiField FlexTable blurbTable;
	@UiField SimplePanel menuBarPanel;
	@UiField Button addNew;
	@UiField Button addDigest;
	@UiField Button recipientsDigest;
	@UiField TextBox blurbUrl;
	@UiField Button suggest;
	@UiField TextBox blurbLinkText;
	@UiField TextArea blurbBodyText;
	@UiField Button blurbSubmit;
	@UiField Modal blurbModal;
	@UiField ListGroup dragPanel;

	@UiField Modal digestModal;
	@UiField Button digestPreview;
	@UiField Button digestSend;
	@UiField TextArea message;

	@UiField Modal previewModal;
	@UiField HTML previewHtml;

	@UiField HTML recipientsHtml;
	@UiField Modal recipientsModal;
	
	@UiField Button archive;
	@UiField Button facebook;
	@UiField Button twitter;
	
	private PromoteViewColumnDefinitions<T> columnDefinitions;
	private List<T> blurbList;
	private PromoteViewPresenter<T> listener;

	private ArrayList<String> headers;

	private ClientFactory clientFactory;

	private SmartBar smartBar;



	public PromoteViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		blurbTable.getRowFormatter().addStyleName(0, "groupListHeader");
		blurbTable.addStyleName("groupList");
		blurbTable.getCellFormatter().addStyleName(0, 1, "groupListNumericColumn");

		// class="list-unstyled ui-sortable draggablePanelList"
		dragPanel.addStyleName("list-unstyled");
		dragPanel.addStyleName("ui-sortable");
		dragPanel.addStyleName("draggablePanelList");
		
		facebook.setVisible(false);
		twitter.setVisible(false);
	}

	@Override
	public void setColumnDefinitions(PromoteViewColumnDefinitions<T> defs) {
		this.columnDefinitions = defs;
		defs.setListener(listener);
		setColumnHeaders(columnDefinitions.getHeaders());
	}

	@UiHandler("recipientsDigest")
	void onShowRecipeients(ClickEvent event) {
		clientFactory.getRpcService().getDigestUserList(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				recipientsHtml.setText("Didn't work");
				
			}

			@Override
			public void onSuccess(String result) {
				recipientsHtml.setHTML(result);
				recipientsModal.show();
				
			}
			
		});
	}
	
	@UiHandler("digestSend")
	void onDigestSend(ClickEvent event) {
		Bootbox.confirm("Are you sure you want to email this digest to The Rugby Net user base?", new ConfirmCallback() {
			@Override
			public void callback(boolean result) {
				if (result) {
					List<Long> blurbIds = getBlurbIds();
					listener.sendDigest(message.getText(), blurbIds);
					digestModal.hide();
				}
			}
		});
	}
	
	@UiHandler("previewSend")
	void onPreviewSend(ClickEvent event) {
		Bootbox.confirm("Are you sure you want to email this digest to The Rugby Net user base?", new ConfirmCallback() {
			@Override
			public void callback(boolean result) {
				if (result) {
					List<Long> blurbIds = getBlurbIds();
					listener.sendDigest(message.getText(), blurbIds);
					previewModal.hide();
					digestModal.hide();
					
				}
			}
		});
	}


	@UiHandler("suggest")
	void onSuggestButtonClicked(ClickEvent event) {

		listener.getClientFactory().getRpcService().getListNameForUrl(blurbUrl.getText(), new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Notify.notify("That's a shit url");

			}

			@Override
			public void onSuccess(String result) {
				blurbLinkText.setText(result);					
			}

		});

	}

	@UiHandler("digestPreview")
	void onPreviewClicked(ClickEvent event) {
		List<Long> blurbIds = getBlurbIds();
		listener.digestPreview(message.getText(), blurbIds);
	}
	
	@UiHandler("archive")
	void onArchiveClicked(ClickEvent event) {
		listener.archive();
	}
	
	@UiHandler("facebook")
	void onFacebookClicked(ClickEvent event) {
		listener.facebook();
	}
	
	@UiHandler("twitter")
	void onTwitterClicked(ClickEvent event) {
		listener.twitter();
	}

	private List<Long> getBlurbIds() {
		List<Long> blurbIds = new ArrayList<Long>();
		Element dpe = dragPanel.getElement();
		NodeList<Element> children = dpe.getElementsByTagName("li");
		for  (int i=0; i < children.getLength(); ++i) {
			Long id = Long.parseLong(children.getItem(i).getId());
			blurbIds.add(id);
		}
		
		return blurbIds;
	}

	@UiHandler("addDigest")
	void onAddDigest(ClickEvent event) {
		List<T> blurbs = listener.getSelected();
		dragPanel.clear();

		//		for (int i=0; i<RootPanel.get("draggablePanelList").getWidgetCount(); ++i) {
		//			RootPanel.get("draggablePanelList").remove(i);
		//		}
		//		
		for (T blurb : blurbs) {
			ListGroupItem panel = new ListGroupItem();
			panel.addStyleName("panel-info");
			PanelHeader header = new PanelHeader();
			header.setText(blurb.getLinkText());
			panel.add(header);
			PanelBody body = new PanelBody();
			HTML bodyText = new HTML(blurb.getBodyText());
			body.add(bodyText);
			panel.add(body);
			panel.setId(blurb.getId().toString());
			dragPanel.add(panel);
		}
		initDraggable();
	}

	@UiHandler("blurbSubmit")
	void onNewBlurbClicked(ClickEvent event) {
		listener.newBlurb(blurbUrl.getText(),blurbLinkText.getText(),blurbBodyText.getText());
	}
	@UiHandler("blurbTable")
	void onTableClicked(ClickEvent event) {
		if (listener != null) {
			HTMLTable.Cell cell = blurbTable.getCellForEvent(event);

			if (cell != null) {
				if (cell.getRowIndex() == 0) { // we have a check box up and click the select all
					if (cell.getCellIndex() == 0) {
						if (columnDefinitions.getColumnDefinitions().get(0).isSelectable()) {
							// select them all
							for (int i = 1; i < blurbList.size()+1; ++i) {
								((CheckBox)blurbTable.getWidget(i, 0).asWidget()).setValue(true);
								listener.onItemSelected(blurbList.get(i-1));
							}	   			  
						}  
					}
				} else { 
					T Promote = blurbList.get(cell.getRowIndex()-1);
					if (shouldFireClickEvent(cell)) {
						if (cell.getCellIndex() == 0) {
							listener.onItemSelected(Promote);
						} else {						
							listener.onItemClicked(Promote, cell.getRowIndex()-1);
						}
					}

					//					if (shouldFireSelectEvent(cell)) { // only do it if we have a checkbox up
					//						listener.onItemSelected(Promote);
					//						if (listener != null) {
					//							//important sanity check because we are clicking on the table and not the checkbox. And we can miss.
					//							int x = cell.getRowIndex();
					//							((CheckBox)blurbTable.getWidget(x,0).asWidget()).setValue(listener.onItemSelected(player));
					//						}
					//					}
				}
			}
		}
	}


	@Override
	public void showList(List<T> blurbList) {
		if (blurbList != null) {
			blurbTable.removeAllRows();
			this.blurbList = blurbList;
			setHeaders();
			String style = "leaderboardRow-odd";

			//	      Date begin = new Date();
			for (int i = 1; i < blurbList.size()+1; ++i) {
				T t = blurbList.get(i-1);
				for (int j = 0; j < columnDefinitions.getColumnDefinitions().size(); ++j) {
					ColumnDefinition<T> columnDefinition = columnDefinitions.getColumnDefinitions().get(j);

					//Logger.getLogger("debug").log(Level.SEVERE,"("+i+","+j+"): "+ System.currentTimeMillis());


					blurbTable.setWidget(i, j, columnDefinition.render(t));
					blurbTable.getRowFormatter().setStyleName(i, style);

				}
				if (style == "leaderboardRow-odd")
					style = "leaderboardRow-even";
				else
					style = "leaderboardRow-odd";

			}

			//	      Date end = new Date();

			//	      Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,begin.toLocaleString() + " end: " + end.toLocaleString());
		}
	}

	private void setHeaders() {
		int i = 0;
		for (String s : headers) {
			blurbTable.setHTML(0, i++, s);	
		}

		blurbTable.getRowFormatter().addStyleName(0, "leaderboardRow-header");


	}

	private boolean shouldFireClickEvent(HTMLTable.Cell cell) {
		boolean shouldFireClickEvent = false;

		if (cell != null) {
			ColumnDefinition<T> columnDefinition =
					columnDefinitions.getColumnDefinitions().get(cell.getCellIndex());

			if (columnDefinition != null) {
				shouldFireClickEvent = columnDefinition.isSelectable() || columnDefinition.isClickable();

			}
		}

		return shouldFireClickEvent;
	}


	@Override
	public void setColumnHeaders(ArrayList<String> headers) {
		this.headers = headers;
	}



	@Override
	public void showWait() {
		blurbTable.removeAllRows();
		blurbTable.setWidget(0,0,new HTML("Stand by...")); //new Image("/resources/images/ajax-loader.gif"));	
	}

	@Override
	public void setPresenter(PromoteViewPresenter<T> p) {
		listener = p;
		if (listener instanceof SmartBar.Presenter) {
			if (!menuBarPanel.getElement().hasChildNodes()) {
				smartBar = clientFactory.getMenuBar();
				menuBarPanel.add(smartBar);
			}
			smartBar.setPresenter((SmartBar.Presenter)listener);		
		}

		if (columnDefinitions != null) {
			columnDefinitions.setListener(p);
		}
	}

	@Override
	public PromoteViewPresenter<T> getPresenter() {
		return listener;
	}

	@Override
	public void showError(T blurb, int index, String message) {
		Window.alert(message);
		// do something cool with a row formatter!
	}

	@Override
	public void updateBlurbRow(T blurb) {
		// ignore i
		// find the right row
		boolean found = false;
		int index = -1;
		for (int k = 0; k < blurbList.size(); ++k) {
			if (blurbList.get(k).getId().equals(blurb.getId())) {
				found = true;
				index = k;
				break;
			}
		}
		if (found == true) {
			for (int j = 0; j < columnDefinitions.getColumnDefinitions().size(); ++j) {
				ColumnDefinition<T> columnDefinition = columnDefinitions.getColumnDefinitions().get(j);
				blurbTable.setWidget(index+1, j, columnDefinition.render(blurb));
			}
		} else {
			// must be a new one, append and redraw
			blurbList.add(blurb);
			showList(blurbList);
		}
	}

	@Override
	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public Modal getBlurbModal() {
		return blurbModal;
	}

	@Override
	public void showBlurb(IBlurb b) {
		blurbUrl.setText(b.getServerPlace().getGuid());
		blurbLinkText.setText(b.getLinkText());
		blurbBodyText.setText(b.getBodyText());
		blurbModal.show();
	}

	@Override
	public void showPreview(String text) {
		previewHtml.setHTML(text);
		previewModal.show();
	}

	public native void initDraggable()
	/*-{
		$wnd.jQuery(function($) {
	        var panelList = $('.draggablePanelList');

	        panelList.sortable({
	            // Only make the .panel-heading child elements support dragging.
	            // Omit this to make then entire <li>...</li> draggable.
	            handle: '.panel-heading', 
	            update: function() {
	                $('.panel', panelList).each(function(index, elem) {
	                     var $listItem = $(elem),
	                         newIndex = $listItem.index();

	                     // Persist the new indices.
	                });
	            }
	        });
	    });
    }-*/;
}

