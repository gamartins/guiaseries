package br.com.martinsdev.guiadeseries.util.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import br.com.martinsdev.guiadeseries.R;
import br.com.martinsdev.guiadeseries.controller.DatabaseClientSeason;
import br.com.martinsdev.guiadeseries.controller.ServiceGenerator;
import br.com.martinsdev.guiadeseries.model.Episode;
import br.com.martinsdev.guiadeseries.model.Season;
import br.com.martinsdev.guiadeseries.util.DataStorage;
import br.com.martinsdev.guiadeseries.view.EpisodeSeries;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by gabriel on 2/29/16.
 */
public class SeasonAdapter extends ArrayAdapter<Season> {
    private String listName = "seasonID";
    static final int REQUEST_CODE = 1;
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
        seasonNumber.setText("Temporada " + season.getSeasonNumber().toString());
        seasonNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enviando a temporada durante a chamada da Activity
                Bundle bundle = new Bundle();
                bundle.putParcelable("season", season);
                bundle.putInt("tvShowId", tvShowId);

                Intent intent = new Intent(getContext(), EpisodeSeries.class);
                intent.putExtras(bundle);

                ((Activity) getContext()).startActivityForResult(intent, REQUEST_CODE);
            }
        });

        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.list_season_checkbox);

        if (storage.searchItem(season.getId())) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        // AlertDialog quando o usuário deseja marcar todos os itens de uma temporada
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Deseja marcar a temporada como completa ?")
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkBox.setChecked(false);
                    }
                })
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DatabaseClientSeason client = ServiceGenerator.createService(DatabaseClientSeason.class);
                        Call<Season> call = client.getSeasonInfo(tvShowId, season.getSeasonNumber());

                        call.enqueue(new Callback<Season>() {
                            @Override
                            public void onResponse(Response<Season> response, Retrofit retrofit) {
                                Season tempSeason = response.body();

                                List<Episode> episodeList = tempSeason.getEpisodeList();
                                for (Episode episode: episodeList) {
                                    // Inserir episódio na lista
                                    DataStorage episodeStorage;
                                    episodeStorage = new DataStorage(getContext(), "serie_ID" + tvShowId);

                                    // Inserindo os episódios que não estão na lista
                                    if (!episodeStorage.searchItem(episode.getId()) && episode.isAired()){
                                        episodeStorage.add(episode.getId());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.e("Error", t.getLocalizedMessage());
                            }
                        });
                    }
                });
        final AlertDialog completSeasonDialog = builder.create();

        // AlertDialog quando o usuário deseja desmarcar todos os itens de uma temporada
        builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Deseja marcar a temporada como incompleta ?")
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkBox.setChecked(true);
                    }
                })
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseClientSeason client = ServiceGenerator.createService(DatabaseClientSeason.class);
                        Call<Season> call = client.getSeasonInfo(tvShowId, season.getSeasonNumber());

                        call.enqueue(new Callback<Season>() {
                            @Override
                            public void onResponse(Response<Season> response, Retrofit retrofit) {
                                Season tempSeason = response.body();

                                List<Episode> episodeList = tempSeason.getEpisodeList();
                                for (Episode episode: episodeList) {
                                    // Inserir episódio na lista
                                    DataStorage episodeStorage;
                                    episodeStorage = new DataStorage(getContext(), "serie_ID" + tvShowId);

                                    // Inserindo os episódios que não estão na lista
                                    if (episodeStorage.searchItem(episode.getId())){
                                        episodeStorage.remove(episode.getId());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.e("Error", t.getLocalizedMessage());
                            }
                        });
                    }
                });
        final AlertDialog incompleteSeasonDialog = builder.create();

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    completSeasonDialog.show();
                } else {
                    incompleteSeasonDialog.show();
                }
            }
        });

        return convertView;
    }

}
