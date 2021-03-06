package br.com.martinsdev.guiadeseries.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import java.util.List;

import br.com.martinsdev.guiadeseries.R;
import br.com.martinsdev.guiadeseries.controller.DatabaseClientSeason;
import br.com.martinsdev.guiadeseries.controller.ServiceGenerator;
import br.com.martinsdev.guiadeseries.model.Episode;
import br.com.martinsdev.guiadeseries.model.Season;
import br.com.martinsdev.guiadeseries.model.TVShow;
import br.com.martinsdev.guiadeseries.util.DataStorage;
import br.com.martinsdev.guiadeseries.util.SearchSeriesDialog;
import br.com.martinsdev.guiadeseries.util.adapters.DetailedAdapter;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class DetailedSeries extends AppCompatActivity implements Callback, AdapterView.OnItemLongClickListener {
    DatabaseClientSeason client = ServiceGenerator.createService(DatabaseClientSeason.class);
    private String listName = "seriesID";
    DisplayMetrics metrics;
    int width;
    DetailedAdapter adapter;
    TVShow tvShow;
    public static final int RESULT_DELETED = 3;
    Intent returnIntent;
    SearchSeriesDialog dialog;

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
        tvShow.removeExtrasSeason();

        setTitle(tvShow.getName());

        adapter = new DetailedAdapter(getBaseContext(), tvShow.getSeasons(), tvShow.getId());
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(this);

        //ProgressDialog enquanto as series são consultadas no sistema
        dialog = new SearchSeriesDialog(this, "Procurando temporadas ", tvShow.getNumberOfSeasons());
        dialog.show();

        // Inserindo os episódios de um temporada em um TVShow
        for (int i = 1; i <= tvShow.getNumberOfSeasons(); i++) {

            // Requisitando a lista de episódio para uma temporada de uma serie
            Call<Season> call = client.getSeasonInfo(tvShow.getId(), i);
            call.enqueue(this);
        }

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
        adapter.notifyDataSetChanged();
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

    private int GetDipsFromPixel(int pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final int temporada = position + 1;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Deseja marcar a temporada como assistida ?")
                .setTitle("Temporada completa")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String listName = "serie_ID" + tvShow.getId();
                        DataStorage storage = new DataStorage(getApplicationContext(), listName);

                        Season season = tvShow.getSeason(position);
                        List<Episode> episodes = season.getEpisodeList();

                        for (Episode episode : episodes){
                            if (episode.isAired()) {
                                episode.setWatched(true);
                                storage.add(episode.getId());
                            }
                        }

                        season.setEpisodeList(episodes);
                        tvShow.setSeason(temporada, season);

                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();

        return true;
    }
}