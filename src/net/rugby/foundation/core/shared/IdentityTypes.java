package net.rugby.foundation.core.shared;

import net.rugby.foundation.model.shared.LoginInfo;

public class IdentityTypes {
	public enum Actions { login, logout, createFacebook, createOpenId, mergeFacebook, mergeOpenId, done, updateScreenName, validateEmail, optOut }
	public enum Keys { action, selector, destination, providerType, email, validationCode, optoutCode }

	public static LoginInfo.ProviderType getProviderType(String string) {
		if (string.toLowerCase().equals("oauth2")) {
			return LoginInfo.ProviderType.oauth2;
		} else if (string.toLowerCase().equals("openid")) {
			return LoginInfo.ProviderType.openid;
		} else if (string.toLowerCase().equals("facebook")) {
			return LoginInfo.ProviderType.facebook;
		}
		return null;
	}

	public static LoginInfo.Selector getSelector(String string) {
		if (string.toLowerCase().equals("google")) {
			return LoginInfo.Selector.google;
		} else if (string.toLowerCase().equals("yahoo")) {
			return LoginInfo.Selector.yahoo;
		} if (string.toLowerCase().equals("myspace")) {
			return LoginInfo.Selector.myspace;
		} if (string.toLowerCase().equals("aol")) {
			return LoginInfo.Selector.aol;
		} if (string.toLowerCase().equals("myopenid_com")) {
			return LoginInfo.Selector.myopenid_com;
		}
		return null;
	}
}
