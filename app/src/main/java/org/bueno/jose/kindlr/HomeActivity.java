package org.bueno.jose.kindlr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.bueno.jose.kindlr.utilities.StringEx;

public class HomeActivity extends AppCompatActivity {

    private String userId;
    private String accessToken;
    private String accessTokenSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sharedPreferences = getSharedPreferences("org.bueno.jose.kindlr", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String tokenSecret = sharedPreferences.getString("tokenSecret", "");

        userId = sharedPreferences.getString("userId", "");



        if (StringEx.isNullOrWhiteSpace(token)){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(Constants.AUTHENTICATED_USER_ID, userId);
            startActivityForResult(intent, Constants.INTENT_LOGIN_ACTIVITY);
        } else {
            //set up UI
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
