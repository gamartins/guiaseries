
package br.com.martinsdev.guiadeseries.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Generated("org.jsonschema2pojo")
public class Genre {

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

}
