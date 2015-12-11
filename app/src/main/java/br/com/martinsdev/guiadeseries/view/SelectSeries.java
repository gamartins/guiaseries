package br.com.martinsdev.guiadeseries.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

import br.com.martinsdev.guiadeseries.R;
import br.com.martinsdev.guiadeseries.controller.TVShowDatabaseClient;
import br.com.martinsdev.guiadeseries.controller.ServiceGenerator;
import br.com.martinsdev.guiadeseries.model.ListPages;
import br.com.martinsdev.guiadeseries.model.Result;
import br.com.martinsdev.guiadeseries.util.EndlessScrollListener;
import br.com.martinsdev.guiadeseries.util.ResultAdapter;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SelectSeries extends AppCompatActivity implements Callback {
    TVShowDatabaseClient client = ServiceGenerator.createService(TVShowDatabaseClient.class);
    ArrayList<Result> results;
    ResultAdapter adapter;
    ListView listView;
    ListPages pages;
    int contPages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_series);
        Log.v("Load page", "Called onCreate");

        listView = (ListView) findViewById(R.id.list_series);
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
    public void onResponse(Response response, Retrofit retrofit) {
        // Log de resposta do Retrofit
        Log.v("Retrofit: ", "Response: " + response.message());
        pages = (ListPages) response.body();
        results.addAll(pages.getResults());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(Throwable t) {
        Log.v("Retroft", t.getLocalizedMessage());
    }

    public void loadTvShow(int page){
        StringBuilder sb = new StringBuilder();
        Log.v("Page",sb.append(page).toString());

        Call<ListPages> call = client.getPopularTvShows(page);
        call.enqueue(this);
    }
}
