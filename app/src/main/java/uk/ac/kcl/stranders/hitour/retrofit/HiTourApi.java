package uk.ac.kcl.stranders.hitour.retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import uk.ac.kcl.stranders.hitour.model.Audience;
import uk.ac.kcl.stranders.hitour.model.DataAudience;
import uk.ac.kcl.stranders.hitour.model.PointData;
import uk.ac.kcl.stranders.hitour.model.Tour;
import uk.ac.kcl.stranders.hitour.model.Point;
import uk.ac.kcl.stranders.hitour.model.Data;
import uk.ac.kcl.stranders.hitour.model.TourPoints;


public interface HiTourApi {
    @GET("audiences")
    Call<List<Audience>> getAudiences();

    @GET("data")
    Call<List<Data>> getData();

    @GET("data_audiences")
    Call<List<DataAudience>> getDataAudiences();

    @GET("points")
    Call<List<Point>> getPoints();

    @GET("point_data")
    Call<List<PointData>> getPointData();

    @GET("tours")
    Call<List<Tour>> getTours();

    @GET("tour_points")
    Call<List<TourPoints>> getTourPoints();
}