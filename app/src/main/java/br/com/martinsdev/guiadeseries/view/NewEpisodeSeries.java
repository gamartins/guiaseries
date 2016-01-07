package br.com.martinsdev.guiadeseries.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import br.com.martinsdev.guiadeseries.util.adapters.TvShowAdapter;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class NewEpisodeSeries extends AppCompatActivity implements Callback {
    private DatabaseClientTVShow tvClient = ServiceGenerator.createService(DatabaseClientTVShow.class);
    private DatabaseClientSeason seasonClient = ServiceGenerator.createService(DatabaseClientSeason.class);
    private ArrayList<String> listSeriesId;
    private ArrayList<TVShow> tvShowArrayList;
    private GridView gridViewSeries;
    private TvShowAdapter showAdapter;
    private String listName = "seriesID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);

        DataStorage storage = new DataStorage(this, listName);

        tvShowArrayList = new ArrayList<>();
        listSeriesId = storage.getList();

        showAdapter = new TvShowAdapter(this, tvShowArrayList, 1);
        gridViewSeries = (GridView) findViewById(R.id.followed_series);

        gridViewSeries.setAdapter(showAdapter);

        for (String serieId : listSeriesId){
            Call<TVShow> call = tvClient.getTvShow(serieId);
            call.enqueue(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_episode_series, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_serie_menu_item:
                Intent selectSeries = new Intent(this, SelectSeries.class);
                startActivity(selectSeries);
                break;
            case R.id.followed_menu_item:
                Intent followedSeries = new Intent(this, FollowedSeries.class);
                startActivity(followedSeries);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(Response response, Retrofit retrofit) {
        final TVShow tvShow = (TVShow) response.body();
        final DataStorage watchedEpisodeStorage = new DataStorage(getApplicationContext(), "serie_ID" + tvShow.getId());

        tvShow.removeExtraSeason();

        List<Season> seasonList = tvShow.getSeasons();
        Season season = seasonList.get(seasonList.size() - 1);

        Call<Season> seasonCall = seasonClient.getSeasonInfo(tvShow.getId(), season.getSeasonNumber());
        seasonCall.enqueue(new Callback<Season>() {
            @Override
            public void onResponse(Response<Season> response, Retrofit retrofit) {
                Season lastSeason = response.body();

                List<Episode> episodeList = lastSeason.getEpisodeList();
                List<Episode> episodesRetain = new ArrayList<Episode>();

                Calendar calendar = Calendar.getInstance();
                Date today = calendar.getTime();
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                for (Episode episode : episodeList) {
                    Date date = null;
                    try {
                        date = format.parse(episode.getAirDate());
                    }

                    //Caso a data seja vazia
                    catch (NullPointerException e) {
                        date = null;
                    }

                    catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if ((date != null) && today.after(date)) {
                        episodesRetain.add(episode);
                    }
                }

                int unairedEpisodes = episodeList.size() - episodesRetain.size();
                episodeList.retainAll(episodesRetain);

                // Atualizando a última temporada da serie
                lastSeason.setEpisodeList(episodeList);
                tvShow.setSeason(lastSeason.getSeasonNumber(), lastSeason);
                tvShow.setNumberOfEpisodes(tvShow.getNumberOfEpisodes() - unairedEpisodes);

                // Atualizando a quantidade de episódios
                tvShow.setUnwatchedEpisodes(tvShow.getNumberOfEpisodes() - watchedEpisodeStorage.getList().size());

                // Caso existam episódios novos exibir na lista
                if (tvShow.getUnwatchedEpisodes() != 0) {
                    tvShowArrayList.add(tvShow);
                    showAdapter.notifyDataSetChanged();
                }

                //Log.v("Guia", "A serie " + tvShow.getName() + " possui " + episodeList.size() + " episódios não exibidos");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Retrofit", t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onFailure(Throwable t) {
        Log.e("Retrofit", t.getLocalizedMessage());
    }
}