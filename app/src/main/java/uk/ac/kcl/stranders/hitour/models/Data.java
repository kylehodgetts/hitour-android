package uk.ac.kcl.stranders.hitour.models;

public class Data {

    private String dataId;
    private String title;
    private String description;
    private String url;


    public Data(String dataId, String title, String description, String url) {
        this.dataId = dataId;
        this.title = title;
        this.description = description;
        this.url = url;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
