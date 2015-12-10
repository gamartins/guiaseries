package br.com.martinsdev.guiadeseries.util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.martinsdev.guiadeseries.R;
import br.com.martinsdev.guiadeseries.model.Genre;
import br.com.martinsdev.guiadeseries.model.Result;

/**
 * Created by gabriel on 09/12/15.
 */
public class ResultAdapter extends ArrayAdapter<Result> {
    String baseURL = "http://image.tmdb.org/t/p/w154";

    public ResultAdapter(Context context, ArrayList<Result> results) {
        super(context, R.layout.item_list_series, results);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Result result = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_list_series, parent, false);
        }

        TextView seriesName = (TextView) convertView.findViewById(R.id.seriesName);
        TextView seriesGenre = (TextView) convertView.findViewById(R.id.seriesGenre);
        TextView seriesYear = (TextView) convertView.findViewById(R.id.seriesYear);
        ImageView poster = (ImageView) convertView.findViewById(R.id.poster);

        String imagePath = baseURL + result.getPosterPath();
        Calendar calendar = Converter.convertDate(result.getFirstAirDate());
        String genreName = Genre.getNameById(result.getGenreIds().get(0));

        seriesName.setText(result.getName());
        seriesGenre.setText(genreName);
        seriesYear.setText(Integer.toString(calendar.get(Calendar.YEAR)));

        Picasso.with(getContext())
                .load(imagePath)
                .resize(120, 180)
                .centerCrop()
                .into(poster);

        return convertView;
    }
}
