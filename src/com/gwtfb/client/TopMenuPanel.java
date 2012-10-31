package com.gwtfb.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.gwtfb.sdk.FBCore;

/**
 * Display Top Menu
 * @author ola
 */
public class TopMenuPanel extends Composite {

	private HorizontalPanel outer = new HorizontalPanel ();
	
	public TopMenuPanel (final FBCore fbCore) {
	    AppImageBundle images = GWT.create( AppImageBundle.class);
	    
		outer.getElement().setId("TopMenu");
		outer.add ( new Image ( images.logo() ) );
        outer.add ( new HTML ( "<div style='margin-top: 2px; float: right;'><div class='fb-login-button' autologoutlink='true' scope='publish_stream,read_stream' /> </div>" ) );
    
        
        /*
        // Login callback, empty basically
        final AsyncCallback<JavaScriptObject> loginCallback = new AsyncCallback<JavaScriptObject>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert ( "failed to login");
			}

			@Override
			public void onSuccess(JavaScriptObject result) {
				// TODO Auto-generated method stub
				
			}
        	
        };
        
        Anchor loginLink = new Anchor();
        loginLink.setText("Login");
        
        ClickHandler loginHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				fbCore.login(loginCallback);
				
				Window.alert("You clicked something");
			}
        };
        
        loginLink.addClickHandler(loginHandler);
        
        // outer.add(loginLink);
         */
        initWidget ( outer );
	}
	
	
}
