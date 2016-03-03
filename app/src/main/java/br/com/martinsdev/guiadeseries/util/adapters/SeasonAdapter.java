package br.com.martinsdev.guiadeseries.util.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.martinsdev.guiadeseries.R;
import br.com.martinsdev.guiadeseries.model.Season;
import br.com.martinsdev.guiadeseries.util.DataStorage;
import br.com.martinsdev.guiadeseries.view.EpisodeSeries;

/**
 * Created by gabriel on 2/29/16.
 */
public class SeasonAdapter extends ArrayAdapter<Season> {
    private String listName = "seasonID";
    DataStorage storage;
    int tvShowId;

    public SeasonAdapter(Context context, List<Season> seasons, int tvShowId) {
        super(context, R.layout.activity_season_series_item, seasons);
        storage = new DataStorage(getContext(), listName);
        this.tvShowId = tvShowId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.activity_season_series_item, null);
        }

        final Season season = getItem(position);

        TextView seasonNumber = (TextView) convertView.findViewById(R.id.list_season_number);
        seasonNumber.append(season.getSeasonNumber().toString());

        seasonNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enviando a temporada durante a chamada da Activity
                Bundle bundle = new Bundle();
                bundle.putParcelable("season", season);
                bundle.putInt("tvShowId", tvShowId);

                Intent intent = new Intent(getContext(), EpisodeSeries.class);
                intent.putExtras(bundle);

                getContext().startActivity(intent);
            }
        });

        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.list_season_checkbox);
        if (storage.searchItem(season.getId())) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    storage.add(season.getId());
                } else {
                    storage.remove(season.getId());
                }
            }
        });

        return convertView;
    }
}
