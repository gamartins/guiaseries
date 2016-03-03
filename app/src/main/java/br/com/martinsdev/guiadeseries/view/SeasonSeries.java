package br.com.martinsdev.guiadeseries.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import br.com.martinsdev.guiadeseries.R;
import br.com.martinsdev.guiadeseries.controller.DatabaseClientSeason;
import br.com.martinsdev.guiadeseries.controller.ServiceGenerator;
import br.com.martinsdev.guiadeseries.model.Season;
import br.com.martinsdev.guiadeseries.model.TVShow;
import br.com.martinsdev.guiadeseries.util.DataStorage;
import br.com.martinsdev.guiadeseries.util.SearchSeriesDialog;
import br.com.martinsdev.guiadeseries.util.adapters.DetailedAdapter;
import br.com.martinsdev.guiadeseries.util.adapters.SeasonAdapter;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SeasonSeries extends AppCompatActivity implements Callback {
    DatabaseClientSeason client = ServiceGenerator.createService(DatabaseClientSeason.class);
    private String listName = "seriesID";
    SeasonAdapter seasonAdapter;
    TVShow tvShow;
    public static final int RESULT_DELETED = 3;
    Intent returnIntent;
    SearchSeriesDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season_series);

        Bundle bundle = getIntent().getExtras();
        tvShow = bundle.getParcelable("tvShow");
        tvShow.removeExtraSeason();

        setTitle(tvShow.getName());

        seasonAdapter = new SeasonAdapter(this, tvShow.getSeasons(), tvShow.getId());

        ListView listView = (ListView) findViewById(R.id.list_season);
        listView.setAdapter(seasonAdapter);

        // Retornando os dados atualizados para a Activity anterior
        returnIntent = new Intent();
        returnIntent.putExtra("tvShow", tvShow);
        setResult(Activity.RESULT_OK, returnIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detailed_series, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.remove_episode_menu_item:
                // Removendo a seria da lista em SharedPreferences
                DataStorage storage = new DataStorage(this, listName);
                storage.remove(tvShow.getId());

                // resultCode indicando que a serie foi deletada
                setResult(DetailedSeries.RESULT_DELETED, returnIntent);

                // Debug
                //Log.v("Guia", "Removendo a serie " + tvShow.getName() + " da lista.");

                // Finalizando a Activity
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(Response response, Retrofit retrofit) {
        Season season = (Season) response.body();
        tvShow.setSeason(season.getSeasonNumber(), season);
        seasonAdapter.notifyDataSetChanged();
        dialog.increment();

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

    public void getEpisodesFromSeason(int seasonNumber){
        Call<Season> call = client.getSeasonInfo(tvShow.getId(), seasonNumber);
        call.enqueue(this);
    }
}
