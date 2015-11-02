package se.mnord.scribe.oauth;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.*;
import org.scribe.oauth.OAuth20ServiceImpl;
import org.scribe.services.Base64Encoder;
import org.scribe.services.CommonsEncoder;

import java.util.logging.Logger;

/**
 * OAuth 2.0 Service for Bitbucket.
 *
 * @author Mattias Nordvall
 */
public class BitbucketService20 extends OAuth20ServiceImpl {
    private Base64Encoder base64Encoder = new CommonsEncoder();
    private Logger logger = Logger.getLogger(BitbucketService20.class.getName());
    private DefaultApi20 api;
    private OAuthConfig config;

    public BitbucketService20(DefaultApi20 api, OAuthConfig config) {
        super(api, config);
        this.api = api;
        this.config = config;
    }

    /**
     * Uses the authorization code to retrieve an access token from Bitbucket
     *
     * @param requestToken  Not used
     * @param verifier      A {@link org.scribe.model.Verifier} object containing the authorization code
     * @return              A {@link org.scribe.model.Token} containing the access token
     */
    @Override
    public Token getAccessToken(Token requestToken, Verifier verifier) {
        OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint());

        // Bitbucket is using basic authentication
        String clientCredentials = createBasicCredentials(config.getApiKey(), config.getApiSecret());
        request.addHeader("Authorization", "Basic ".concat(clientCredentials));

        request.addBodyParameter("grant_type", "authorization_code");
        request.addBodyParameter("code", verifier.getValue());

        if (this.config.getCallback() != null) {
            request.addBodyParameter("redirect_uri", this.config.getCallback());
        }

        logger.info("Sending OAuth access token request to " + api.getAccessTokenEndpoint());
        Response response = sendRequest(request);
        String responseBody = response.getBody();
        logger.finer("Response body: " + responseBody);

        if (response.getCode() >= 400) {
            throw new OAuthException("HTTP error response from Bitbucket: " + response.getCode());
        }

        Token scribeToken = api.getAccessTokenExtractor().extract(responseBody);

        return scribeToken;
    }

    private String createBasicCredentials(String username, String password) {
        String plainTextCredentials = String.format("%s:%s", username, password);
        String encodedCredentials = base64Encoder.encode(plainTextCredentials.getBytes()).toString();
        return encodedCredentials;
    }

    /**
     * Sends the request and returns the response.
     * Override this method in unit tests
     *
     * @param request   The prepared request
     * @return          The response
     */
    protected Response sendRequest(OAuthRequest request) {
        return request.send();
    }

    /**
     * Inserts the access token into the request.
     * Used when making authenticated api calls (after all tokens are exchanged).
     */
    @Override
    public void signRequest(Token accessToken, OAuthRequest request) {
        request.addHeader("Authorization", "Bearer ".concat(accessToken.getToken()));
    }
}
