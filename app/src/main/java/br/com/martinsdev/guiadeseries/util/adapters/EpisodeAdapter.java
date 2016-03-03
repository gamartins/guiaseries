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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.martinsdev.guiadeseries.R;
import br.com.martinsdev.guiadeseries.model.Episode;
import br.com.martinsdev.guiadeseries.model.Season;
import br.com.martinsdev.guiadeseries.util.Converter;
import br.com.martinsdev.guiadeseries.util.DataStorage;
import br.com.martinsdev.guiadeseries.view.EpisodeSeries;

/**
 * Created by gabriel on 2/29/16.
 */
public class EpisodeAdapter extends ArrayAdapter<Episode> {
    private String listName = "serie_ID";
    private DataStorage storage;
    private List<Episode> episodes;

    public EpisodeAdapter(Context context, List<Episode> episodes, int tvShowId) {
        super(context, R.layout.activity_episode_series_item, episodes);

        this.episodes = episodes;
        this.listName = "serie_ID" + tvShowId;
        this.storage = new DataStorage(context, listName);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.activity_episode_series_item, null);
        }

        // Inicio da copia
        final Episode episode = getItem(position);

        TextView episodeNumber = (TextView) convertView.findViewById(R.id.list_series_episode_number);
        TextView episodeName = (TextView) convertView.findViewById(R.id.list_series_episode_name);
        final CheckBox watched = (CheckBox) convertView.findViewById(R.id.list_series_episode_checkbox);

        if (storage.searchItem(episode.getId())) {
            watched.setChecked(true);
            episode.setWatched(true);
        }

        episodeNumber.setText(Converter.twoDigitNumber(episode.getEpisodeNumber()) + ".");

        // Inserindo o nome do episódio no formato '
        episodeName.setText(episode.getName());

        // Desabilitando a checkbox em episódios não exibidos
        if (episode.isAired()){
            watched.setEnabled(true);
        } else {
            // Exibindo a data no padrão brasileiro
            try {
                Calendar cal = Converter.stringToDate(episode.getAirDate());
                Date date = cal.getTime();
                String dateBR = new SimpleDateFormat("dd-MM-yyyy").format(date);
                episodeName.append(" em : " + dateBR);
            } catch (NullPointerException e) {
                episodeName.append(" em : " + "indisponível");
            } finally {
                watched.setEnabled(false);
            }
        }

        watched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (watched.isChecked()) {
                    storage.add(episode.getId());
                    episode.setWatched(true);
                } else {
                    storage.remove(episode.getId());
                    episode.setWatched(false);
                }
            }
        });

        // Evita que novos CheckBoxes se sejam marcados visiveis devido ao RecycleView do ArrayAdapter
        if (episode.isWatched()){
            watched.setChecked(true);
        } else {
            watched.setChecked(false);
        }

        return convertView;
    }
}
