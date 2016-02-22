package org.linphone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.auth0.api.APIClient;
import com.auth0.core.Auth0;
import com.auth0.core.Strategies;
import com.auth0.core.UserProfile;
import com.auth0.facebook.FacebookIdentityProvider;
import com.auth0.identity.IdentityProvider;


import org.telegram.TelegramApid;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;



/**
 * Created by PSI-REUGIE on 12/28/2015.
 */
public class Auth0Activity extends Activity {

    /*private LocalBroadcastManager broadcastManager;

    private BroadcastReceiver authenticationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Intent newIntent = new Intent(Auth0Activity.this, LinphoneLauncherActivity.class);
            newIntent.putExtras(intent);
            startActivity(newIntent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_main);
        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Auth0Activity.this, LockActivity.class));
            }
        });
        broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(authenticationReceiver, new IntentFilter(AUTHENTICATION_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        broadcastManager.unregisterReceiver(authenticationReceiver);
    }*/

    private APIClient client;
    private EventBus eventBus;
    private FacebookIdentityProvider facebookProvider;
    private IdentityProvider identity;
    private Button signInButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_main);
        Auth0 auth0 = new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain_name));

        client = auth0.newAPIClient();

        //String t = new org.linphone.custom.StringHelper().getVmin("t");
        //Log.d("t",t);
        eventBus = new EventBus();
        final EventBusIdentityProviderCallback callback = new EventBusIdentityProviderCallback(eventBus, client);
        Collection<String> permissions = Arrays.asList("public_profile", "email");
        facebookProvider = new FacebookIdentityProvider(this, permissions);
        facebookProvider.setCallback(callback);

        signInButton = (Button) findViewById(R.id.sign_in_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInButton.setText("Signing in...");
                signInButton.setEnabled(false);
                identity = facebookProvider;
                identity.start(Auth0Activity.this, Strategies.Facebook.getName());
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.register(this);
    }

    @Override
    protected void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.v("Auth0", "Received new Intent with URI " + intent.getData());
        if (identity != null) {
            identity.authorize(this, IdentityProvider.WEBVIEW_AUTH_REQUEST_CODE, RESULT_OK, intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (identity != null) {
            identity.authorize(this, requestCode, resultCode, data);
        }
    }

    public void onEvent(ErrorEvent event) {
        /*if (progressDialog != null) {
            progressDialog.dismiss();
        }*/
        signInButton.setText("Log in with Facebook");  //set the text of button to original state

        signInButton.setEnabled(true);
        event.showDialog(this);
        startActivity(new Intent(this, LinphoneLauncherActivity.class));
    }

    public void onEvent(AuthenticationEvent event) {
        UserProfile profile = event.getProfile();
        Log.i("Auth0", "LOGGED IN!");
        //authenticateInServer(profile); //call authentication endpoint on Cilantro API

        //Log all extra info data
        Log.d("Auth0", "profile=" + profile.toString());
        if (profile.getExtraInfo().size() > 0) {
            Iterator iterator = profile.getExtraInfo().keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                Object value = profile.getExtraInfo().get(key);
                Log.d("Auth0", "extraInfo[" + key + "] : " + value);
            }
        }

        startActivity(new Intent(this, LinphoneLauncherActivity.class));
        //String h = new StringHelper().generatePassword(getFacebookIds(profile.getExtraInfo().get("user_metadata").toString()));

        /*Intent intent = new Intent(this, LinphoneLauncherActivity.class);
        Bundle b = new Bundle();
        b.putString("userId", getFacebookIds(profile.getExtraInfo().get("link").toString())); //Your id
        b.putString("vmin", getFacebookIds(profile.getExtraInfo().get("user_metadata").toString()));
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);*/

        finish();
    }

    private String getFacebookIds(String value){
        String regpattern = "(\\d{10,20})";
        Pattern localPattern = Pattern.compile(regpattern);
        Matcher match = localPattern.matcher(value);
        while(match.find()){
            return match.group();
        }
        return value;
    }


}
