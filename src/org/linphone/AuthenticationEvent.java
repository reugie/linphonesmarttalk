package org.linphone;

/**
 * Created by PSI-REUGIE on 12/29/2015.
 */

import com.auth0.core.Token;
import com.auth0.core.UserProfile;

public class AuthenticationEvent {

    private final UserProfile profile;
    private final Token token;

    public AuthenticationEvent(UserProfile profile, Token token) {
        this.profile = profile;
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public UserProfile getProfile() {
        return profile;
    }
}
