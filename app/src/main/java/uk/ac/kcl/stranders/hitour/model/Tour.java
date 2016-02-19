package uk.ac.kcl.stranders.hitour.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

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
    }
}