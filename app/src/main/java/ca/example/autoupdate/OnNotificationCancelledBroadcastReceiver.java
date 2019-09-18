package ca.example.autoupdate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnNotificationCancelledBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action.equals("notification_cancelled"))
        {
            App.get().setWeekly().scheduleJob();
        }
    }
}
