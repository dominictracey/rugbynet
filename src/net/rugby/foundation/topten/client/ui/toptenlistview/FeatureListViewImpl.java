package net.rugby.foundation.topten.client.ui.toptenlistview;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.constants.IconSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.client.ui.html.Span;

import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.model.shared.ITopTenList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;


public class FeatureListViewImpl extends Composite implements FeatureListView<ITopTenList> {

	private static TopTenListViewImplUiBinder uiBinder = GWT.create(TopTenListViewImplUiBinder.class);

	interface TopTenListViewImplUiBinder extends UiBinder<Widget, FeatureListViewImpl>
	{
	}

	@UiField Div featureBody;
	@UiField Div featuredTTL;
	
	// next and prev features nav
	@UiField Column topColumn;
	@UiField Column left;
	@UiField Anchor left_arrow;
	@UiField Column prev;
	@UiField Anchor prevList;
	@UiField Column right;
	@UiField Anchor right_arrow;
	@UiField Column next;
	@UiField Anchor nextList;
	
	// admin buttons
	@UiField Div adminButtons;
	@UiField Button edit;
	@UiField Button publish;
	@UiField Button promote;
	
	ITopTenList list = null;
	
	Element root = null;
	
	HTML body = null;
	
	public FeatureListViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		edit.setVisible(false);
		publish.setVisible(false);
		promote.setVisible(false);
		
		left_arrow.setIcon(IconType.CHEVRON_LEFT);
		left.addStyleName("col-xs-1");
		left.addStyleName("vertical-center");
		left.addStyleName("pull-left");
		right_arrow.setIcon(IconType.CHEVRON_RIGHT);
		right.addStyleName("col-xs-1");
		right.addStyleName("vertical-center");
		right.addStyleName("pull-right");
		prev.addStyleName("col-xs-5");
		next.addStyleName("col-xs-5");
		
		root = DOM.getElementById("toptenRoot");
		topColumn.setStyleName("col-md-9");
	}
	

	private ClientFactory clientFactory;
	private FeatureListViewPresenter presenter;
	
	@Override
	public void setList(ITopTenList result, String baseUrl) {
		list = result;
		//featureBody.clear();
		if (added) {
			clientFactory.getEditTTLInfo().setVisible(false);
		}
		
		if (result != null) {
			clientFactory.getSimpleView().setList(result, baseUrl);
			
			if (body != null) {
				featureBody.remove(body);
			}
			body = new HTML(list.getContent());
			body.addStyleName("toptentext");
			featureBody.add(body);
			
			featuredTTL.add(clientFactory.getSimpleView());
						
			if (list.getLive()) {
				publish.setText("Unpublish");
			} else {
				publish.setText("Publish");
			}
		}

	
		
//		this.removeStyleName("row-md-12");
//		this.addStyleName("row-md-8");
	}

	@Override
	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public ITopTenList getList() {
		return list;
	}

	@Override
	public void showContent(boolean show) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void hasNext(boolean has) {
		right_arrow.setVisible(has);
		if (!has) {
			nextList.setText("");
		}
	}


	@Override
	public void hasPrev(boolean has) {
		left_arrow.setVisible(has);
		if (!has) {
			prevList.setText("");
		}
	}
	
	protected HandlerRegistration nextArrowReg = null;
	protected HandlerRegistration prevArrowReg = null;
	protected HandlerRegistration nextReg = null;
	protected HandlerRegistration prevReg = null;
	
	@Override
	public void setPresenter(final FeatureListViewPresenter presenter) {
		this.presenter = presenter;	
		
		// tie in next and prev anchors
		if (nextReg != null) {
			nextReg.removeHandler();
			nextReg = null;
		}
		if (prevReg != null) {
			prevReg.removeHandler();
			prevReg = null;
		}
		
		if (nextArrowReg != null) {
			nextArrowReg.removeHandler();
			nextArrowReg = null;
		}
		if (prevArrowReg != null) {
			prevArrowReg.removeHandler();
			prevArrowReg = null;
		}
		
		nextReg = nextList.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				presenter.showNext();
			}
			
		});
		
		prevReg = prevList.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				presenter.showPrev();
			}
			
		});
		
		nextArrowReg = right_arrow.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				presenter.showNext();
			}
			
		});
		
		prevArrowReg = left_arrow.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				presenter.showPrev();
			}
			
		});
	}

	
//	@UiHandler("prevButton")
//	void onPrevButtonClicked(ClickEvent event) {	
//		presenter.showPrev();
//	}
//
//	@UiHandler("nextButton")
//	void onNextButtonClicked(ClickEvent event) {	
//		presenter.showNext();
//	}

	@UiHandler("edit")
	void onEditButtonClicked(ClickEvent event) {	
		presenter.edit(list);
	}

	@UiHandler("publish")
	void onPublishButtonClicked(ClickEvent event) {	
		if (list != null) {
			if (list.getLive()) {
				presenter.unpublish(list);
			} else {
				presenter.publish(list);
			}
		}
	}
	
	@UiHandler("promote")
	void onPromoteButtonClicked(ClickEvent event) {	
		presenter.promote(list);
	}
	
	private boolean added=false;
	@Override
	public void editList(ITopTenList list) {
		clientFactory.getEditTTLInfo().showTTL(list);
		if (body != null) {
			featureBody.remove(body);
		}
		
		if (!added) {
			featureBody.add(clientFactory.getEditTTLInfo());
			added=true;
		} else {
			clientFactory.getEditTTLInfo().setVisible(true);
		}

	}

	@Override
	public void showEditorButtons(boolean show) {
		publish.setVisible(show);
		
	}

	@Override
	public void showContributorButtons(boolean show) {
		edit.setVisible(show);
		promote.setVisible(show);

		if (list != null) {
			promote.setEnabled(list.getLive());	
		}
	}
	
	@Override
	public void expandView(boolean expand) {
		if (topColumn != null) {
			if (expand) {
				topColumn.setStyleName("col-md-12");
			} else {
				topColumn.setStyleName("col-md-9");
			}
		}
	}

	@Override
	public Anchor getNextLabel() {
		return nextList;
	}

	@Override
	public Anchor getPrevLabel() {
		return prevList;
	}







}