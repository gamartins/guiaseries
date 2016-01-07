package br.com.martinsdev.guiadeseries.model;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Generated("org.jsonschema2pojo")
public class Season implements Parcelable {

    @SerializedName("air_date")
    @Expose
    private String airDate;

    @SerializedName("episode_count")
    @Expose
    private Integer episodeCount;

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    @SerializedName("season_number")
    @Expose
    private Integer seasonNumber;

    @SerializedName("episodes")
    @Expose
    private List<Episode> episodeList;

    @SerializedName("name")
    @Expose
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     *     The airDate
     */
    public String getAirDate() {
        return airDate;
    }

    /**
     *
     * @param airDate
     *     The air_date
     */
    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }

    /**
     *
     * @return
     *     The episodeCount
     */
    public Integer getEpisodeCount() {
        if (episodeList == null){
            return episodeCount;
        } else {
            return episodeList.size();
        }

    }

    /**
     *
     * @param episodeCount
     *     The episode_count
     */
    public void setEpisodeCount(Integer episodeCount) {
        this.episodeCount = episodeCount;
    }

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
     *     The posterPath
     */
    public String getPosterPath() {
        return posterPath;
    }

    /**
     *
     * @param posterPath
     *     The poster_path
     */
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    /**
     *
     * @return
     *     The seasonNumber
     */
    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    /**
     *
     * @param seasonNumber
     *     The season_number
     */
    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public List<Episode> getEpisodeList() {
        return episodeList;
    }

    public void setEpisodeList(List<Episode> episodeList) {
        this.episodeList = episodeList;
    }

    public Season() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.airDate);
        dest.writeValue(this.episodeCount);
        dest.writeValue(this.id);
        dest.writeString(this.posterPath);
        dest.writeValue(this.seasonNumber);
        dest.writeTypedList(episodeList);
        dest.writeString(this.name);
    }

    protected Season(Parcel in) {
        this.airDate = in.readString();
        this.episodeCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.posterPath = in.readString();
        this.seasonNumber = (Integer) in.readValue(Integer.class.getClassLoader());
        this.episodeList = in.createTypedArrayList(Episode.CREATOR);
        this.name = in.readString();
    }

    public static final Creator<Season> CREATOR = new Creator<Season>() {
        public Season createFromParcel(Parcel source) {
            return new Season(source);
        }

        public Season[] newArray(int size) {
            return new Season[size];
        }
    };
}