package br.com.martinsdev.guiadeseries.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import br.com.martinsdev.guiadeseries.R;
import br.com.martinsdev.guiadeseries.controller.DatabaseClientSeason;
import br.com.martinsdev.guiadeseries.controller.DatabaseClientTVShow;
import br.com.martinsdev.guiadeseries.controller.ServiceGenerator;
import br.com.martinsdev.guiadeseries.model.Episode;
import br.com.martinsdev.guiadeseries.model.Season;
import br.com.martinsdev.guiadeseries.model.TVShow;
import br.com.martinsdev.guiadeseries.util.Converter;
import br.com.martinsdev.guiadeseries.util.DataStorage;
import br.com.martinsdev.guiadeseries.util.SearchSeriesDialog;
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
    private SearchSeriesDialog dialog;

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

        //ProgressDialog enquanto as series são consultadas no sistema
        dialog = new SearchSeriesDialog(this, "Procurando temporadas ", listSeriesId.size());
        dialog.show();

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

                /*Fix: Bug no atributo TVShow.number_of_episodes enviado pela API, o número de episódios
                na serie é menor que a quantidade de episódios na temporada, sendo que a temporada esta
                correta.*/
                if (tvShow.getNumberOfEpisodes() < lastSeason.getEpisodeList().size()){
                    tvShow.setNumberOfEpisodes(lastSeason.getEpisodeList().size());
                }

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

                // Encerra a ProgressBar caso todas as series tinham sido carregadas
                dialog.increment();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TvShowAdapter.REQUEST_CODE) {
            TVShow show = data.getExtras().getParcelable("tvShow");

            // Caso a serie tenha sido deletada
            if (resultCode == DetailedSeries.RESULT_DELETED) {
                DataStorage storage = new DataStorage(this,listName);
                listSeriesId = storage.getList();

                for (Iterator<TVShow> iterator = tvShowArrayList.iterator(); iterator.hasNext();){
                    TVShow tempShow = iterator.next();

                    if(!listSeriesId.contains(Converter.intToString(tempShow.getId()))){
                        iterator.remove();
                    }
                }

                Toast.makeText(this, "Show " + show.getName() + " removed.", Toast.LENGTH_LONG).show();
                showAdapter.notifyDataSetChanged();

            } else { // Caso a serie tenha sido modificada

                int indexOf = -1;
                for (TVShow tempShow : tvShowArrayList) {
                    if (show.getId().equals(tempShow.getId())) {
                        indexOf = tvShowArrayList.indexOf(tempShow);
                        DataStorage watchedEpisodeStorage = new DataStorage(
                                getApplicationContext(), "serie_ID" + show.getId());
                        show.setUnwatchedEpisodes(
                                show.getUnwatchedEpisodes() - watchedEpisodeStorage.getList().size());
                    }
                }

                tvShowArrayList.set(indexOf, show);
                showAdapter.notifyDataSetChanged();

            }
        }
    }
}