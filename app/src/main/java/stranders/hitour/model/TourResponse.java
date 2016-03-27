package stranders.hitour.model;


public class TourResponse {

    private TourSession tour_session;
    private Tour tours;

    /**
     *
     * @return
     * The tour_session
     */
    public TourSession getTour_session() {
        return tour_session;
    }

    /**
     *
     * @param tour_session
     * The tour_session
     */
    public void setTour_session(TourSession tour_session) {
        this.tour_session = tour_session;
    }

    /**
     *
     * @return
     * The tour
     */
    public Tour getTour() {
        return tours;
    }

    /**
     *
     * @param tours
     * The tour
     */
    public void setTour(Tour tours) {
        this.tours = tours;
    }

}
