package uk.ac.kcl.stranders.hitour.retrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.ac.kcl.stranders.hitour.model.Tour;
import uk.ac.kcl.stranders.hitour.model.TourResponse;
import uk.ac.kcl.stranders.hitour.model.TourSession;

public class HiTourRetrofit {

    private Retrofit retrofit;

    private HiTourApi hiTourApi;

    private Tour tour;

    private TourSession tourSession;

    private String passphrase;

    private CallbackRetrofit mCallback;



    public interface CallbackRetrofit {
        void onAllRequestsFinished();
    }

    /**
     * Sets up Retrofit.
     * @param mCallback {@link uk.ac.kcl.stranders.hitour.retrofit.HiTourRetrofit.CallbackRetrofit}
     * @param passphrase
     * The passphrase of the tour to be downloaded
     */
    public HiTourRetrofit(CallbackRetrofit mCallback, String passphrase) {
        this.mCallback = mCallback;
        this.passphrase = passphrase;

        retrofit = new Retrofit.Builder()
                .baseUrl("https://hitour.herokuapp.com/api/A7DE6825FD96CCC79E63C89B55F88/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        hiTourApi = retrofit.create(HiTourApi.class);
    }

    /**
     * Initializes requests to fetch all data from the web API.
     */
    public void fetchTour() {
        fetchTour(hiTourApi.getTourResponse(passphrase));
    }

    /**
     * Invoked when the request is successfully finished.
     */
    public void onRequestFinished() {
        mCallback.onAllRequestsFinished();
    }

    private void fetchTour(Call<TourResponse> call) {

        call.enqueue(new Callback<TourResponse>() {
            @Override
            public void onResponse(Call<TourResponse> call, Response<TourResponse> response) {
                TourResponse tourResponse = response.body();
                tour = tourResponse.getTour();
                tourSession = tourResponse.getTour_session();
                onRequestFinished();
            }

            @Override
            public void onFailure(Call<TourResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    public Tour getTour() {
        return tour;
    }

    public TourSession getTourSession() {
        return tourSession;
    }

}
