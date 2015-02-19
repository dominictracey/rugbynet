package net.rugby.foundation.topten.client.ui.toptenlistview;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Div;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ISponsor;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
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
	
	@UiField
	protected Row sponsorRow;
	@UiField 
	protected Column compCol;
//	@UiField 
//	protected Column dropdownCol;
	@UiField 
	protected Column sponsorCol;
	@UiField
	protected Div compSpacer;
	@UiField 
	Div sponsorDiv;
	@UiField
	protected Div sponsorSpacer;
	@UiField
	protected HTML sponsorTag;
	@UiField 
	protected HTML listTitle;
	
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
	@UiField Button delete;
	
	@UiField Div redittLink;
	
	ITopTenList list = null;

	
	HTML body = null;
	
	public FeatureListViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		edit.setVisible(false);
		publish.setVisible(false);
		promote.setVisible(false);
		delete.setVisible(false);
		
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
		
		//sponsorTag.setHTML("<center>delivered by DHL</center>");
		sponsorTag.addStyleName("font-size:.5em");
		
		sponsorRow.addStyleName("sponsor");
		
		compCol.setStylePrimaryName("comp-logo");
		compCol.addStyleName("col-xs-3");
		sponsorCol.setStylePrimaryName("sponsor-logo");
		sponsorCol.addStyleName("col-xs-3");

		compSpacer.setHeight("38px");
		sponsorSpacer.setHeight("38px");

	}
	

	private ClientFactory clientFactory;
	private FeatureListViewPresenter presenter;
	protected String currentCompStyle = "";
	protected String currentSponsorStyle = "";

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
			listTitle.setHTML("<center><h5>"+list.getTitle()+"</h5></center>");	
			
			if (list.getCompId() != null) {
				Core.getCore().getComp(list.getCompId(), new AsyncCallback<ICompetition>() {

					@Override
					public void onFailure(Throwable caught) {
						if (!currentCompStyle.isEmpty()) {
							compCol.setStyleDependentName(currentCompStyle, false);
						}
						compCol.setStyleDependentName("NON", true);
						currentCompStyle = "NON";
					}

					@Override
					public void onSuccess(ICompetition result) {
						// remove any style we already have before adding the new one
						if (!currentCompStyle.isEmpty()) {
							compCol.setStyleDependentName(currentCompStyle, false);
						}
						if (result.getAbbr() == null || result.getAbbr().isEmpty()) {
							compCol.setStyleDependentName("NON", true);
							currentCompStyle = "NON";
						} else {
							compCol.setStyleDependentName(result.getAbbr(), true);
							currentCompStyle = result.getAbbr();
						}
						
						clientFactory.getSponsorForList(list, new AsyncCallback<ISponsor>() {

							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void onSuccess(ISponsor result) {
								
								
								// remove any style we already have before adding the new one
								if (!currentSponsorStyle.isEmpty()) {
									sponsorCol.setStyleDependentName(currentSponsorStyle, false);
								}
								if (result == null || result.getAbbr() == null || result.getAbbr().isEmpty()) {
									sponsorCol.setStyleDependentName("NON", true);
									currentSponsorStyle = "NON";
									sponsorTag.setHTML("");
								} else {
									sponsorCol.setStyleDependentName(result.getAbbr(), true);
									currentSponsorStyle = result.getAbbr();
									sponsorTag.setHTML("<center>"+ result.getTagline() + "</center>");
									clientFactory.recordAnalyticsEvent("sponsor", "show", result.getName(), 1);
								}
								
							}
							
						});
					}

				});
			}
			
			// from  http://www.reddit.com/buttons/
			// couldn't get the script to fire in the minute or so I played with it.
//			redittLink.clear();
//			redittLink.add(new HTML("<script type=\"text/javascript\">" +
//			  "reddit_url = \"//www.reddit.com/buttons\"; " +
//			  "reddit_title = \"Buttons!\"; " +
//			  "reddit_bgcolor = \"FF3\"; " +
//			  "reddit_bordercolor = \"00F\"; " +
//			  "reddit_target=\"/r/rugbyunion\"; " +
//			  "</script>" +
//			  "<script type=\"text/javascript\" src=\"//www.redditstatic.com/button/button3.js\"></script>"));
			if (list.getLive()) {
				publish.setText("Unpublish");
			} else {
				publish.setText("Publish");
			}
		} else {
			// just blank everything out I guess
			if (body != null) {
				featureBody.remove(body);
			}
			
			listTitle.setHTML("No feature");
		}

		Element elem = DOM.getElementById("app");
	    if (elem != null) {
	        elem.scrollIntoView();
	    }

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
	
	@UiHandler("delete")
	void onDeleteButtonClicked(ClickEvent event) {	
		presenter.delete(list);
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
		delete.setVisible(show);
		
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