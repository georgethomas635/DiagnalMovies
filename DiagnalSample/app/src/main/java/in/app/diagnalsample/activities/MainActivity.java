package in.app.diagnalsample.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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

public class MainActivity extends AppCompatActivity implements
        MovieListAdapter.OnItemClickListener, MovieListAdapter.OnReloadClickListener, MainActivityContracts.View {


    private Unbinder unbinder;
    private MovieListAdapter adapter;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private int pageCount = 1;
    private GridLayoutManager layoutManager;
    private MainActivityPresenter mPresenter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);
        initPresenter();
        setAdapter(mPresenter.getMovieList(String.valueOf(pageCount), getAssets()));

    }

    private void initPresenter() {
        mPresenter = new MainActivityPresenter();
        mPresenter.attach(this);
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        mPresenter.detach();
        super.onDestroy();
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

        if (adapter == null) {
            adapter = new MovieListAdapter(this);
            adapter.setOnItemClickListener(this);
            adapter.setOnReloadClickListener(this);
            layoutManager = new GridLayoutManager(this, Constants.COLOUM_SIZE);
            recyclerView.addItemDecoration(new RecyclerViewMargin(Constants.RECYCLERVIEW_MARGIN, Constants.COLOUM_SIZE));
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
        } else {
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

        if (!isLoading && !isLastPage) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= Constants.ZERO
                    && totalItemCount >= Constants.TOTAL_ITEM_COUNT) {
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
        if (pageCount < Constants.COLOUM_SIZE) {
            adapter.addFooter();
            isLastPage = false;
        }
        new Handler().postDelayed(() -> {
            if (pageCount == Constants.COLOUM_SIZE) {
                isLastPage = true;
                adapter.removeFooter();
            }
            adapter.addAll(mPresenter.getMovieList(String.valueOf(pageCount), getAssets()));
            isLoading = false;
        }, Constants.TIME_DELAY);

    }

    @OnClick(R.id.img_back)
    void onBackArrowPress() {
        onBackPressed();
    }

    @Override
    public void onItemClick(int position, View view) {

    }

    @Override
    public void onReloadClick() {

    }
}
