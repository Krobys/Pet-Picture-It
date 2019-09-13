package com.akrivonos.app_standart_java.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.akrivonos.app_standart_java.AuthActivity;

public class AutorunBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action != null && action.equals(Intent.ACTION_BOOT_COMPLETED)){
            context.startActivity(new Intent(context, AuthActivity.class));
        }
    }
}
