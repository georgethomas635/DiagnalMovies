package in.app.diagnalsample.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import in.app.diagnalsample.R;
import in.app.diagnalsample.adapters.MovieListAdapter;
import in.app.diagnalsample.apps.Constants;
import in.app.diagnalsample.contracts.MainActivityContracts;
import in.app.diagnalsample.models.Movie;
import in.app.diagnalsample.presenters.activities.MainActivityPresenter;
import in.app.diagnalsample.utils.RecyclerViewMargin;

import static in.app.diagnalsample.apps.Constants.EMPTY;

public class MainActivity extends AppCompatActivity implements
        MovieListAdapter.OnItemClickListener, MovieListAdapter.OnReloadClickListener, MainActivityContracts.View {

private static final String BUNDLE_MOVIES_KEY = "Movie List";
private static final String BUNDLE_SEARCH_MOVIES = "Serched Movie List";
private static final String SEARCH_KEY = "Search";
private static final String PAGE_COUNT_KEY = "Page Count";

private Unbinder unbinder;
private MovieListAdapter adapter;
private boolean isLastPage = false;
private boolean isLoading = false;
private int pageCount = 1;
private int maxPageCount = 3;
private GridLayoutManager layoutManager;
private MainActivityPresenter mPresenter;
private int coloumSize;
private int textMinLength = 3;
private ArrayList<Movie> searchResult;
private ArrayList<Movie> movies;

@BindView(R.id.etxt_search)
EditText etxtSearch;

@BindView(R.id.txt_title)
TextView txtTitle;

@BindView(R.id.recycler_view)
RecyclerView recyclerView;

@BindView(R.id.txt_empty_list)
TextView txtErromMessage;

@BindView(R.id.img_search)
ImageView imgSearch;


@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    unbinder = ButterKnife.bind(this);
    initPresenter();
    setcolumSize();
    setRecyclerView();
    setListener();
    handleRotation(savedInstanceState);
}

private void handleRotation(Bundle savedInstanceState) {
    if(savedInstanceState == null){
        movies = mPresenter.getMovieList(String.valueOf(pageCount), getAssets());
        setAdapter(movies);
    }else if(etxtSearch.getVisibility() == View.VISIBLE){
        movies = savedInstanceState.getParcelableArrayList(BUNDLE_MOVIES_KEY);
        searchResult = savedInstanceState.getParcelableArrayList(BUNDLE_SEARCH_MOVIES);
        setAdapter(searchResult);
    }else{
        movies = savedInstanceState.getParcelableArrayList(BUNDLE_MOVIES_KEY);
        setAdapter(movies);
    }
}

@Override
public void setcolumSize() {
    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
        coloumSize = Constants.COLOUM_SIZE;
    }else{
        coloumSize = Constants.COLOUM_SIZE_LANDSCAPE;
    }
}

@Override
public void showSearchField(Boolean visibility) {
    etxtSearch.setVisibility(visibility ? View.VISIBLE : View.GONE);
    txtTitle.setVisibility(visibility ? View.GONE : View.VISIBLE);
    imgSearch.setImageDrawable(visibility ? getResources().getDrawable(R.drawable.ic_close_white) :
            getResources().getDrawable(R.drawable.ic_search));
}

@Override
public void emptyMovieList(Boolean visibility) {
    txtErromMessage.setVisibility(visibility ? View.VISIBLE : View.GONE);
}

@Override
protected void onDestroy() {
    if(unbinder != null){
        unbinder.unbind();
    }
    mPresenter.detach();
    super.onDestroy();
}

/**
 * save the list of movies and Page count
 *
 * @param savedInstanceState
 */
@Override
protected void onSaveInstanceState(final Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
    
    // Save the state of item position
    saveSearchStatus(savedInstanceState);
    savedInstanceState.putString(SEARCH_KEY, etxtSearch.getText().toString());
    savedInstanceState.putInt(PAGE_COUNT_KEY, pageCount);
}

/**
 * If list showing search result then save them. Otherwise save full movie list
 *
 * @param savedInstanceState
 */
private void saveSearchStatus(Bundle savedInstanceState) {
    if(etxtSearch.getVisibility() == View.VISIBLE){
        savedInstanceState.putParcelableArrayList(BUNDLE_SEARCH_MOVIES, searchResult);
        savedInstanceState.putParcelableArrayList(BUNDLE_MOVIES_KEY, movies);
    }else{
        savedInstanceState.putParcelableArrayList(BUNDLE_MOVIES_KEY, movies);
    }
}

@Override
protected void onRestoreInstanceState(final Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    // Read the state of item position
    checkSearch(Objects.requireNonNull(savedInstanceState.getString(SEARCH_KEY)));
    pageCount = savedInstanceState.getInt(PAGE_COUNT_KEY, pageCount);
}

//region: Search Movies
private void checkSearch(String searchKey) {
    if(!searchKey.equals(EMPTY)){
        showSearchField(true);
    }
}

private void setListener() {
    setEditorActionListener();
    etxtSearch.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        
        }
        
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        
        }
        
        @Override
        public void afterTextChanged(Editable s) {
            if(s.length() >= textMinLength){
                searchFor(s.toString());
            }
        }
    });
}

/**
 * Set the error message for the search bar
 */
private void setEditorActionListener() {
    etxtSearch.setOnEditorActionListener((v, actionId, event) -> {
        if(etxtSearch.getText().length() < textMinLength){
            etxtSearch.setError(getResources().getText(R.string.write_minimum_charecters));
            return true;
        }
        return false;
    });
}

/**
 * Searching for a movie name in the list.
 *
 * @param searchId : Search keyword
 */
private void searchFor(String searchId) {
    searchResult = new ArrayList<>();
    for(Movie item : movies){
        if(item.getName().toLowerCase().contains(searchId.toLowerCase())){
            searchResult.add(item);
        }
    }
    if(searchResult.size() > Constants.ZERO){
        modifyMovieList(searchResult);
    }else{
        emptyMovieList(true);
        adapter.clear();
        Toast.makeText(this, getResources().getString(R.string.result_not_found), Toast.LENGTH_SHORT).show();
    }
}

// endregion MO

private void initPresenter() {
    mPresenter = new MainActivityPresenter();
    mPresenter.attach(this);
}

//region: RecyclerView
/**
 * Seting up the Recycler view with GridLayout
 */
private void setRecyclerView() {
    layoutManager = new GridLayoutManager(this, coloumSize);
    recyclerView.setLayoutManager(layoutManager);
}

/**
 * Create the recyclerview adapter with fixed three coloums
 *
 * @param list :List of Movie objects.
 */

@Override
public void setAdapter(ArrayList<Movie> list) {
    
    isLoading = false;
    isLastPage = false;
    
    if(adapter == null){
        adapter = new MovieListAdapter(this);
        adapter.setOnItemClickListener(this);
        adapter.setOnReloadClickListener(this);
        recyclerView.addItemDecoration(new RecyclerViewMargin(Constants.RECYCLERVIEW_MARGIN, coloumSize));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
    }else{
        adapter.clear();
    }
    adapter.addAll(list);
}

/**
 * Implementation of Recycler view Scoll listener for pagination.
 */
private RecyclerView.OnScrollListener recyclerViewOnScrollListener =
        new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                handleRecyclerViewScrolling();
                
            }
        };

private void handleRecyclerViewScrolling() {
    
    int visibleItemCount = layoutManager.getChildCount();
    int totalItemCount = layoutManager.getItemCount();
    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
    
    if(!isLoading && !isLastPage){
        if((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                && firstVisibleItemPosition >= Constants.ZERO
                && totalItemCount >= Constants.TOTAL_ITEM_COUNT){
            loadMoreMovieItems();
        }
    }
}

/**
 * This method will handle the pagination for movie list.
 */
private void loadMoreMovieItems() {
    isLoading = true;
    pageCount++;
    if(pageCount < maxPageCount){
        adapter.addFooter();
        isLastPage = false;
    }
    if(pageCount <= maxPageCount){
        new Handler().postDelayed(() -> {
            if(pageCount == maxPageCount){
                isLastPage = true;
                adapter.removeFooter();
            }
            ArrayList<Movie> movieLisy = mPresenter.getMovieList(String.valueOf(pageCount), getAssets());
            adapter.addAll(movieLisy);
            movies.addAll(movieLisy);
            isLoading = false;
        }, Constants.TIME_DELAY);
    }
    
}

//endregion

@OnClick(R.id.img_back)
void onBackArrowPress() {
    onBackPressed();
}


@OnClick(R.id.img_search)
void onSearchIconClicked() {
    if(etxtSearch.getVisibility() == View.VISIBLE){
        modifyMovieList(movies);
        etxtSearch.setText(EMPTY);
    }
    showSearchField(etxtSearch.getVisibility() == View.GONE);
}

@Override
public void onItemClick(int position, View view) {

}

@Override
public void onReloadClick() {

}

/**
 * Modify the list of movies
 *
 * @param movies: List of movies
 */
private void modifyMovieList(ArrayList<Movie> movies) {
    emptyMovieList(false);
    if(adapter != null){
        adapter.clear();
        adapter.addAll(movies);
        adapter.notifyDataSetChanged();
    }
}
    
    
}
