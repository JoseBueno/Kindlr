package org.bueno.jose.kindlr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.bueno.jose.kindlr.utilities.ResponseParser;
import org.bueno.jose.kindlr.utilities.StringEx;
import org.bueno.jose.kindlr.utilities.Toaster;

public class MainActivity extends AppCompatActivity {

    private Intent landingPageIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        setContentView(R.layout.activity_main);

        landingPageIntent = new Intent(this, HomeActivity.class);

        new Handler().postDelayed(directTraffic, 3000);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.INTENT_LOGIN_ACTIVITY:
                if (resultCode == RESULT_OK){
                    startActivity(landingPageIntent);
                }
                break;
            default:
                Toaster.makeToast(this, "I don't know why I'm here!!!");
        }

    }

    private final Runnable directTraffic = new Runnable() {
        @Override
        public void run() {

            SharedPreferences sharedPreferences = getSharedPreferences("org.bueno.jose.kindlr", MODE_PRIVATE);
            String token = sharedPreferences.getString("token", "");
            String tokenSecret = sharedPreferences.getString("tokenSecret", "");

            String userId = sharedPreferences.getString("userId", "");

            if (StringEx.isNullOrWhiteSpace(token)){
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra(Constants.AUTHENTICATED_USER_ID, userId);
                startActivityForResult(intent, Constants.INTENT_LOGIN_ACTIVITY);
            } else {
                ResponseParser.SetTokenWithSecret(token, tokenSecret);
                startActivity(landingPageIntent);
            }
        }
    };
}
