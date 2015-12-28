package br.com.martinsdev.guiadeseries.util.adapters;

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
import br.com.martinsdev.guiadeseries.view.DetailedSeries;

/**
 * Created by gabriel on 21/12/15.
 */
public class TvShowAdapter extends ArrayAdapter<TVShow> {
    public TvShowAdapter(Context context, ArrayList<TVShow> tvShows) {
        super(context, R.layout.activity_followed_series_item_list, tvShows);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TVShow tvShow = getItem(position);
        String baseURL = "http://image.tmdb.org/t/p/w154";

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.activity_followed_series_item_list, parent, false);
        }

        ImageView poster = (ImageView) convertView.findViewById(R.id.followed_poster);
        TextView textView = (TextView) convertView.findViewById(R.id.folowed_name);

        String imagePath = baseURL + tvShow.getPosterPath();

        textView.setText(tvShow.getName());
        Picasso.with(getContext()).load(imagePath).resize(120, 180).centerCrop().into(poster);

        // Exibir os detalhes da serie clicada
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DetailedSeries.class);
                intent.putExtra("tvShow", tvShow);
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
