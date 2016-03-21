package uk.ac.kcl.stranders.hitour.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Tour implements Parcelable {

    @SerializedName("id")
    private Integer id;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("name")
    private String name;
    @SerializedName("audience_id")
    private Integer audienceId;
    @SerializedName("points")
    private List<Point> points;
    @SerializedName("quiz_url")
    private String quizUrl;

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

    /**
     *
     * @return
     * The audienceId
     */
    public Integer getAudienceId() {
        return audienceId;
    }

    /**
     *
     * @param audienceId
     * The audience_id
     */
    public void setAudienceId(Integer audienceId) {
        this.audienceId = audienceId;
    }

    /**
     *
     * @return
     * The list of points
     */
    public List<Point> getPoints() {
        return points;
    }

    /**
     *
     * @param points
     * The list of points
     */
    public void setPoints(List<Point> points) {
        this.points = points;
    }

    /**
     *
     * @return
     * The quizUrl
     */
    public String getQuizUrl() {
        return quizUrl;
    }

    /**
     *
     * @param quizUrl
     * The quiz_url
     */
    public void setQuizUrl(String quizUrl) {
        this.quizUrl = quizUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(name);
        dest.writeInt(audienceId);
        dest.writeList(points);
        dest.writeString(quizUrl);
    }

    public static final Parcelable.Creator<Tour> CREATOR
            = new Parcelable.Creator<Tour>() {
        public Tour createFromParcel(Parcel in) {
            return new Tour(in);
        }

        public Tour[] newArray(int size) {
            return new Tour[size];
        }
    };

    private Tour(Parcel in) {
        id = in.readInt();
        createdAt = in.readString();
        updatedAt = in.readString();
        name = in.readString();
        audienceId = in.readInt();
        points = new ArrayList<Point>();
        in.readList(points, Point.class.getClassLoader());
        quizUrl = in.readString();
    }

    public Tour() {}

}