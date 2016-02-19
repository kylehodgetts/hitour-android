package uk.ac.kcl.stranders.hitour.retrofit;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.ac.kcl.stranders.hitour.model.Audience;
import uk.ac.kcl.stranders.hitour.model.Data;
import uk.ac.kcl.stranders.hitour.model.DataAudience;
import uk.ac.kcl.stranders.hitour.model.DataType;
import uk.ac.kcl.stranders.hitour.model.Point;
import uk.ac.kcl.stranders.hitour.model.PointData;
import uk.ac.kcl.stranders.hitour.model.Tour;
import uk.ac.kcl.stranders.hitour.model.TourPoints;

public class HiTourRetrofit {

    private final static int NUMBER_OF_REQUESTS = 7;

    private Retrofit retrofit;

    private HiTourApi hiTourApi;

    private List<Audience> listAudience;

    private List<Data> listData;

    private List<DataAudience> listDataAudience;

    private List<Point> listPoint;

    private List<PointData> listPointData;

    private List<Tour> listTour;

    private List<TourPoints> listTourPoints;

    private ArrayList<Boolean> tasksFinished = new ArrayList<>();

    private Context mContext;

    public interface CallbackRetrofit {
        void onAllRequestsFinished();
    }

    public HiTourRetrofit(Context context) {
        this.mContext = context;

        retrofit = new Retrofit.Builder()
                .baseUrl("https://hitour.herokuapp.com/api/A7DE6825FD96CCC79E63C89B55F88/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        hiTourApi = retrofit.create(HiTourApi.class);
    }

    public void fetchAll() {
        listAudience = fetchData(hiTourApi.getAudiences());
        listData = fetchData(hiTourApi.getData());
        listDataAudience = fetchData(hiTourApi.getDataAudiences());
        listPoint = fetchData(hiTourApi.getPoints());
        listPointData = fetchData(hiTourApi.getPointData());
        listTour = fetchData(hiTourApi.getTours());
        listTourPoints = fetchData(hiTourApi.getTourPoints());
    }

    public void onRequestFinished() {
        tasksFinished.add(true);
        if(allRequestsFinished()) {
            ((CallbackRetrofit) mContext).onAllRequestsFinished();
        }
    }

    public boolean allRequestsFinished() {
        return tasksFinished.size() == NUMBER_OF_REQUESTS;
    }

    private <E> List<E> fetchData(Call<List<E>> call) {
        final List<E> list = new ArrayList<>();

        call.enqueue(new Callback<List<E>>() {

            @Override
            public void onResponse(Call<List<E>> call, Response<List<E>> response) {
                list.addAll(response.body());
                onRequestFinished();
            }

            @Override
            public void onFailure(Call<List<E>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        return list;
    }

    public List getList(DataType dataType) {
        switch(dataType) {
            case AUDIENCE: return listAudience;
            case DATA: return listData;
            case DATA_AUDIENCE: return listDataAudience;
            case POINT: return listPoint;
            case POINT_DATA: return listPointData;
            case TOUR: return listTour;
            case TOUR_POINTS: return listTourPoints;
            default: return null;
        }
    }

}
