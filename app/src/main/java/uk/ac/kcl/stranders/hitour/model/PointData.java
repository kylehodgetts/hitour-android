package uk.ac.kcl.stranders.hitour.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PointData implements Parcelable {

    @SerializedName("id")
    private Integer id;
    @SerializedName("point_id")
    private Integer pointId;
    @SerializedName("datum_id")
    private Integer datumId;
    @SerializedName("rank")
    private Integer rank;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

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
     * The pointId
     */
    public Integer getPointId() {
        return pointId;
    }

    /**
     *
     * @param pointId
     * The point_id
     */
    public void setPointId(Integer pointId) {
        this.pointId = pointId;
    }

    /**
     *
     * @return
     * The datumId
     */
    public Integer getDatumId() {
        return datumId;
    }

    /**
     *
     * @param datumId
     * The datum_id
     */
    public void setDatumId(Integer datumId) {
        this.datumId = datumId;
    }

    /**
     *
     * @return
     * The rank
     */
    public Integer getRank() {
        return rank;
    }

    /**
     *
     * @param rank
     * The rank
     */
    public void setRank(Integer rank) {
        this.rank = rank;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(pointId);
        dest.writeInt(datumId);
        dest.writeInt(rank);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }

    public static final Parcelable.Creator<PointData> CREATOR
            = new Parcelable.Creator<PointData>() {
        public PointData createFromParcel(Parcel in) {
            return new PointData(in);
        }

        public PointData[] newArray(int size) {
            return new PointData[size];
        }
    };

    private PointData(Parcel in) {
        id = in.readInt();
        pointId = in.readInt();
        datumId = in.readInt();
        rank = in.readInt();
        createdAt = in.readString();
        updatedAt = in.readString();
    }
}