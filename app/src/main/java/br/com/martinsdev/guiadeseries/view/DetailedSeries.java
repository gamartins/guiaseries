package br.com.martinsdev.guiadeseries.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ExpandableListView;

import br.com.martinsdev.guiadeseries.R;
import br.com.martinsdev.guiadeseries.controller.DatabaseClientSeason;
import br.com.martinsdev.guiadeseries.controller.ServiceGenerator;
import br.com.martinsdev.guiadeseries.model.Season;
import br.com.martinsdev.guiadeseries.model.TVShow;
import br.com.martinsdev.guiadeseries.util.adapters.SeasonAdapter;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class DetailedSeries extends AppCompatActivity implements Callback {
    DatabaseClientSeason client = ServiceGenerator.createService(DatabaseClientSeason.class);
    DisplayMetrics metrics;
    int width;
    SeasonAdapter adapter;
    TVShow tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_series);

        Bundle bundle = getIntent().getExtras();
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.detailed_series_episodes_list);

        // Set the child indicator to the right
        // initialze displayMetrics
        metrics = new DisplayMetrics();
        // get the metrics of window
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        // save width of window
        width = metrics.widthPixels;
        // check version of sdk(android version)
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //For sdk version bellow 18
            listView.setIndicatorBounds(width - GetDipsFromPixel(80), width - GetDipsFromPixel(30));
        } else {
            //For sdk 18 and above
            listView.setIndicatorBoundsRelative(width - GetDipsFromPixel(80), width - GetDipsFromPixel(30));
        }

        tvShow = bundle.getParcelable("tvShow");
        tvShow.removeExtraSeason();

        setTitle(tvShow.getName());

        adapter = new SeasonAdapter(getBaseContext(), tvShow.getSeasons(), tvShow.getId());
        listView.setAdapter(adapter);

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
        adapter.notifyDataSetChanged();


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

    private int GetDipsFromPixel(int pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }
}