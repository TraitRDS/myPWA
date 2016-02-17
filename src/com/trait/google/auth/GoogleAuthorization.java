package com.trait.google.auth;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by yli on 1/28/2016.
 */
public class GoogleAuthorization {

    private static final String AUTHORIZATION_SERVER_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String TOKEN_SERVER_URL = "https://www.googleapis.com/oauth2/v4/token";
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static JsonFactory JSON_FACTORY = new JacksonFactory();

    private String apiKey;
    private String apiSecret;
    private String scope;
    private VerificationCodeReceiver receiver;
    private DataStoreFactory dataStoreFactory;

    public GoogleAuthorization(String apiKey, String apiSecret, String scope, VerificationCodeReceiver receiver, DataStoreFactory dataStoreFactory) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.scope = scope;
        this.receiver = receiver;
        this.dataStoreFactory = dataStoreFactory;
    }

    public Credential authorize(String userName) throws IOException {
        AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
                HTTP_TRANSPORT,
                JSON_FACTORY,
                new GenericUrl(TOKEN_SERVER_URL),
                new ClientParametersAuthentication(apiKey, apiSecret),
                apiKey,
                AUTHORIZATION_SERVER_URL).setScopes(Arrays.asList(scope)).setDataStoreFactory(dataStoreFactory).build();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize(userName);
    }
}
