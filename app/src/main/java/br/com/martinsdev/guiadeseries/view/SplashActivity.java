package br.com.martinsdev.guiadeseries.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import br.com.martinsdev.guiadeseries.controller.DatabaseClientTVShow;
import br.com.martinsdev.guiadeseries.controller.ServiceGenerator;
import br.com.martinsdev.guiadeseries.controller.alarm.AlarmReceiver;
import br.com.martinsdev.guiadeseries.controller.alarm.BootReceiver;
import br.com.martinsdev.guiadeseries.model.ListPages;
import br.com.martinsdev.guiadeseries.util.DataStorage;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SplashActivity extends AppCompatActivity implements Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verificando conexão a internet
        DatabaseClientTVShow client = ServiceGenerator.createService(DatabaseClientTVShow.class);
        Call<ListPages> call = client.getPopularTvShows(1);
        call.enqueue(this);

        // Iniciando o alarme
        createAlarm();
    }

    private void createAlarm(){
        // Configurando para a cada minuto -> debug
//        long interval_three_minutes = AlarmManager.INTERVAL_FIFTEEN_MINUTES / 15;
        long interval_three_minutes = AlarmManager.INTERVAL_HOUR;

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                interval_three_minutes, interval_three_minutes, pendingIntent);

        // Ativando o alarme durante o boot
        ComponentName receiver = new ComponentName(getApplicationContext(), BootReceiver.class);
        PackageManager manager = getApplicationContext().getPackageManager();

        manager.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onResponse(Response response, Retrofit retrofit) {
        String listName = "seriesID";
        DataStorage storage = new DataStorage(this, listName);

        // Verificando se o usuário já selecionou alguma serie.
        Intent intent;
        if (storage.getList().isEmpty()) {
            intent = new Intent(this, SelectSeries.class);
        } else {
            intent = new Intent(this, NewEpisodeSeries.class);
        }

        startActivity(intent);
        finish();
    }

    @Override
    public void onFailure(Throwable t) {
        String errorMessage = "O dispositivo não possui uma conexão à internet ativa.";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(errorMessage).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
