package net.rugby.foundation.client.ui;

import java.util.ArrayList;

import net.rugby.foundation.client.ClientFactory;
import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.Feature;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Admin extends DialogBox {


	
	private static AdminUiBinder uiBinder = GWT.create(AdminUiBinder.class);
	private ClientFactory clientFactory = null;
	
	interface AdminUiBinder extends UiBinder<Widget, Admin> {
	}

	public Admin() {
		setWidget(uiBinder.createAndBindUi(this));
		
	}

	@UiField Button endRound;
	@UiField Button close;
	@UiField HTML textArea;
	@UiField TextBox adminEmail;
	@UiField Button makeAdminButton;
	@UiField TextBox superAdminEmail;
	@UiField Button makeSuperAdminButton;
	@UiField Button updateScores;
	@UiField TextArea noLoginText;
	@UiField Button noLoginSubmitButton;
	@UiField TextArea noDraftText;
	@UiField Button noDraftSubmitButton;
	@UiField TextArea noRoundText;
	@UiField Button noRoundSubmitButton;
	@UiField TextArea completeText;
	@UiField Button completeSubmitButton;
	@UiField TextBox featureID;
	@UiField TextArea featurePreamble;
	@UiField Button setFeatureButton;
	@UiField Button convertCompetition;
	@UiField Button sendEmails;
	
	@UiHandler("sendEmails")
	void onSendEmailsClick(ClickEvent e) {
		clientFactory.getRpcService().sendEmails(0L, new AsyncCallback<String>() {

			public void onFailure(Throwable error) {
	    		  String blah = "failure in call";

	    		  textArea.setHTML(blah);					
		      }

		      public void onSuccess(String result) {
		    	  String blah = "success";
		    	  if (result != null) {
		    			  blah = result + "<br>";
		    	  } else {
		    		  blah = "failure (admin access needed?)";
		    	  }

		    	  textArea.setHTML(blah);
		      }
		});		
	}
	
	@UiHandler("convertCompetition")
	void onConvertCompetitionClick(ClickEvent e) {
		clientFactory.getRpcService().endStage(0L, new AsyncCallback<ArrayList<String>>() {

			public void onFailure(Throwable error) {
	    		  String blah = "failure in call";

	    		  textArea.setHTML(blah);					
		      }

		      public void onSuccess(ArrayList<String> result) {
		    	  String blah = "success";
		    	  if (result != null) {
		    		  for (String s : result) {
		    			  blah += s + "<br>";
		    		  }
		    	  } else {
		    		  blah = "failure (admin access needed?)";
		    	  }

		    	  textArea.setHTML(blah);
		      }
		});		
	}
	
	@UiHandler("setFeatureButton")
	void onSetFeatureClick(ClickEvent e) {
		Feature feature = new Feature(featurePreamble.getText(),Long.parseLong(featureID.getText()), true);
		clientFactory.getRpcService().addFeature(feature, new AsyncCallback<Boolean>() {

			public void onFailure(Throwable error) {
	    		  String blah = "failure in call";

	    		  textArea.setHTML(blah);					
		      }

		      public void onSuccess(Boolean result) {
		    	  String blah = "success";
		    	  if (!result)
		    		  blah = "failure (admin access needed?)";

		    	  textArea.setHTML(blah);
		      }
		});		
	}
	
	@UiHandler("noLoginSubmitButton")
	void onNoLoginSubmitClick(ClickEvent e) {
		setContent(CoreConfiguration.getNoLoginContentId(),noLoginText.getText());	
	}
	
	@UiHandler("noDraftSubmitButton")
	void onNoDraftSubmitClick(ClickEvent e) {
		setContent(CoreConfiguration.getNoDraftContentId(),noDraftText.getText());	
	}
	
	@UiHandler("noRoundSubmitButton")
	void onNoRoundSubmitClick(ClickEvent e) {
		setContent(CoreConfiguration.getNoRoundContentId(),noRoundText.getText());	
	}
	
	@UiHandler("completeSubmitButton")
	void onCompleteSubmitClick(ClickEvent e) {
		setContent(CoreConfiguration.getCompleteContentId(),completeText.getText());	
	}
	
	private void setContent(long id, String text) {
		clientFactory.getRpcService().setContent(id, text, new AsyncCallback<Boolean>() {

			public void onFailure(Throwable error) {
	    		  String blah = "failure in call";

	    		  textArea.setHTML(blah);					
		      }

		      public void onSuccess(Boolean result) {
		    	  String blah = "success";
		    	  if (!result)
		    		  blah = "failure (admin access needed?)";

		    	  textArea.setHTML(blah);
		      }
		});		
	}

	private void getContent(final Long id) {
		clientFactory.getRpcService().getContent(id,  new AsyncCallback<String>() {

			public void onFailure(Throwable error) {
	    		  String blah = "failure getting content ";

	    		  textArea.setHTML(blah);
		      }

		      public void onSuccess(String result) {
		    	  if (result.isEmpty()) {
				}
		    	  if (id == CoreConfiguration.getNoLoginContentId()) {
		    		  noLoginText.setText(result);
		    	  } else if (id == CoreConfiguration.getNoDraftContentId()) {
		    		  noDraftText.setText(result);
		    	  } else if (id == CoreConfiguration.getNoRoundContentId()) {
		    		  noRoundText.setText(result);
		    	  } else if (id == CoreConfiguration.getCompleteContentId()) {
		    		  completeText.setText(result);
		    	  }
		      }
		});		
	}
	@UiHandler("makeAdminButton")
	void onMakeAdminClick(ClickEvent e) {

		clientFactory.getRpcService().makeAdmin(adminEmail.getText(), false, new AsyncCallback<Boolean>() {


				public void onFailure(Throwable error) {
		    		  String blah = "failure in call";

		    		  textArea.setHTML(blah);					
			      }

			      public void onSuccess(Boolean result) {
			    	  String blah = "success";
			    	  if (!result)
			    		  blah = "failure";

			    	  textArea.setHTML(blah);
			      }
			});
			
	}
	
	@UiHandler("makeSuperAdminButton")
	void onSuperMakeAdminClick(ClickEvent e) {

		clientFactory.getRpcService().makeAdmin(superAdminEmail.getText(), true, new AsyncCallback<Boolean>() {


				public void onFailure(Throwable error) {
		    		  String blah = "failure in call";

		    		  textArea.setHTML(blah);					
			      }

			      public void onSuccess(Boolean result) {
			    	  String blah = "success";
			    	  if (!result)
			    		  blah = "failure";

			    	  textArea.setHTML(blah);
			      }
			});
			
	}

	
	@UiHandler("endRound")
	void onEndRoundClick(ClickEvent e) {

		clientFactory.getRpcService().endRound(new ArrayList<Long>(), new AsyncCallback<ArrayList<String>>() {


				public void onFailure(Throwable error) {
			      }

			      public void onSuccess(ArrayList<String> result) {
			    	  String blah = "<ul>";
			    	  for (String r : result) {
			    		  blah += "<li>" + r + "</li>";
			    	  }
			    	  blah += "</ul>";
			    	  textArea.setHTML(blah);
			      }
			});
			
	}
	
	
	@UiHandler("updateScores")
	void onUpdateScoresClick(ClickEvent e) {

		clientFactory.getRpcService().updateRoundScores(CoreConfiguration.getCurrentround()-2, new AsyncCallback<String>() {

				public void onFailure(Throwable error) {
			    	  textArea.setHTML("fail");
			      }

			      public void onSuccess(String result) {
			    	  textArea.setHTML(result);
			      }
			});
			
	}
	
	@UiHandler("close")
	void onCloseClick(ClickEvent e) {
		hide();
		
	}
	public ClientFactory getClientFactory() {
		return clientFactory;
	}

	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		getContent(CoreConfiguration.getNoLoginContentId());
		getContent(CoreConfiguration.getNoDraftContentId());
		getContent(CoreConfiguration.getNoRoundContentId());
		getContent(CoreConfiguration.getCompleteContentId());
		Integer roundToEnd = CoreConfiguration.getCurrentround()-2;
		endRound.setText("End Round " + roundToEnd);
		updateScores.setText("Update Scores for Round " + roundToEnd);

	}

}
