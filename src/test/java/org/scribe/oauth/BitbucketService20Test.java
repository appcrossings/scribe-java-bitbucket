package org.scribe.oauth;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.scribe.builder.api.BitbucketApi20;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.*;
import org.scribe.services.Base64Encoder;
import org.scribe.services.CommonsEncoder;
import org.scribe.utils.OAuthEncoder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BitbucketService20Test {
    private Base64Encoder base64Encoder = new CommonsEncoder();

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void successfulApiResponseShouldReturnCorrectOAuthToken() {
        OAuthConfig config = new OAuthConfig("key", "secret", "http://mydomain.com", null, null, null);
        String rawResponse = "{\"access_token\": \"abc123\" }";

        final Response mockResponse = mock(Response.class);
        when(mockResponse.getCode()).thenReturn(200);
        when(mockResponse.getBody()).thenReturn(rawResponse);

        OAuthService service = new BitbucketService20(new BitbucketApi20(), config) {
            @Override
            protected Response sendRequest(OAuthRequest request) {
                return mockResponse;
            }
        };

        Token token = service.getAccessToken(null, new Verifier("xyz789"));

        assertEquals(token.getToken(), "abc123");
        assertEquals(token.getRawResponse(), rawResponse);
    }

    @Test
    public void outgoingTokenRequestShouldContainCorrectParameters() {
        final OAuthConfig config = new OAuthConfig("key", "secret", "http://mydomain.com", null, null, null);
        final Verifier verifier = new Verifier("xyz789");

        final Response mockResponse = mock(Response.class);
        when(mockResponse.getCode()).thenReturn(200);
        when(mockResponse.getBody()).thenReturn("{\"access_token\": \"abc123\" }");

        OAuthService service = new BitbucketService20(new BitbucketApi20(), config) {
            @Override
            protected Response sendRequest(OAuthRequest request) {
                String formBody = request.getBodyParams().asFormUrlEncodedString();

                assertTrue(formBody.contains("grant_type=authorization_code"));
                assertTrue(formBody.contains("code=" + verifier.getValue()));
                assertTrue(formBody.contains("redirect_uri=" + OAuthEncoder.encode(config.getCallback())));

                assertEquals(request.getHeaders().get("Authorization"),
                        "Basic " + base64Encoder.encode("key:secret".getBytes()).toString());

                return mockResponse;
            }
        };

        service.getAccessToken(null, verifier);
    }


    @Test
    public void ifNoCallbackUrlIsConfiguredTheRedirect_uriParameterIsOmitted() {
        OAuthConfig config = new OAuthConfig("key", "secret", null, null, null, null);

        final Response mockResponse = mock(Response.class);
        when(mockResponse.getCode()).thenReturn(200);
        when(mockResponse.getBody()).thenReturn("{\"access_token\": \"abc123\" }");

        OAuthService service = new BitbucketService20(new BitbucketApi20(), config) {
            @Override
            protected Response sendRequest(OAuthRequest request) {
                String formBody = request.getBodyParams().asFormUrlEncodedString();

                assertFalse(formBody.contains("redirect_uri="));
                return mockResponse;
            }
        };

        service.getAccessToken(null, new Verifier("xyz789"));
    }

    @Test
    public void httpErrorCodeShouldThrowException() {
        OAuthConfig config = new OAuthConfig("key", "secret", "http://mydomain.com", null, null, null);

        final Response mockResponse = mock(Response.class);
        when(mockResponse.getCode()).thenReturn(401);
        when(mockResponse.getBody()).thenReturn("{ \"error\": { \"message\": \"Error\" } }");

        OAuthService service = new BitbucketService20(new BitbucketApi20(), config) {
            @Override
            protected Response sendRequest(OAuthRequest request) {
                return mockResponse;
            }
        };

        exception.expect(OAuthException.class);
        service.getAccessToken(null, new Verifier("xyz789"));
    }

    @Test
    public void testSignRequest() {
        OAuthConfig config = new OAuthConfig("key", "secret");

        OAuthService service = new BitbucketService20(new BitbucketApi20(), config);

        OAuthRequest request = new OAuthRequest(Verb.GET, "http://dummy");
        Token token = new Token("abc123", "secret");
        service.signRequest(token, request);

        assertTrue(request.getHeaders().containsKey("Authorization"));
        assertEquals(request.getHeaders().get("Authorization"), "Bearer abc123");
    }
}
