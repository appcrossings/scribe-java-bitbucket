package se.mnord.scribe.builder.api;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.ParameterList;
import org.scribe.model.Verb;
import se.mnord.scribe.oauth.BitbucketService20;
import org.scribe.oauth.OAuthService;

/**
 * OAuth 2.0 API for Bitbucket.
 *
 * @author Mattias Nordvall
 */
public class BitbucketApi20 extends DefaultApi20 {
    private static final String AUTHORIZE_URL = "https://bitbucket.org/site/oauth2/authorize";
    private static final String TOKEN_URL = "https://bitbucket.org/site/oauth2/access_token";

    @Override
    public String getAccessTokenEndpoint() {
        return TOKEN_URL;
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config) {
        ParameterList parameters = new ParameterList();
        parameters.add("response_type", "code");
        parameters.add("client_id", config.getApiKey());

        if (config.getCallback() != null) {
            parameters.add("redirect_uri", config.getCallback());
        }

        return parameters.appendTo(AUTHORIZE_URL);
    }

    @Override
    public OAuthService createService(OAuthConfig config) {
        return new BitbucketService20(this, config);
    }

    @Override
    public AccessTokenExtractor getAccessTokenExtractor()
    {
        return new JsonTokenExtractor();
    }
}
