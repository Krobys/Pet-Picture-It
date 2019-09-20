package com.akrivonos.app_standart_java.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;

public class BatteryChangeReceiver extends BroadcastReceiver {
    private int currentBatteryStatus = 100;
        public void onReceive(Context context, Intent intent) {
            int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = -1;
            if (rawlevel >= 0 && scale > 0) {
                level = (rawlevel * 100) / scale;
            }
            if (currentBatteryStatus != level) {
                Toast.makeText(context, "Change battery lvl: " + level, Toast.LENGTH_SHORT).show();
                currentBatteryStatus = level;
            }
        }
}
