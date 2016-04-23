package br.com.martinsdev.guiadeseries.controller.alarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import br.com.martinsdev.guiadeseries.R;
import br.com.martinsdev.guiadeseries.controller.DatabaseClientSeason;
import br.com.martinsdev.guiadeseries.controller.DatabaseClientTVShow;
import br.com.martinsdev.guiadeseries.controller.ServiceGenerator;
import br.com.martinsdev.guiadeseries.model.Episode;
import br.com.martinsdev.guiadeseries.model.Season;
import br.com.martinsdev.guiadeseries.model.TVShow;
import br.com.martinsdev.guiadeseries.util.DataStorage;
import br.com.martinsdev.guiadeseries.view.SelectSeries;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class AlarmReceiver extends BroadcastReceiver implements Callback{
    private DatabaseClientTVShow tvClient = ServiceGenerator.createService(DatabaseClientTVShow.class);
    private DatabaseClientSeason seasonClient = ServiceGenerator.createService(DatabaseClientSeason.class);
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Disponibilizando context para os demais métodos
        this.context = context;

        /* Procurando as series acompanhadas */
        DataStorage storage = new DataStorage(context, "seriesID");
        ArrayList<String> listSeriesID = storage.getList();

        for (String serieID : listSeriesID) {
            Call<TVShow> tvShowCall = tvClient.getTvShow(serieID);
            tvShowCall.enqueue(this);
        }
    }

    private void showNewEpisodeNotification(Context context, TVShow tvShow){
        Intent resultIntent = new Intent(context, SelectSeries.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("Guia de Series")
                .setSmallIcon(R.drawable.logo)
                .setContentText("Hoje tem um novo episódio de " + tvShow.getName())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(tvShow.getId(), builder.build());
    }

    @Override
    public void onResponse(Response response, Retrofit retrofit) {
        final TVShow tvShow = (TVShow) response.body();

        //Log.v("Retrofit", "Verificando novos episódios de " + tvShow.getName());

        Call<Season> seasonCall = seasonClient.getSeasonInfo(tvShow.getId(), tvShow.getNumberOfSeasons());
        seasonCall.enqueue(new Callback<Season>() {
            @Override
            public void onResponse(Response<Season> response, Retrofit retrofit) {
                Season season = response.body();
                List<Episode> episodeList = season.getEpisodeList();
                String lastEpisodeAired = null;

                // Percorrendo a lista de episódios do último para o primeiro
                for (int cont = episodeList.size(); cont > 0 ; cont--) {
                    Episode episode = episodeList.get(cont - 1);
                    if (episode.isAired()){
                        lastEpisodeAired = episode.getAirDate();
                        break;
                    }
                }

                // Verificando se o último episódio exibido será transmitido hoje
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Calendar calendar = Calendar.getInstance();
                String today = dateFormat.format(calendar.getTime());

                if (lastEpisodeAired != null && (today.equals(lastEpisodeAired))){
                    showNewEpisodeNotification(context, tvShow);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Retrofit Notification", t.getLocalizedMessage());
            }
        });

    }

    @Override
    public void onFailure(Throwable t) {
        Log.e("Retrofit Notification", t.getLocalizedMessage());
    }
}
