package in.app.diagnalsample.presenters.activities;

import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import in.app.diagnalsample.apps.Constants;
import in.app.diagnalsample.contracts.MainActivityContracts;
import in.app.diagnalsample.models.Movie;

import static in.app.diagnalsample.apps.Constants.PAGE;

public class MainActivityPresenter implements MainActivityContracts.Presenter {

    private MainActivityContracts.View mView;

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
        return movies;
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
}
