package uk.ac.kcl.stranders.hitour.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TourSession implements Parcelable {

    @SerializedName("id")
    private Integer id;
    @SerializedName("tour_id")
    private Integer tourId;
    @SerializedName("start_date")
    private String startDate;
    @SerializedName("duration")
    private Integer duration;
    @SerializedName("passphrase")
    private String passphrase;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("name")
    private String name;


    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The tourId
     */
    public Integer getTourId() {
        return tourId;
    }

    /**
     *
     * @param tourId
     * The tour_id
     */
    public void setTourId(Integer tourId) {
        this.tourId = tourId;
    }

    /**
     *
     * @return
     * The startDate
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     *
     * @param startDate
     * The start_date
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     *
     * @return
     * The duration
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     *
     * @param duration
     * The duration
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     *
     * @return
     * The passphrase
     */
    public String getPassphrase() {
        return passphrase;
    }

    /**
     *
     * @param passphrase
     * The passphrase
     */
    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    /**
     *
     * @return
     * The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @param createdAt
     * The created_at
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     *
     * @return
     * The updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     *
     * @param updatedAt
     * The updated_at
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(tourId);
        dest.writeString(startDate);
        dest.writeInt(duration);
        dest.writeString(passphrase);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(name);
    }

    public static final Parcelable.Creator<TourSession> CREATOR
            = new Parcelable.Creator<TourSession>() {
        public TourSession createFromParcel(Parcel in) {
            return new TourSession(in);
        }

        public TourSession[] newArray(int size) {
            return new TourSession[size];
        }
    };

    private TourSession(Parcel in) {
        id = in.readInt();
        tourId = in.readInt();
        startDate = in.readString();
        duration = in.readInt();
        passphrase = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        name = in.readString();
    }

}
