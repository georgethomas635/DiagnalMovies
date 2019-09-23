package in.app.diagnalsample.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private String name;

    private String posterImage;

public Movie() {
}

protected Movie(Parcel in) {
    name = in.readString();
    posterImage = in.readString();
}

public static final Creator<Movie> CREATOR = new Creator<Movie>() {
    @Override
    public Movie createFromParcel(Parcel in) {
        return new Movie(in);
    }
    
    @Override
    public Movie[] newArray(int size) {
        return new Movie[size];
    }
};

public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosterImage() {
        return posterImage;
    }

    public void setPosterImage(String posterImage) {
        this.posterImage = posterImage;
    }

@Override
public int describeContents() {
    return 0;
}

@Override
public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(name);
    dest.writeString(posterImage);
}
}
