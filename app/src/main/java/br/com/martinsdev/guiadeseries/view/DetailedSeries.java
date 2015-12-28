package br.com.martinsdev.guiadeseries.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import br.com.martinsdev.guiadeseries.R;
import br.com.martinsdev.guiadeseries.controller.DatabaseClientSeason;
import br.com.martinsdev.guiadeseries.controller.ServiceGenerator;
import br.com.martinsdev.guiadeseries.model.Episode;
import br.com.martinsdev.guiadeseries.model.Season;
import br.com.martinsdev.guiadeseries.model.TVShow;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class DetailedSeries extends AppCompatActivity implements Callback {
    DatabaseClientSeason client = ServiceGenerator.createService(DatabaseClientSeason.class);
    TVShow tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_series);

        Bundle bundle = getIntent().getExtras();
        tvShow = bundle.getParcelable("tvShow");

        // Inserindo os episódios de um temporada em um TVShow
        for (int i = 1; i <= tvShow.getNumberOfSeasons(); i++) {

            // Requisitando a lista de episódio para uma temporada de uma serie
            Call<Season> call = client.getSeasonInfo(tvShow.getId(), i);
            call.enqueue(this);
        }

    }

    @Override
    public void onResponse(Response response, Retrofit retrofit) {
        Season season = (Season) response.body();
        tvShow.setSeason(season.getSeasonNumber(), season);

        // Verificando o conteúdo das variáveis
        /*StringBuilder sb = new StringBuilder();
        sb.append(tvShow.getName() + "\n" + season.getName() + "\n");

        List<Episode> episodeList = tvShow.getSeason(season.getSeasonNumber() - 1).getEpisodeList();
        for (Episode episode : episodeList) {
            sb.append(episode.getEpisodeNumber() + " " + episode.getName() + "\n");
        }

        String message = sb.toString();
        Log.v("Guia", message);*/


    }

    @Override
    public void onFailure(Throwable t) {
        Log.e("Error", t.getLocalizedMessage());
    }
}