package br.com.martinsdev.guiadeseries.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by gabriel on 21/12/15.
 */
public class DataStorage {
    private String listName = "listSeriesID";
    private static final String LIST_SERIES = "br.com.martinsdev.guiaseries";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    public DataStorage(Context context) {
        this.settings = context.getSharedPreferences(LIST_SERIES, 0);
        this.editor = settings.edit();
    }

    private Set<String> getSetSeries(){
        Set<String> setSeries = settings.getStringSet(listName, new HashSet<String>());
        return  setSeries;
    }

    private void saveSet(Set<String> setSeries){
        editor.clear();
        editor.putStringSet(listName, setSeries);
        editor.apply();
    }

    public ArrayList<String> getListSeries(){
        // Leitura dos valores armazenados em SharedPreferences
        Set<String> setSeries = getSetSeries();

        // Convertermos o set para um list
        ArrayList<String> listSeries = new ArrayList<>();
        listSeries.addAll(setSeries);

        return listSeries;
    };

    public boolean searchItem(int serieId){
        Set<String> setSeries = getSetSeries();
        boolean exists;

        // Conversão do valor para String
        String serieIdString = new StringBuilder().append(serieId).toString();

        if (setSeries.contains(serieIdString)) {
            exists = true;
        } else {
            exists = false;
        }

        return exists;
    }

    public void addSeries(int serieId){
        // Leitura dos valores armazenados em SharedPreferences
        Set<String> setSeries = getSetSeries();

        // Conversão do valor para String
        String serieIdString = new StringBuilder().append(serieId).toString();

        // Inserir e salvar a lista
        setSeries.add(serieIdString);
        saveSet(setSeries);
    };

    public void removeSeries(int serieId){
        // Leitura dos valores armazenados em SharedPreferences
        Set<String> setSeries = getSetSeries();

        // Conversão do valor para String
        String serieIdString = new StringBuilder().append(serieId).toString();

        // Remover e salvar a lista
        setSeries.remove(serieIdString);
        saveSet(setSeries);
    };


}
