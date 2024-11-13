package nepheus.capacitor.androidshortcuts;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.getcapacitor.Bridge;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.List;

public class AndroidShortcuts {

    public Boolean isPinnedSupported(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
            return shortcutManager.isRequestPinShortcutSupported();
        }

        return false;
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    public void pin(PluginCall call, Bridge bridge, String id, String shortLabel, String longLabel, String urlIcon, String data)
        throws UnsupportedOperationException {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            throw new UnsupportedOperationException("Pinned shortcuts are not supported on this device");
        }

        ShortcutManager shortcutManager = bridge.getContext().getSystemService(ShortcutManager.class);

        if (!shortcutManager.isRequestPinShortcutSupported()) {
            throw new UnsupportedOperationException("Pinned shortcuts are not supported on this device");
        }

        try {
            // Scarica l'immagine dall'URL
            URL url = new URL(urlIcon);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);

            ShortcutInfo shortcut = buildShortcut(bridge, id, shortLabel, longLabel, bitmap, data);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bridge.getContext().registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        JSObject object = new JSObject();
                        object.put("status", true);
                        call.resolve(object);
                        context.unregisterReceiver(this);
                    }
                }, new IntentFilter("test_action"), Context.RECEIVER_NOT_EXPORTED);
            } else {
                bridge.getContext().registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        JSObject object = new JSObject();
                        object.put("status", true);
                        call.resolve(object);
                        context.unregisterReceiver(this);
                    }
                }, new IntentFilter("test_action"));
            }

            Intent intent = new Intent("test_action");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(bridge.getContext(), 123, intent, PendingIntent.FLAG_IMMUTABLE);
            shortcutManager.requestPinShortcut(shortcut, pendingIntent.getIntentSender());
        } catch (Exception ex) {
            Log.e("CapacitorShortcuts", "Error adding shortcut", ex);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private ShortcutInfo buildShortcut(
        Bridge bridge,
        String id,
        String shortLabel,
        String longLabel,
        Bitmap icon,
        String data
    ) throws InvalidParameterException {
        if (id.length() == 0) {
            throw new InvalidParameterException("Parameter 'Id' invalid");
        }

        if (shortLabel.length() == 0 || longLabel.length() == 0) {
            throw new InvalidParameterException("Parameter 'shortLabel' or 'longLabel' invalid");
        }

        Context context = bridge.getContext();
        ShortcutInfo.Builder builder = new ShortcutInfo.Builder(context, id);

        Intent intent = new Intent(
            Intent.EXTRA_SHORTCUT_INTENT,
            bridge.getIntentUri(),
            bridge.getContext(),
            bridge.getActivity().getClass()
        );
        intent.putExtra("data", data);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return builder
                    .setActivity(bridge.getActivity().getComponentName())
                    .setIntent(intent)
                    .setShortLabel(shortLabel)
                    .setLongLabel(longLabel)
                    .setIcon(Icon.createWithAdaptiveBitmap(icon))
                    .build();
        } else {
            return builder
                    .setActivity(bridge.getActivity().getComponentName())
                    .setIntent(intent)
                    .setShortLabel(shortLabel)
                    .setLongLabel(longLabel)
                    .setIcon(Icon.createWithBitmap(icon))
                    .build();
        }
    }

    // Ottiene l'elenco di tutti gli shortcut
    public List<ShortcutInfo> getShortcuts(Bridge bridge) throws UnsupportedOperationException {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            throw new UnsupportedOperationException("Pinned shortcuts are not supported on this device");
        }

        ShortcutManager shortcutManager = bridge.getContext().getSystemService(ShortcutManager.class);

        if (!shortcutManager.isRequestPinShortcutSupported()) {
            throw new UnsupportedOperationException("Pinned shortcuts are not supported on this device");
        }
        return shortcutManager.getPinnedShortcuts();
    }
}
