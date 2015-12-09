package br.com.martinsdev.guiadeseries;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import br.com.martinsdev.guiadeseries.model.Genre;
import br.com.martinsdev.guiadeseries.model.TVShow;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SelectSeries extends AppCompatActivity implements Callback {
    final MovieDatabaseClient client = ServiceGenerator.createService(MovieDatabaseClient.class);
    TextView seriesName, seriesYear, seriesGenre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_series);

        Call<TVShow> call = client.getTvShow(1396);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Response response, Retrofit retrofit) {
        // Realizamos a deserialização do objeto
        TVShow tvShow = (TVShow) response.body();
        List<Genre> listGenre = tvShow.getGenres();
        Genre genre = listGenre.get(0);

        seriesName = (TextView) findViewById(R.id.Name);
        seriesGenre = (TextView) findViewById(R.id.Genre);
        seriesYear = (TextView) findViewById(R.id.Year);

        seriesName.setText(tvShow.getName().toString());
        seriesGenre.setText(genre.getName());
        seriesYear.setText(tvShow.getFirstAirDate());

        Log.v("Retrofit: ", "Response: " + response.message());
    }

    @Override
    public void onFailure(Throwable t) {
        Log.v("Retroft", t.getLocalizedMessage());
    }
}
