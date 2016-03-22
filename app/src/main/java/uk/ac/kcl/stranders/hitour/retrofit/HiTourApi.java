package uk.ac.kcl.stranders.hitour.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;
import uk.ac.kcl.stranders.hitour.model.TourResponse;

/**
 * Interface for the Retrofit API.
 */
public interface HiTourApi {
    @GET
    Call<TourResponse> getTourResponse(@Url String url);
}