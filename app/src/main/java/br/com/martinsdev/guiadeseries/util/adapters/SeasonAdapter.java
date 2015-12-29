package br.com.martinsdev.guiadeseries.util.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.martinsdev.guiadeseries.R;
import br.com.martinsdev.guiadeseries.model.Episode;
import br.com.martinsdev.guiadeseries.model.Season;
import br.com.martinsdev.guiadeseries.util.Converter;

/**
 * Created by gabriel on 28/12/15.
 */
public class SeasonAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Season> seasonList;

    public SeasonAdapter(Context context, List<Season> seasonList) {
        this.context = context;
        this.seasonList = seasonList;
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
        return childPosition;
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
        Episode episode = episodeList.get(childPosition);

        TextView episodeNumber = (TextView) convertView.findViewById(R.id.detailed_series_episode_number);
        TextView episodeName = (TextView) convertView.findViewById(R.id.detailed_series_episode_name);

        episodeNumber.setText(Converter.twoDigitNumber(episode.getEpisodeNumber()) + ".");
        episodeName.setText(episode.getName());

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
