package br.com.martinsdev.guiadeseries.util.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import br.com.martinsdev.guiadeseries.R;
import br.com.martinsdev.guiadeseries.model.TVShow;
import br.com.martinsdev.guiadeseries.util.Converter;
import br.com.martinsdev.guiadeseries.view.SeasonSeries;

/**
 * Created by gabriel on 21/12/15.
 */
public class TvShowAdapter extends ArrayAdapter<TVShow> {
    // Usado para identificar a activity (0 = FollowedSeries.java e 1 = NewEpisodeSeries.java
    private int activity_id;
    public static final int REQUEST_CODE = 1;

    public TvShowAdapter(Context context, ArrayList<TVShow> tvShows, int activity_id) {
        super(context, R.layout.activity_series_item_list, tvShows);
        this.activity_id = activity_id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TVShow tvShow = getItem(position);
        String baseURL = "http://image.tmdb.org/t/p/w154";

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.activity_series_item_list, parent, false);
        }

        ImageView poster = (ImageView) convertView.findViewById(R.id.followed_poster);
        TextView name = (TextView) convertView.findViewById(R.id.folowed_name);
        TextView watchedEpisodes = (TextView) convertView.findViewById(R.id.unwatched_episodes);

        if (activity_id == 1){
            watchedEpisodes.setVisibility(View.VISIBLE);
            watchedEpisodes.setText(Converter.intToString(tvShow.getUnwatchedEpisodes()));

            if (tvShow.getUnwatchedEpisodes() > 99) {
                watchedEpisodes.setTextSize(14);
            }
        }

        String imagePath = baseURL + tvShow.getPosterPath();

        name.setText(tvShow.getName());
        Picasso.with(getContext()).load(imagePath).resize(120, 180).centerCrop().into(poster);

        // Exibir os detalhes da serie clicada
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SeasonSeries.class);
                intent.putExtra("tvShow", tvShow);

                ((Activity) getContext()).startActivityForResult(intent, REQUEST_CODE);
            }
        });

        return convertView;
    }
}
