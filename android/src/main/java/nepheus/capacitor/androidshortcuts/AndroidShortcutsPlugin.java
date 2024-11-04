package nepheus.capacitor.androidshortcuts;

import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "AndroidShortcuts")
public class AndroidShortcutsPlugin extends Plugin {

    private AndroidShortcuts implementation = new AndroidShortcuts();

    @PluginMethod
    public void isPinnedSupported(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("result", implementation.isPinnedSupported(this.getContext()));
        call.resolve(ret);
    }

    @PluginMethod
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void pin(PluginCall call) {
        String id = call.getString("id");
        String shortLabel = call.getString("shortLabel");
        String longLabel = call.getString("longLabel");
        String urlIcon = call.getString("urlIcon");
        String data = call.getString("data");
        try {
            implementation.pin(this.getBridge(), id, shortLabel, longLabel, urlIcon, data);
        } catch (Exception e) {
            call.reject(e.getMessage());
            return;
        }
        call.resolve();
    }

    /**
     * Listen for EXTRA_SHORTCUT_INTENT intents
     * @param intent
     */
    @Override
    protected void handleOnNewIntent(Intent intent) {
        super.handleOnNewIntent(intent);

        if (Intent.EXTRA_SHORTCUT_INTENT.equals(intent.getAction())) {
            JSObject ret = new JSObject();
            ret.put("data", intent.getStringExtra("data"));
            notifyListeners("shortcut", ret, true);
        }
    }
}
