package uk.ac.kcl.stranders.hitour.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class DataAudience implements Parcelable {

    @SerializedName("id")
    private Integer id;
    @SerializedName("audience_id")
    private Integer audienceId;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("datum_id")
    private Integer datumId;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(audienceId);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeInt(datumId);
    }

    public static final Parcelable.Creator<DataAudience> CREATOR
            = new Parcelable.Creator<DataAudience>() {
        public DataAudience createFromParcel(Parcel in) {
            return new DataAudience(in);
        }

        public DataAudience[] newArray(int size) {
            return new DataAudience[size];
        }
    };

    private DataAudience(Parcel in) {
        id = in.readInt();
        audienceId = in.readInt();
        createdAt = in.readString();
        updatedAt = in.readString();
        datumId = in.readInt();
    }

}