package com.gwtfb.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtfb.sdk.FBCore;

public class UserInfoViewController extends Composite {
	
    private HTML welcomeHtml = new HTML ();
	private VerticalPanel outer = new VerticalPanel ();

	private FBCore fbCore;
	
	/**
	 * Display User info
	 */
	class MeCallback extends Callback<JavaScriptObject> {
		public void onSuccess ( JavaScriptObject response ) {
			renderMe ( response );
		}
	}


	/**
	 * Render information about logged in user
	 */
	private void renderMe ( JavaScriptObject response ) {
		JSOModel jso = response.cast();
		welcomeHtml.setHTML ( "<h3> Hi,  " + jso.get ( "name" ) + "</h3> GwtFB is a simple GWT Facebook Graph Client. "  );
	}

	/**
	 * Render publish
	 */
	public void streamPublish () {
		
	    JSONObject streamPublish = new JSONObject ();
	    streamPublish.put( "method", new JSONString ( "stream.publish" ) );
	    streamPublish.put( "message", new JSONString ( "Getting education about Facebook Connect and GwtFB" ) );
	    
	    JSONObject attachment = new JSONObject ();
	    attachment.put( "name", new JSONString ( "GwtFB" ) );
	    attachment.put("caption", new JSONString ( "The Facebook Connect Javascript SDK and GWT" ) );
	    attachment.put( "description", new JSONString ( "A small GWT library that allows you to interact with Facebook Javascript SDK in GWT ") ); 
	    attachment.put("href",  new JSONString ( "http://www.gwtfb.com" ) );
	    streamPublish.put( "attachment", attachment );

	    JSONObject actionLink = new JSONObject ();
	    actionLink.put ( "text", new JSONString ( "Code" ) );
	    actionLink.put ( "href", new JSONString ( "http://www.gwtfb.com" ) );

	    JSONArray actionLinks = new JSONArray ();
	    actionLinks.set(0, actionLink);
	    streamPublish.put( "action_links", actionLinks);

	    streamPublish.put( "user_message_prompt", new JSONString ( "Share your thoughts about Connect and GWT" ) );
	    
	    fbCore.ui(streamPublish.getJavaScriptObject(), new Callback () );
	    
	}
	
	/**
	 * Render share
	 */
	public void testShare () {
	    JSONObject data = new JSONObject ();
	    data.put( "method", new JSONString ( "stream.share" ) );
	    data.put( "u", new JSONString ( "http://www.gwtfb.com" ) );
	    fbCore.ui ( data.getJavaScriptObject(), new Callback () );
	}
	
	
	/**
	 * New View
	 */
	public UserInfoViewController ( final FBCore fbCore ) {

		// Old style dependency injection
	    this.fbCore = fbCore;

	    outer.add ( welcomeHtml );
		outer.add ( new HTML ( "<p/>" ) );
		outer.add( new HTML ( "<h1>Like GwtFB on facebook</h1><div class='fb-like' data-href='http://www.facebook.com/apps/application.php?id=37309251911&amp;sk=page_getting_started' data-send='false' data-width='450' data-show-faces='false'></div>"));
//        outer.add ( new HTML ( "<hr/><fb:comments css='http://www.gwtfb.com/GwtFB.css?123' xid='gwtfb' />" ) );
	
		
		fbCore.api ( "/me" , new MeCallback () );
		// fbCore.api ( "/f8/posts",  new PostsCallback () );
		initWidget ( outer );
	}
}
