package br.com.martinsdev.guiadeseries.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import br.com.martinsdev.guiadeseries.R;
import br.com.martinsdev.guiadeseries.controller.DatabaseClientTVShow;
import br.com.martinsdev.guiadeseries.controller.ServiceGenerator;
import br.com.martinsdev.guiadeseries.model.ListPages;
import br.com.martinsdev.guiadeseries.model.Result;
import br.com.martinsdev.guiadeseries.util.EndlessScrollListener;
import br.com.martinsdev.guiadeseries.util.adapters.ResultAdapter;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SelectSeries extends AppCompatActivity implements Callback {
    DatabaseClientTVShow client = ServiceGenerator.createService(DatabaseClientTVShow.class);
    ArrayList<Result> results;
    ResultAdapter adapter;
    ListView listView;
    ListPages pages;
    SearchView searchView;
    MenuItem menuItem;
    int contPages = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_series, menu);

        menuItem = menu.findItem(R.id.search_widget);
        searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                contPages = 1;
                results.clear();
                searchTvShow(query, contPages);
                listView.setOnScrollListener(new EndlessScrollListener() {
                    @Override
                    public boolean onLoadMore(int page, int totalItemsCount) {
                        searchTvShow(query, ++contPages);
                        return true;
                    }
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    resetListView();
                }
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.followed_menu_item:
                Intent intent = new Intent(this, FollowedSeries.class);
                startActivity(intent);
                break;
            case R.id.new_episode_menu_item:
                Intent newEpisodes = new Intent(this, NewEpisodeSeries.class);
                startActivity(newEpisodes);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_series);

        listView = (ListView) findViewById(R.id.selected_series_list);
        results = new ArrayList<Result>();
        adapter = new ResultAdapter(this, results);

        // Valor 1 para a primeira página a ser carregada
        loadTvShow(contPages);

        listView.setAdapter(adapter);
        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                loadTvShow(++contPages);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if ((searchView != null) && !searchView.isIconified()) {
            resetListView();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResponse(Response response, Retrofit retrofit) {
        pages = (ListPages) response.body();
        results.addAll(pages.getResults());
        Log.v("Results Array", new StringBuilder().append(results.size()).toString());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(Throwable t) {
        Log.v("Retroft", t.getLocalizedMessage());
    }

    public void loadTvShow(int page){
        Call<ListPages> call = client.getPopularTvShows(page);
        call.enqueue(this);
    }

    public void searchTvShow(String search, int cont) {
        Call<ListPages> call = client.searchTvShow(search, cont);
        call.enqueue(this);
    }

    public void resetListView(){
        results.clear();
        contPages = 1;

        // Reset SearchView
        searchView.setIconified(true);
        searchView.clearFocus();
        menuItem.collapseActionView();

        loadTvShow(contPages);
        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                loadTvShow(++contPages);
                return true;
            }
        });
    }
}