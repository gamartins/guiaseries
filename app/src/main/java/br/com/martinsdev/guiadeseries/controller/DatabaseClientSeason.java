package br.com.martinsdev.guiadeseries.controller;

import br.com.martinsdev.guiadeseries.model.Season;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by gabriel on 26/12/15.
 */
public interface DatabaseClientSeason {
    @GET("tv/{id}/season/{season_number}")
    Call<Season> getSeasonInfo(
        @Path("id") int id,
        @Path("season_number") int season_number
    );
}
