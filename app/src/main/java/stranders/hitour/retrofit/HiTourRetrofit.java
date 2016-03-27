package stranders.hitour.retrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import stranders.hitour.model.Tour;
import stranders.hitour.model.TourResponse;
import stranders.hitour.model.TourSession;

/**
 * A type-safe HTTP client for Android and Java that allowed us
 * to simplify the process of data fetching from the web API
 * and the creation of an interface to access fetched data.
 */
public class HiTourRetrofit {

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
     *
     * @param mCallback {@link stranders.hitour.retrofit.HiTourRetrofit.CallbackRetrofit}
     * @param passphrase The passphrase of the tour to be downloaded
     */
    public HiTourRetrofit(CallbackRetrofit mCallback, String passphrase) {
        this.mCallback = mCallback;
        this.passphrase = passphrase;

        Retrofit retrofit = new Retrofit.Builder()
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
    private void onRequestFinished() {
        mCallback.onAllRequestsFinished();
    }

    /**
     * Fetches the tour JSON from url and populates models
     * @param call Call<TourResponse>
     */
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
                onRequestFinished();
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
