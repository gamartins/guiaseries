package br.com.martinsdev.guiadeseries.controller.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by gabriel on 4/14/16.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){

            // Alarme gerado a cada 3 minutos
            long interval_three_minutes = AlarmManager.INTERVAL_FIFTEEN_MINUTES / 6;
//            long interval_three_minutes = AlarmManager.INTERVAL_HOUR;

            Intent receiverIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, receiverIntent, 0);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    interval_three_minutes, interval_three_minutes, pendingIntent);
        }
    }
}
