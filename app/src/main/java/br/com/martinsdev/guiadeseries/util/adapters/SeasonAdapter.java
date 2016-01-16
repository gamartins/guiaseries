package br.com.martinsdev.guiadeseries.util.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.martinsdev.guiadeseries.R;
import br.com.martinsdev.guiadeseries.model.Episode;
import br.com.martinsdev.guiadeseries.model.Season;
import br.com.martinsdev.guiadeseries.util.Converter;
import br.com.martinsdev.guiadeseries.util.DataStorage;

/**
 * Created by gabriel on 28/12/15.
 */
public class SeasonAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Season> seasonList;
    private String listName;
    private DataStorage storage;

    public SeasonAdapter(Context context, List<Season> seasonList, int serieID) {
        this.context = context;
        this.seasonList = seasonList;
        this.listName = "serie_ID" + serieID;
        this.storage = new DataStorage(context, listName);
    }

    @Override
    public int getGroupCount() {
        return seasonList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int numEpisodes = getEpisodeList(groupPosition).size();
        return numEpisodes;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return getEpisodeList(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<Episode> episodeList = getEpisodeList(groupPosition);
        return episodeList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
//        return childPosition;
        long chilId = ( groupPosition + 1 ) * childPosition;
        return chilId;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.activity_detailed_series_expandable, null);
        }

        Season season = seasonList.get(groupPosition);

        TextView seasonNumber = (TextView) convertView.findViewById(R.id.detailed_series_season_number);
        seasonNumber.setText(season.getName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.activity_detailed_series_expandable_item, null);
        }

        List<Episode> episodeList = getEpisodeList(groupPosition);
        final Episode episode = episodeList.get(childPosition);

        TextView episodeNumber = (TextView) convertView.findViewById(R.id.detailed_series_episode_number);
        TextView episodeName = (TextView) convertView.findViewById(R.id.detailed_series_episode_name);
        final CheckBox watched = (CheckBox) convertView.findViewById(R.id.detailed_series_episode_checkbox);

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

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private List<Episode> getEpisodeList(int seasonPosition){
        Season season = seasonList.get(seasonPosition);
        List<Episode> episodeList = season.getEpisodeList();

        return episodeList;
    }
}
