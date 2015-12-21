package br.com.martinsdev.guiadeseries.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.martinsdev.guiadeseries.R;
import br.com.martinsdev.guiadeseries.model.Genre;
import br.com.martinsdev.guiadeseries.model.Result;

/**
 * Created by gabriel on 09/12/15.
 */
public class ResultAdapter extends ArrayAdapter<Result>{
    private String baseURL = "http://image.tmdb.org/t/p/w154";
    private DataStorage storage;

    public ResultAdapter(Context context, ArrayList<Result> results) {
        super(context, R.layout.item_list_series, results);
        storage = new DataStorage(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Result result = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_list_series, parent, false);
        }

        TextView seriesName = (TextView) convertView.findViewById(R.id.seriesName);
        TextView seriesGenre = (TextView) convertView.findViewById(R.id.seriesGenre);
        TextView seriesYear = (TextView) convertView.findViewById(R.id.seriesYear);
        ImageView poster = (ImageView) convertView.findViewById(R.id.poster);
        final ImageView addSerieIcon = (ImageView) convertView.findViewById(R.id.add_serie);

        // Verifica se a serie esta na lista e a marca como acompanhada
        if (storage.searchItem(result.getId())) {
            result.setWatched(true);
            addSerieIcon.setVisibility(View.VISIBLE);
        }

        // Listener para setar o visibilidade e acompanhamento da serie
        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (result.isWatched() == false || storage.searchItem(result.getId())) {
                if (result.isWatched() == false) {
                    result.setWatched(true);
                    addSerieIcon.setVisibility(View.VISIBLE);

                    // Serie removida das configurações do aplicativo
                    storage.addSeries(result.getId());
                } else {
                        result.setWatched(false);
                    addSerieIcon.setVisibility(View.INVISIBLE);

                    // Serie removida das configurações do aplicativo
                    storage.removeSeries(result.getId());
                }

                // Teste do armazenamento de dados
                ArrayList<String> seriesList = storage.getListSeries();
                Log.v("Lista de series", seriesList.toString());
            }
        });

        String imagePath = baseURL + result.getPosterPath();
        Calendar calendar = Converter.convertDate(result.getFirstAirDate());
        String genreName = Genre.getNameById(result.getGenreIds().get(0));

        seriesName.setText(result.getName());
        seriesGenre.setText(genreName);
        seriesYear.setText(Integer.toString(calendar.get(Calendar.YEAR)));

        // Evita que novas ImageViews se tornem visiveis devido ao RecycleView do ArrayAdapter
        if (result.isWatched() == true){
            addSerieIcon.setVisibility(convertView.VISIBLE);
        } else {
            addSerieIcon.setVisibility(convertView.INVISIBLE);
        }

        Picasso.with(getContext())
                .load(imagePath)
                .resize(120, 180)
                .centerCrop()
                .into(poster);

        return convertView;
    }
}