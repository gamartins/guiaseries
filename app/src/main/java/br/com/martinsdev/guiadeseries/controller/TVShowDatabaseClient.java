package br.com.martinsdev.guiadeseries.controller;

import br.com.martinsdev.guiadeseries.model.Episode;
import br.com.martinsdev.guiadeseries.model.ListPages;
import br.com.martinsdev.guiadeseries.model.TVShow;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by gabriel on 08/12/15.
 */
public interface TVShowDatabaseClient {
    @GET("tv/{id}")
    Call<TVShow> getTvShow(
        @Path(("id")) int id
    );

    @GET("tv/{id}")
    Call<Episode> getEpisode(
        @Path("id") int id
    );

    @GET("tv/popular")
    Call<ListPages> getPopularTvShows(
        @Query("page") int page
    );
}
