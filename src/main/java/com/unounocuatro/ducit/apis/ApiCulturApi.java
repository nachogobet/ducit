package com.unounocuatro.ducit.apis;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;

public class ApiCulturApi extends DefaultApi20{

	@Override
	public String getAccessTokenEndpoint() {
		return "http://wso2.org/apimanager/security";
	}

	@Override
	public String getAuthorizationUrl(OAuthConfig config) {
		return "http://wso2.org/apimanager/security";
	}

}
