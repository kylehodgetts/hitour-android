package stranders.hitour.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Data implements Parcelable {

    @SerializedName("id")
    private Integer id;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("url")
    private String url;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("audiences")
    private List<Audience> audiences;
    @SerializedName("rank")
    private Integer rank;

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
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(String url) {
        this.url = url;
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
     * The list of audiences
     */
    public List<Audience> getAudiences() {
        return audiences;
    }

    /**
     *
     * @param audiences
     * The list of audiences
     */
    public void setAudiences(List<Audience> audiences) {
        this.audiences = audiences;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeList(audiences);
        dest.writeInt(rank);
    }

    public static final Parcelable.Creator<Data> CREATOR
            = new Parcelable.Creator<Data>() {
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        public Data[] newArray(int size) {
            return new Data[size];
        }
    };

    private Data(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        url = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        audiences = new ArrayList<>();
        in.readList(audiences, Audience.class.getClassLoader());
        rank = in.readInt();
    }

    public Data() {}

}