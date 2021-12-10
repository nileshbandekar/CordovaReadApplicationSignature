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
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, getAppSignature());
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
                Log.d(TAG, "getAppSignature() called : " + getSHA1(signature.toByteArray()));
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
            digest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        digest.update(sig);
//         byte[] hashtext =  digest.digest();
        return Base64.encodeToString(digest.digest(), Base64.DEFAULT).trim();
    }

    interface Actions {
        String GET_SIGNATURE = "getSignature";
    }
}
