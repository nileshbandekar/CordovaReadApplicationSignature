package cordova.plugin.appSignature;

import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.util.Base64;

import java.util.zip.*;
import java.io.IOException;

/**
 * This class echoes a string called from JavaScript.
 */
public class ReadAppSignature extends CordovaPlugin {
    private final String TAG = "ReadAppSignature";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals(Actions.GET_SIGNATURE)) {
            this.getAppSignature(callbackContext);
            return true;
        }
        return false;
    }

    private void getAppSignature(CallbackContext callbackContext) {
        String result = getAppSignature() +"|" + getCrc();
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, result);
        callbackContext.sendPluginResult(pluginResult);
    }

    @SuppressLint("PackageManagerGetSignatures")
    public String getAppSignature() {

        PackageInfo packageInfo;
        try {
            packageInfo = cordova.getActivity().getPackageManager().getPackageInfo(
                    cordova.getActivity().getPackageName(), PackageManager.GET_SIGNATURES);

            //note sample just checks the first signature
            Signature[] signatures = packageInfo.signatures;
            if (signatures.length > 0) {
                Signature signature = signatures[0];
                Log.i(TAG, "getAppSignature() called : " + getSHA1(signature.toByteArray()));
                return getSHA1(signature.toByteArray());
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    //computed the sha1 hash of the signature
    public static String getSHA1(byte[] sig) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        digest.update(sig);
//         byte[] hashtext =  digest.digest();
        return Base64.encodeToString(digest.digest(), Base64.DEFAULT).trim();
    }

    private String getCrc() {
        // boolean modified = false;
        try{
             // required dex crc value stored as a text string.
             // it could be any invisible layout element
             //long dexCrc = Long.parseLong(Main.MyContext.getString(R.string.dex_crc));

             ZipFile zf = new ZipFile(cordova.getActivity().getApplication().getPackageCodePath());
             ZipEntry ze = zf.getEntry("classes.dex");

             Log.i(TAG, "CODE_CRC1 " + ze.getCrc());

             return Long.toString(ze.getCrc());
        } catch (IOException e) {
             e.printStackTrace();
        }
        return "";
    }


    interface Actions {
        String GET_SIGNATURE = "getSignature";
    }
}
