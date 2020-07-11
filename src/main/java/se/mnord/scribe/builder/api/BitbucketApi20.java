package se.mnord.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.OAuth2AccessTokenJsonExtractor;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.ParameterList;
import com.github.scribejava.core.model.Verb;

import java.util.Map;

/**
 * OAuth 2.0 API for Bitbucket.
 *
 * @author Mattias Nordvall
 */
public class BitbucketApi20 extends DefaultApi20 {

  private static final String AUTHORIZE_URL = "https://bitbucket.org/site/oauth2/authorize";
  private static final String TOKEN_URL = "https://bitbucket.org/site/oauth2/access_token";

  protected String getAuthorizationBaseUrl() {
    return AUTHORIZE_URL;
  }

  @Override
  public String getAccessTokenEndpoint() {
    return TOKEN_URL;
  }

  @Override
  public Verb getAccessTokenVerb() {
    return Verb.POST;
  }

  @Override
  public String getAuthorizationUrl(
      String responseType,
      String apiKey,
      String callback,
      String scope,
      String state,
      Map<String, String> additionalParams) {

    ParameterList parameters = new ParameterList(additionalParams);
    parameters.add("response_type", "code");
    parameters.add("client_id", apiKey);

    if (state != null) parameters.add("state", state);

    if (callback != null) {
      parameters.add("redirect_uri", callback);
    }

    return parameters.appendTo(AUTHORIZE_URL);
  }

  //  @Override
  //  public OAuth20Service createService(
  //      String apiKey,
  //      String apiSecret,
  //      String callback,
  //      String defaultScope,
  //      String responseType,
  //      OutputStream debugStream,
  //      String userAgent,
  //      HttpClientConfig httpClientConfig,
  //      HttpClient httpClient) {
  //    return new BitbucketService20(
  //        this,
  //        apiKey,
  //        apiSecret,
  //        callback,
  //        defaultScope,
  //        responseType,
  //        debugStream,
  //        userAgent,
  //        httpClientConfig,
  //        httpClient);
  //  }

  @Override
  public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
    return OAuth2AccessTokenJsonExtractor.instance();
  }

  public static BitbucketApi20 instance() {
    return BitbucketApi20.InstanceHolder.INSTANCE;
  }

  private static class InstanceHolder {
    private static final BitbucketApi20 INSTANCE = new BitbucketApi20();

    private InstanceHolder() {}
  }
}
