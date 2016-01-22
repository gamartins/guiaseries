package br.com.martinsdev.guiadeseries.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

import br.com.martinsdev.guiadeseries.R;
import br.com.martinsdev.guiadeseries.controller.DatabaseClientTVShow;
import br.com.martinsdev.guiadeseries.controller.ServiceGenerator;
import br.com.martinsdev.guiadeseries.model.TVShow;
import br.com.martinsdev.guiadeseries.util.Converter;
import br.com.martinsdev.guiadeseries.util.DataStorage;
import br.com.martinsdev.guiadeseries.util.SearchSeriesDialog;
import br.com.martinsdev.guiadeseries.util.adapters.TvShowAdapter;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class FollowedSeries extends AppCompatActivity implements Callback{
    private DatabaseClientTVShow client = ServiceGenerator.createService(DatabaseClientTVShow.class);
    private ArrayList<String> listSeriesId;
    private ArrayList<TVShow> tvShowArrayList;
    private GridView gridViewSeries;
    private DataStorage storage;
    private TvShowAdapter showAdapter;
    private String listName = "seriesID";
    private SearchSeriesDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);

        storage = new DataStorage(this, listName);
        listSeriesId = storage.getList();
        tvShowArrayList = new ArrayList<>();
        showAdapter = new TvShowAdapter(this, tvShowArrayList, 0);

        gridViewSeries = (GridView) findViewById(R.id.followed_series);
        gridViewSeries.setAdapter(showAdapter);

        //ProgressDialog enquanto as series são consultadas no sistema
        dialog = new SearchSeriesDialog(this, "Procurando temporadas ", listSeriesId.size());
        dialog.show();

        if (listSeriesId.isEmpty()) {
            dialog.dismiss();
        } else {
            for (String serieId : listSeriesId){
                Call<TVShow> call = client.getTvShow(serieId);
                call.enqueue(this);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_followed_series, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_serie_menu_item:
                Intent selectSeries = new Intent(this, SelectSeries.class);
                startActivity(selectSeries);
                break;
            case R.id.new_episode_menu_item:
                Intent newEpisodes = new Intent(this, NewEpisodeSeries.class);
                startActivity(newEpisodes);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(Response response, Retrofit retrofit) {
        TVShow tvShow = (TVShow) response.body();
        tvShowArrayList.add(tvShow);
        showAdapter.notifyDataSetChanged();
        dialog.increment();
    }

    @Override
    public void onFailure(Throwable t) {
        Log.e("Retrofit", t.getLocalizedMessage());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TvShowAdapter.REQUEST_CODE) {
            listSeriesId = storage.getList();
            for (Iterator<TVShow> iterator = tvShowArrayList.iterator(); iterator.hasNext();){
                TVShow show = iterator.next();

                if(!listSeriesId.contains(Converter.intToString(show.getId()))){
                    iterator.remove();
                    Toast.makeText(this, "Show " + show.getName() + " removed.", Toast.LENGTH_LONG).show();
                }
            }

            showAdapter.notifyDataSetChanged();
        }
    }
}
