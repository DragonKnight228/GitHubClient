package com.example.githubclient;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.browser.customtabs.CustomTabsIntent;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.ClientSecretPost;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import java.io.IOException;

public class LogInActivity extends AppCompatActivity {
    TokenStorage tokenStorage;
    AuthorizationService authorizationService;
    AuthorizationServiceConfiguration configuration;
    AuthorizationResponse response;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tokenStorage = new TokenStorage();
        if (tokenStorage.accessToken != null){
            Intent intent = new Intent(LogInActivity.this, MainScreenActivity.class);
            startActivity(intent);
        }

        authorizationService = new AuthorizationService(getApplication());
        configuration = new AuthorizationServiceConfiguration(
                Uri.parse("https://github.com/login/oauth/authorize"),
                Uri.parse("https://github.com/login/oauth/access_token"),
                null,
                Uri.parse("https://github.com/logout")
        );
    }

    public void Authorise(View view) throws IOException {
        CustomTabsIntent cctIntent = new CustomTabsIntent.Builder().build();

        Intent gitHubAuthorizationPage = authorizationService.getAuthorizationRequestIntent(
            getAuthorizationRequest(),
                cctIntent
        );
        getAuthResponse.launch(gitHubAuthorizationPage);
    }



    ActivityResultLauncher<Intent> getAuthResponse = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        handleAuthResponseIntent(data);
                    }
                }
            });

    public void handleAuthResponseIntent(Intent intent) {
        Exception exception = AuthorizationException.fromIntent(intent);
        response =  AuthorizationResponse.fromIntent(intent);
        if (response != null) {
            TokenRequest tokenExchangeRequest = response.createTokenExchangeRequest();
            if (exception != null) {
                Toast.makeText(LogInActivity.this, "Ошибка входа", Toast.LENGTH_LONG).show();
            }
            if (tokenExchangeRequest != null){
                performTokenRequest(authorizationService, tokenExchangeRequest);
            }
        }

    }

    public void performTokenRequest(AuthorizationService authorizationService, TokenRequest tokenRequest) {
        authorizationService.performTokenRequest(
                response.createTokenExchangeRequest(),
                getClientAuthentication(),
                new AuthorizationService.TokenResponseCallback() {
                    @Override public void onTokenRequestCompleted(
                            TokenResponse resp, AuthorizationException ex) {
                        if (resp != null) {
                            tokenStorage.accessToken = resp.accessToken;
                            tokenStorage.refreshToken = resp.refreshToken;
                            Intent intent = new Intent(LogInActivity.this, MainScreenActivity.class);
                            intent.putExtra("token", resp.accessToken);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LogInActivity.this, "Нет доступа", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private ClientAuthentication getClientAuthentication() {
        return new ClientSecretPost("b82521cfe4a9c34e6613417163f6071e9c5095d8");
    }

    public AuthorizationRequest getAuthorizationRequest() {
        Uri redirectUri = Uri.parse("ru.kts.oauth://github.com/callback");
        return new AuthorizationRequest.Builder(
                configuration,
                "0f9f8983462a5e0516fd",
                ResponseTypeValues.CODE,
                redirectUri
        ).setScope("user, repo").build();
    }



}

