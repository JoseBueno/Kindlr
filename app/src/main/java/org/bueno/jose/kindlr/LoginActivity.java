package org.bueno.jose.kindlr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.bueno.jose.kindlr.entities.User;
import org.bueno.jose.kindlr.utilities.ResponseParser;
import org.bueno.jose.kindlr.utilities.StringEx;
import org.bueno.jose.kindlr.utilities.Toaster;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;


public class LoginActivity extends AppCompatActivity {


    private final OAuthProvider provider = new DefaultOAuthProvider(
            "http://www.goodreads.com/oauth/request_token",
            "http://www.goodreads.com/oauth/access_token",
            "http://www.goodreads.com/oauth/authorize");
    private final static String consumerKey = "xxx";
    private final static String consumerSecret = "xxx";
    private final static String callbackUrl = "kindlr://goodreads";

    private final static CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.btnLogIn).setOnClickListener(onLoginClicked);
        findViewById(R.id.btnCancel).setOnClickListener(onCancelClicked);

    }

    // the below is copied from YAGRAC - needs to be replaced (as well as the items above) with an intent to the FB app first if possible.
    @Override
    public void onResume()
    {
        super.onResume();

        // We might be resuming due to the web browser sending us our
        // access tokens.  If so, save them and finish.
        Uri uri = this.getIntent().getData();
        if (uri != null && uri.toString().startsWith(callbackUrl))
        {
            String oauthToken = uri.getQueryParameter(OAuth.OAUTH_TOKEN);
            // this will populate token and token_secret in consumer
            try

            {
                // Crazy sauce can happen here. Believe it or not, the entire app may have been flushed
                // from memory while the browser was active.
                SharedPreferences sharedPreferences = getSharedPreferences("org.bueno.jose.kindlr", MODE_PRIVATE);
                String requestToken = sharedPreferences.getString("RequestToken", "");
                String requestTokenSecret = sharedPreferences.getString("RequestTokenSecret", "");
                if (requestToken.length() == 0 || requestTokenSecret.length() == 0)
                {
                    Toast.makeText(this, "The request tokens were lost, please close the browser and application and try again.", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                consumer.setTokenWithSecret(requestToken, requestTokenSecret);
                provider.retrieveAccessToken(consumer, oauthToken);

            }
            catch (OAuthMessageSignerException e1)
            {
                Toast.makeText(this, "Message signer exception:\n" + e1.getMessage(), Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            catch (OAuthNotAuthorizedException e1)
            {
                Toast.makeText(this, "Not Authorized Exception:\n" + e1.getMessage(), Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            catch (OAuthExpectationFailedException e1)
            {
                Toast.makeText(this, "Expectation Failed Exception:\n" + e1.getMessage(), Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            catch (OAuthCommunicationException e1)
            {
                Toast.makeText(this, "Communication Exception:\n" + e1.getMessage(), Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            String token = consumer.getToken();
            String tokenSecret = consumer.getTokenSecret();
            String userId = "";
            ResponseParser.SetTokenWithSecret(token, tokenSecret);


            SharedPreferences sharedPreferences = getSharedPreferences("org.bueno.jose.kindlr", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("token", token);
            editor.putString("tokenSecret", tokenSecret);
            editor.apply();

            try
            {
                User authorizedUser = ResponseParser.GetAuthorizedUser();

                userId = authorizedUser.get_Id();

                sharedPreferences = getSharedPreferences("org.bueno.jose.kindlr", MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("userId", userId);
                editor.apply();
            }
            catch (Exception e)
            {
                Toast.makeText(this, "Error getting authorized user:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            Intent tokens = new Intent(getIntent());
            tokens.putExtra("org.bueno.jose.kindlr.token", token);
            tokens.putExtra("org.bueno.jose.kindlr.tokenSecret", tokenSecret);
            tokens.putExtra("org.bueno.jose.kindlr.userId", userId);
            setResult(RESULT_OK, tokens);

            Toast.makeText(this, "Thanks for authenticating!\nPlease close the browser to continue", Toast.LENGTH_LONG).show();
            finish();
        }

        // we also might be resuming because the user backed out of the browser.
        else
        {
            SharedPreferences sharedPreferences = getSharedPreferences("org.bueno.jose.kindlr", MODE_PRIVATE);
            String token = sharedPreferences.getString("token", "");
            String tokenSecret = sharedPreferences.getString("tokenSecret", "");
            String userId = sharedPreferences.getString("userId", "");

            if (!StringEx.isNullOrWhiteSpace(token))
            {
                Intent tokens = new Intent(getIntent());
                tokens.putExtra("org.bueno.jose.kindlr.token", token);
                tokens.putExtra("org.bueno.jose.kindlr.tokenSecret", tokenSecret);
                tokens.putExtra("org.bueno.jose.kindlr.userId", userId);
                setResult(RESULT_OK, tokens);
                finish();
            }
        }
    }

    private final View.OnClickListener onLoginClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new AsyncAuthentication(provider, consumer, callbackUrl, LoginActivity.this).execute("");
        }
    };

    public void doLogin(String authUrl){

        if (StringEx.isNullOrWhiteSpace(authUrl)){
            Toaster.makeToast(LoginActivity.this, "Unable to authenticate the application", Toast.LENGTH_LONG);
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("org.bueno.jose.kindlr", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("RequestToken", consumer.getToken());
        editor.putString("RequestTokenSecret", consumer.getTokenSecret());
        editor.apply();

        Context context = this;
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
    }

    private final View.OnClickListener onCancelClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LoginActivity.this.finishAffinity();
        }
    };


    // Tutorial level stuff. Correct implementation is a generic AsyncNetworkTask
    // that has the request params passed in and response wrappers retrieved.
    class AsyncAuthentication extends AsyncTask<String, String, String>{

        private OAuthProvider provider;
        private CommonsHttpOAuthConsumer consumer;
        private String callbackUrl;
        private LoginActivity loginActivity; // NOOOOOO! pass in a class that implements a listener instead of a concrete class!

       public AsyncAuthentication(OAuthProvider provider, CommonsHttpOAuthConsumer consumer, String callbackUrl, LoginActivity loginActivity){
           this.provider = provider;
           this.consumer = consumer;
           this.callbackUrl = callbackUrl;
           this.loginActivity = loginActivity;
       }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (loginActivity != null) loginActivity.doLogin(s);
        }

        @Override
        protected String doInBackground(String... params) {
            String result ="";
            try {
                result = provider.retrieveRequestToken(consumer, callbackUrl);
            } catch (OAuthMessageSignerException
                    | OAuthCommunicationException
                    | OAuthExpectationFailedException
                    | OAuthNotAuthorizedException e) {
                e.printStackTrace();
                // IDE generated handling. Should be wrapped up in an object that contains the result and exception
            }

            return result;
        }
    }
}
