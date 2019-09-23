package in.app.diagnalsample.contracts;

import android.content.res.AssetManager;
import android.os.Bundle;

import java.util.ArrayList;

import in.app.diagnalsample.models.Movie;

public interface MainActivityContracts {

    interface View {
        void setAdapter(ArrayList<Movie> list);
        
        void setcolumSize();
    
        void showSearchField(Boolean visibility);
    
        void emptyMovieList(Boolean visibility);
    
        void showErrorMessage(int messageId);
    
        void modifyMovieList(ArrayList<Movie> movies);
    
        void clearAdapter();
    }

    interface Presenter extends BasePresenter<View>{
        ArrayList<Movie> getMovieList(String pageNumber, AssetManager assets);

        String readJSONFromAsset(String pageNumber,AssetManager assets);
    
        void handleRotation(Bundle savedInstanceState, int visibility, int pagecount, AssetManager assets);
    
        ArrayList<Movie> getMovies();
    
        ArrayList<Movie> getSearchResult();
    
        void searchFor(String searchId);

    }
}
