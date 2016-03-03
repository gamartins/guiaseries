package br.com.martinsdev.guiadeseries.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import br.com.martinsdev.guiadeseries.R;
import br.com.martinsdev.guiadeseries.controller.DatabaseClientSeason;
import br.com.martinsdev.guiadeseries.controller.ServiceGenerator;
import br.com.martinsdev.guiadeseries.model.Season;
import br.com.martinsdev.guiadeseries.util.adapters.EpisodeAdapter;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class EpisodeSeries extends AppCompatActivity implements Callback {
    DatabaseClientSeason client = ServiceGenerator.createService(DatabaseClientSeason.class);
    EpisodeAdapter adapter;
    Season season;
    int tvShowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_series);

        Bundle bundle = getIntent().getExtras();
        season = bundle.getParcelable("season");
        tvShowId = bundle.getInt("tvShowId");

        // Realizando a consulta com os episódios
        Call<Season> call = client.getSeasonInfo(tvShowId, season.getSeasonNumber());
        call.enqueue(this);

    }

    @Override
    public void onResponse(Response response, Retrofit retrofit) {
        Season season = (Season) response.body();
        adapter = new EpisodeAdapter(this, season.getEpisodeList(), tvShowId);

        ListView listView = (ListView) findViewById(R.id.episode_series_listview);
        listView.setAdapter(adapter);
    }

    @Override
    public void onFailure(Throwable t) {
        Log.e("Error", t.getLocalizedMessage());
    }
}
