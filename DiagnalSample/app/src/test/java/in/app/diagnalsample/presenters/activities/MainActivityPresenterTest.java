package in.app.diagnalsample.presenters.activities;

import android.content.Context;
import android.content.res.AssetManager;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import in.app.diagnalsample.apps.Constants;
import in.app.diagnalsample.contracts.MainActivityContracts;
import in.app.diagnalsample.models.Movie;

import static in.app.diagnalsample.apps.Constants.PAGE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class MainActivityPresenterTest {

    private MainActivityPresenter presenter;
    private ArrayList<Movie> movieList = new ArrayList<>();
    private String PAGE1_JSON = "{ \"page\": { \"title\": \"Romantic Comedy\", \"total-content-items\" : \"54\", \"page-num\" : \"1\", \"page-size\" : \"20\", \"content-items\": { \"content\": [ { \"name\": \"The Birds\", \"poster-image\": \"poster1.jpg\" }, { \"name\": \"Rear Window\", \"poster-image\": \"poster2.jpg\" }, { \"name\": \"Family Pot\", \"poster-image\": \"poster3.jpg\" }, { \"name\": \"Family Pot\", \"poster-image\": \"poster2.jpg\" }, { \"name\": \"Rear Window\", \"poster-image\": \"poster1.jpg\" }, { \"name\": \"The Birds\", \"poster-image\": \"poster3.jpg\" }, { \"name\": \"Rear Window\", \"poster-image\": \"poster3.jpg\" }, { \"name\": \"The Birds\", \"poster-image\": \"poster2.jpg\" }, { \"name\": \"Family Pot\", \"poster-image\": \"poster1.jpg\" }, { \"name\": \"The Birds\", \"poster-image\": \"poster1.jpg\" }, { \"name\": \"The Birds\", \"poster-image\": \"poster1.jpg\" }, { \"name\": \"Rear Window\", \"poster-image\": \"poster2.jpg\" }, { \"name\": \"Family Pot\", \"poster-image\": \"poster3.jpg\" }, { \"name\": \"Family Pot\", \"poster-image\": \"poster2.jpg\" }, { \"name\": \"Rear Window\", \"poster-image\": \"poster1.jpg\" }, { \"name\": \"The Birds\", \"poster-image\": \"poster3.jpg\" }, { \"name\": \"Rear Window\", \"poster-image\": \"poster3.jpg\" }, { \"name\": \"The Birds\", \"poster-image\": \"poster2.jpg\" }, { \"name\": \"Family Pot\", \"poster-image\": \"poster1.jpg\" }, { \"name\": \"The Birds\", \"poster-image\": \"poster1.jpg\" } ] } } }";

    @Mock
    private MainActivityContracts.View view;

    @Mock
    AssetManager assetManager;

    @Mock
    Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        MockitoAnnotations.initMocks(this);//create all @Mock objetcs
        doReturn(assetManager).when(context).getAssets();

        presenter = new MainActivityPresenter();
        presenter.attach(view);
    }

    @Test
    public void getMovieList() {
        //Failed case
        ArrayList<Movie> movies =presenter.getMovieList("5",assetManager);
        assertNotEquals(movies,movieList);

        //SuccessCase
        ArrayList<Movie> movieArrayList =presenter.getMovieList("1",assetManager);
        movieList = getMovieModel();
        assertNotEquals(movieArrayList,movieList);
    }

    private ArrayList<Movie> getMovieModel() {
        JSONObject obj;
        try {
            obj = new JSONObject(PAGE1_JSON);
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
                        movieList.add(movie);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Test
    public void readJSONFromAsset() {
        String json=presenter.readJSONFromAsset("1",assetManager);
        assertEquals(json,PAGE1_JSON);
    }
}