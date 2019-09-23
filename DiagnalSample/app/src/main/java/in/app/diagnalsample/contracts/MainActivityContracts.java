package in.app.diagnalsample.contracts;

import android.content.res.AssetManager;

import java.util.ArrayList;

import in.app.diagnalsample.models.Movie;

public interface MainActivityContracts {

    interface View {
        void setAdapter(ArrayList<Movie> list);
        
        void setcolumSize();
    }

    interface Presenter extends BasePresenter<View>{
        ArrayList<Movie> getMovieList(String pageNumber, AssetManager assets);

        String readJSONFromAsset(String pageNumber,AssetManager assets);

    }
}
