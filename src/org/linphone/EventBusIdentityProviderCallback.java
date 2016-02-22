package org.linphone;

/**
 * Created by PSI-REUGIE on 12/29/2015.
 */
import android.app.Dialog;
import android.util.Log;
import com.auth0.api.APIClient;
import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.api.callback.BaseCallback;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.identity.IdentityProviderCallback;
import de.greenrobot.event.EventBus;

public class EventBusIdentityProviderCallback implements IdentityProviderCallback {
    private static final String TAG = "EventBusIdentityProviderCallback";

    private final EventBus bus;
    private final APIClient client;

    public EventBusIdentityProviderCallback(EventBus bus, APIClient client) {
        this.bus = bus;
        this.client = client;
    }

    @Override
    public void onFailure(Dialog dialog) {
        bus.post(new ErrorEvent(dialog));
    }

    @Override
    public void onFailure(int title, int message, Throwable throwable) {
        bus.post(new ErrorEvent(title, message, throwable));
    }

    @Override
    public void onSuccess(String serviceName, String accessToken) {
        client.socialLogin(serviceName, accessToken, null, new AuthenticationCallback() {
            @Override
            public void onSuccess(UserProfile userProfile, Token token) {
                bus.post(new AuthenticationEvent(userProfile, token));
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("CALLBACK", "auth0 error: " + throwable.getMessage());
                bus.post(new ErrorEvent(1, 2, throwable));
            }
        });
    }

    @Override
    public void onSuccess(final Token token) {
        client.fetchUserProfile(token.getIdToken(), new BaseCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile profile) {
                bus.post(new AuthenticationEvent(profile, token));
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("CALLBACK", "auth0 error: " + throwable.getMessage());
                bus.post(new ErrorEvent(1, 2, throwable));
            }
        });
    }
}

