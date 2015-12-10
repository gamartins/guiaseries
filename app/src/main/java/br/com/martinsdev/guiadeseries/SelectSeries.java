package br.com.martinsdev.guiadeseries;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import java.util.ArrayList;
import br.com.martinsdev.guiadeseries.controller.MovieDatabaseClient;
import br.com.martinsdev.guiadeseries.controller.ServiceGenerator;
import br.com.martinsdev.guiadeseries.model.Genre;
import br.com.martinsdev.guiadeseries.model.ListPages;
import br.com.martinsdev.guiadeseries.model.Result;
import br.com.martinsdev.guiadeseries.util.ResultAdapter;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SelectSeries extends AppCompatActivity implements Callback {
    MovieDatabaseClient client = ServiceGenerator.createService(MovieDatabaseClient.class);
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_series);

        Call<ListPages> call = client.getPopularTvShows();
        call.enqueue(this);
    }

    @Override
    public void onResponse(Response response, Retrofit retrofit) {
        // Log de resposta do Retrofit
        Log.v("Retrofit: ", "Response: " + response.message());

        ListPages pages = (ListPages) response.body();
        ArrayList<Result> results = pages.getResults();

        ResultAdapter adapter = new ResultAdapter(this, results);

        listView = (ListView) findViewById(R.id.list_series);
        listView.setAdapter(adapter);
    }

    @Override
    public void onFailure(Throwable t) {
        Log.v("Retroft", t.getLocalizedMessage());
    }
}
