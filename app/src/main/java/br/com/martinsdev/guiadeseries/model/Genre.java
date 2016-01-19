
package br.com.martinsdev.guiadeseries.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Genre implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("name")
    @Expose
    private String name;

    private static final Map<Integer, String> myMap = Collections.unmodifiableMap(
        new HashMap<Integer, String>() {{
            put(0, "Não encontrado");
            put(28,"Ação");
            put(12,"Aventura");
            put(16,"Animação");
            put(35,"Comédia");
            put(80,"Crime");
            put(99,"Documentário");
            put(18,"Drama");
            put(10751,"Família");
            put(14,"Fantasia");
            put(10769,"Estrangeiro");
            put(36,"História");
            put(27,"Terror");
            put(10402,"Musical");
            put(9648,"Mistério");
            put(10749,"Romance");
            put(878,"Ficção Cientifica");
            put(10770,"Filmes para TV");
            put(53,"Thriller");
            put(10752,"Guerra");
            put(37,"Western");
            put(10759,"Ação e Aventura");
            put(10762,"Infantil");
            put(10763,"Noticias");
            put(10764,"Reality");
            put(10765,"Sci-Fi e Fantasia");
            put(10766,"Novela");
            put(10767,"Entrevistas");
            put(10768,"Guerra e Política");
        }});

    /**
     * 
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param id
     *     The id of the genre
     */
    public static String getNameById(int id) {
        String genreName = myMap.get(id);
        return genreName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
    }

    public Genre() {
    }

    protected Genre(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Genre> CREATOR = new Parcelable.Creator<Genre>() {
        public Genre createFromParcel(Parcel source) {
            return new Genre(source);
        }

        public Genre[] newArray(int size) {
            return new Genre[size];
        }
    };
}
