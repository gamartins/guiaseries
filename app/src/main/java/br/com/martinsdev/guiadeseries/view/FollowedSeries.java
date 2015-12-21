package br.com.martinsdev.guiadeseries.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import java.util.ArrayList;

import br.com.martinsdev.guiadeseries.R;
import br.com.martinsdev.guiadeseries.controller.ServiceGenerator;
import br.com.martinsdev.guiadeseries.controller.TVShowDatabaseClient;
import br.com.martinsdev.guiadeseries.model.TVShow;
import br.com.martinsdev.guiadeseries.util.DataStorage;
import br.com.martinsdev.guiadeseries.util.adapters.TvShowAdapter;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class FollowedSeries extends AppCompatActivity implements Callback{
    private TVShowDatabaseClient client = ServiceGenerator.createService(TVShowDatabaseClient.class);
    private ArrayList<String> listSeriesId;
    private ArrayList<TVShow> tvShowArrayList;
    private GridView gridViewSeries;
    private DataStorage storage;
    private TvShowAdapter showAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followed_series);

        storage = new DataStorage(this);
        listSeriesId = storage.getListSeries();
        tvShowArrayList = new ArrayList<TVShow>();
        showAdapter = new TvShowAdapter(this, tvShowArrayList);

        gridViewSeries = (GridView) findViewById(R.id.followed_series);
        gridViewSeries.setAdapter(showAdapter);

        for (String serieId : listSeriesId){
            Call<TVShow> call = client.getTvShow(serieId);
            call.enqueue(this);
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
            case R.id.search_button:
                Intent intent = new Intent(this, SelectSeries.class);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(Response response, Retrofit retrofit) {
        TVShow tvShow = (TVShow) response.body();
        tvShowArrayList.add(tvShow);
        showAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(Throwable t) {

    }
}