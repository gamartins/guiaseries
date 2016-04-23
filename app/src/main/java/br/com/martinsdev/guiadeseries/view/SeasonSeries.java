package br.com.martinsdev.guiadeseries.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import br.com.martinsdev.guiadeseries.R;
import br.com.martinsdev.guiadeseries.model.TVShow;
import br.com.martinsdev.guiadeseries.util.DataStorage;
import br.com.martinsdev.guiadeseries.util.adapters.SeasonAdapter;

public class SeasonSeries extends AppCompatActivity {
    private String listName = "seriesID";
    SeasonAdapter seasonAdapter;
    TVShow tvShow;
    Intent returnIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season_series);

        Bundle bundle = getIntent().getExtras();
        tvShow = bundle.getParcelable("tvShow");
        tvShow.removeExtrasSeason();

        setTitle(tvShow.getName());

        seasonAdapter = new SeasonAdapter(this, tvShow.getSeasons(), tvShow.getId());

        ListView listView = (ListView) findViewById(R.id.list_season);
        listView.setAdapter(seasonAdapter);

        // Retornando os dados atualizados para a Activity anterior
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            int seasonId = data.getIntExtra("seasonId", 0);
            DataStorage storage = new DataStorage(this, "seasonID");

            if (resultCode == RESULT_OK) {
                storage.add(seasonId);
                seasonAdapter.notifyDataSetChanged();
            } else {
                storage.remove(seasonId);
                seasonAdapter.notifyDataSetChanged();
            }
        }
    }

}
