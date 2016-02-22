package org.linphone.custom;

/**
 * Created by PSI-REUGIE on 1/9/2016.
 */
public class StringHelper {
    static{
        System.loadLibrary("twolib-second");
    }

    public native String getVmin(String vmin);

    public String generatePassword(String vmin){
        return getVmin(vmin);
    }

}
