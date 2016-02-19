package uk.ac.kcl.stranders.hitour;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import uk.ac.kcl.stranders.hitour.model.Tour;

public interface HiTourApi {
    @GET("tours")
    Call<List<Tour>> getTours();
}