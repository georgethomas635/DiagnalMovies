package in.app.diagnalsample.presenters.activities;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import in.app.diagnalsample.R;
import in.app.diagnalsample.apps.Constants;
import in.app.diagnalsample.contracts.MainActivityContracts;
import in.app.diagnalsample.models.Movie;

import static in.app.diagnalsample.apps.Constants.PAGE;

public class MainActivityPresenter implements MainActivityContracts.Presenter {

    private MainActivityContracts.View mView;
private ArrayList<Movie> searchResult;
private ArrayList<Movie> movies;

    @Override
    public void attach(MainActivityContracts.View view) {
        mView=view;
    }

    @Override
    public void detach() {
        mView=null;
    }

    /**
     * Parse the JSON object into a Movie Class and return the arraylist
     * @param pageNumber : Page number of the JSON file
     * @param assets: Assets file
     * @return List of movies
     */
    @Override
    public ArrayList<Movie> getMovieList(String pageNumber, AssetManager assets){
        ArrayList<Movie> movies = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(readJSONFromAsset(pageNumber,assets));
            if(obj.has(PAGE)){
                JSONObject pageObject = obj.getJSONObject(PAGE);
                if(pageObject.has(Constants.CONTENT_ITEMS)){
                    JSONObject contentItemObject = pageObject.getJSONObject(Constants.CONTENT_ITEMS);
                    if(contentItemObject.has(Constants.CONTENT)){
                        JSONArray contents = contentItemObject.getJSONArray(Constants.CONTENT);
                        for(int index=Constants.ZERO;index<contents.length();index++){
                            Movie movie = new Movie();
                            JSONObject movieObject = contents.getJSONObject(index);
                            movie.setName(movieObject.getString(Constants.MOVIE_NAME));
                            movie.setPosterImage(movieObject.getString(Constants.POSTER_IMAGE));
                            movies.add(movie);
                        }
                    }
                }
            }

        } catch (JSONException exp) {
            Log.e(exp.getMessage(),exp.getMessage(),exp);
        }
        saveMovieList(movies);
        return movies;
    }

private void saveMovieList(ArrayList<Movie> movieList) {
    if(movies != null){
        movies.addAll(movieList);
    }else{
        movies = new ArrayList<>(movieList);
    }
}

/**
     * Read the JSON from file
     * @param pageNumber:Indicates the JSON file name.
     * @return: JSON string read
     */

    @Override
    public String readJSONFromAsset(String pageNumber,AssetManager assets) {
        String json;
        try {
            InputStream is = assets.open(Constants.FILE_NAME + pageNumber + Constants.JSON_EXTENSION);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

@Override
public void handleRotation(Bundle savedInstanceState, int searchVisisbility, int pagecount, AssetManager assets) {
    if(savedInstanceState == null){
        movies = getMovieList(String.valueOf(pagecount), assets);
        mView.setAdapter(movies);
    }else if(searchVisisbility == View.VISIBLE){
        movies = savedInstanceState.getParcelableArrayList(Constants.BUNDLE_MOVIES_KEY);
        searchResult = savedInstanceState.getParcelableArrayList(Constants.BUNDLE_SEARCH_MOVIES);
        mView.setAdapter(searchResult);
    }else{
        movies = savedInstanceState.getParcelableArrayList(Constants.BUNDLE_MOVIES_KEY);
        mView.setAdapter(movies);
    }
}

/**
 * Searching for a movie name in the list.
 *
 * @param searchId : Search keyword
 */
@Override
public void searchFor(String searchId) {
    searchResult = new ArrayList<>();
    for(Movie item : getMovies()){
        if(item.getName().toLowerCase().contains(searchId.toLowerCase())){
            searchResult.add(item);
        }
    }
    if(searchResult.size() > Constants.ZERO){
        mView.modifyMovieList(searchResult);
    }else{
        mView.emptyMovieList(true);
        mView.clearAdapter();
        mView.showErrorMessage(R.string.result_not_found);
    }
}

@Override
public ArrayList<Movie> getMovies() {
    return movies;
}

@Override
public ArrayList<Movie> getSearchResult() {
    return searchResult;
}


}
