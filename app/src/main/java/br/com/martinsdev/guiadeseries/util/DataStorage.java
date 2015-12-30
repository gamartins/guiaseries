package br.com.martinsdev.guiadeseries.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by gabriel on 21/12/15.
 */
public class DataStorage {
//    private String listName = "listSeriesID";
    private String listName;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    public DataStorage(Context context, String listName) {
        String sharedPreferencesNameBase = "br.com.martinsdev.guiaseries";
        String sharedPreferencesName = sharedPreferencesNameBase + "." + listName;

        this.settings = context.getSharedPreferences(sharedPreferencesName, 0);
        this.editor = settings.edit();
        this.listName = listName;
    }

    private Set<String> getSet(){
        Set<String> setSeries = settings.getStringSet(listName, new HashSet<String>());
        return  setSeries;
    }

    private void saveSet(Set<String> setSeries){
        editor.clear();
        editor.putStringSet(listName, setSeries);
        editor.apply();
    }

    public ArrayList<String> getList(){
        // Leitura dos valores armazenados em SharedPreferences
        Set<String> setSeries = getSet();

        // Convertermos o set para um list
        ArrayList<String> listSeries = new ArrayList<>();
        listSeries.addAll(setSeries);

        return listSeries;
    };

    public boolean searchItem(int serieId){
        Set<String> setSeries = getSet();
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

    public void add(int serieId){
        // Leitura dos valores armazenados em SharedPreferences
        Set<String> setSeries = getSet();

        // Conversão do valor para String
        String serieIdString = new StringBuilder().append(serieId).toString();

        // Inserir e salvar a lista
        setSeries.add(serieIdString);
        saveSet(setSeries);
    };

    public void remove(int serieId){
        // Leitura dos valores armazenados em SharedPreferences
        Set<String> setSeries = getSet();

        // Conversão do valor para String
        String serieIdString = new StringBuilder().append(serieId).toString();

        // Remover e salvar a lista
        setSeries.remove(serieIdString);
        saveSet(setSeries);
    };

}
