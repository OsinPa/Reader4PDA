package ru.OsinPA.Reader4PDA;

/**
 * Created by Osin on 06.07.2015.
 */
public class Item4PDA {

    private String mUrl;
    private String mTitle;
    private String mUrlImage;

    public String getmUrlImage() {
        return mUrlImage;
    }

    public void setmUrlImage(String mUrlImage) {
        this.mUrlImage = mUrlImage;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    @Override
    public boolean equals(Object o) {
        return mUrl.equals(((Item4PDA) o).mUrl);
    }

    @Override
    public int hashCode() {
        return mUrl.hashCode();
    }
}
