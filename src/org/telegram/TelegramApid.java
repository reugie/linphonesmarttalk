package org.telegram;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


import org.telegram.api.auth.TLAbsSentCode;
import org.telegram.api.auth.TLAuthorization;
import org.telegram.api.engine.ApiCallback;
import org.telegram.api.engine.AppInfo;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.TLAbsUpdates;
import org.telegram.api.requests.TLRequestAuthSendCode;
import org.telegram.api.requests.TLRequestAuthSignIn;
import org.telegram.api.requests.TLRequestAuthSignUp;

import java.util.Map;


/**
 * Created by PSI-REUGIE on 2/19/2016.
 */
public class TelegramApid {

    private static TelegramApi api;
    static {
        api = new TelegramApi(new MemoryApiState(true), new AppInfo(1, "deviceModel", "systemVersion", "appVersion", "langCode"), new ApiCallback() {

            @Override
            public void onUpdatesInvalidated(TelegramApi api) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onUpdate(TLAbsUpdates updates) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAuthCancelled(TelegramApi api) {
                // TODO Auto-generated method stub

            }
        });
    }
    public static String phone_hash;
    public static boolean phone_is_registered;
    public static String phone_number;
    public static TLAuthorization auth;

    public static void sendCode(String phone){
        try{
            TLAbsSentCode sentCode = api.doRpcCallNonAuth(new TLRequestAuthSendCode(phone, 0, BuildVars.APP_ID, BuildVars.APP_HASH, "en_US"));
            phone_hash = sentCode.getPhoneCodeHash();
            phone_is_registered = sentCode.getPhoneRegistered();
            phone_number = phone;
        }catch(Exception e){
            Log.e("tmessage",e.toString());
        }
    }

    public static void signUp(String code, String firstname, String lastname){
        try{
            auth = api.doRpcCallNonAuth(new TLRequestAuthSignUp(phone_number, phone_hash, code, firstname,lastname));
        }catch(Exception e){
            Log.e("tmessage",e.toString());
            Log.e("tmessage",e.toString());
        }
    }

    public static void signIn(String code){
        try{
            auth = api.doRpcCallNonAuth(new TLRequestAuthSignIn(phone_number, phone_hash, code));
        }catch(Exception e){
            Log.e("tmessage",e.toString());
            Log.e("tmessage",e.toString());
        }
    }

}
