package nepheus.capacitor.androidshortcuts;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

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
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public void pin(PluginCall call) {
        String id = call.getString("id");
        String shortLabel = call.getString("shortLabel");
        String longLabel = call.getString("longLabel");
        String urlIcon = call.getString("urlIcon");
        String data = call.getString("data");

        //Controllare se è già presente il pin
        boolean isExist = false;
        List<ShortcutInfo> elenco = implementation.getShortcuts(this.getBridge());
        for (int i = 0; i < elenco.size(); i++) {
            ShortcutInfo item = elenco.get(i);
            if (item.getId().equals(id)) {
                isExist = true;
                break;
            }
        }
        try {
            if(!isExist){
                implementation.pin(this.getBridge(), id, shortLabel, longLabel, urlIcon, data);
            }else{
                call.reject("Shortcut Già Presente");
            }
        } catch (Exception e) {
            call.reject(e.getMessage());
            return;
        }
        call.resolve();
    }

    @PluginMethod
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public void getShortCuts(PluginCall call) {
        try {
            Gson gson = new Gson();
            List<ShortcutInfo> elenco = implementation.getShortcuts(this.getBridge());
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < elenco.size(); i++) {
                ShortcutInfo item = elenco.get(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", item.getId());
                jsonObject.put("shortLabel", item.getShortLabel());
                jsonObject.put("longLabel", item.getLongLabel());
                jsonArray.put(i, jsonObject);
            }
            Log.d("ShortCut", gson.toJson(jsonArray));
            JSObject ret = new JSObject();
            ret.put("result", jsonArray);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }

    @PluginMethod
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public void removeShortCut(PluginCall call) {
        String shortcutId = call.getString("shortcutId");
        try {
            implementation.removeShortcut(this.getBridge(), shortcutId);
            call.resolve();
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
