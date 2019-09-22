package in.app.diagnalsample.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import in.app.diagnalsample.R;
import in.app.diagnalsample.models.Movie;

public class MovieListAdapter extends BaseAdapter<Movie> {

    private static final String POSTER_1 = "poster1.jpg";
    private static final String POSTER_2 = "poster2.jpg";
    private static final String POSTER_3 = "poster3.jpg";
    private static final String POSTER_4 = "poster4.jpg";
    private static final String POSTER_5 = "poster5.jpg";
    private static final String POSTER_6 = "poster6.jpg";
    private static final String POSTER_7 = "poster7.jpg";
    private static final String POSTER_8 = "poster8.jpg";
    private static final String POSTER_9 = "poster9.jpg";
    private static final int ZERO = 0;

    private FooterViewHolder footerViewHolder;
    private static Context mContext;
    private RequestOptions requestOptions;

    @SuppressLint("CheckResult")
    public MovieListAdapter(Context context) {
        super();
        mContext = context;
        requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.placeholder);
    }

    @Override
    public int getItemViewType(int position) {
        return (isLastPosition(position) && isFooterAdded) ? FOOTER : ITEM;
    }

    @Override
    protected RecyclerView.ViewHolder createHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    protected RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_movie_item, parent, false);
        return new PropertyViewHolder(v);
    }

    @Override
    protected RecyclerView.ViewHolder createOtherResultViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    protected void bindOtherResultViewHolder(RecyclerView.ViewHolder viewHolder) {
    }

    @Override
    protected void displayLoadMoreFooter() {
        if(footerViewHolder!= null){
            footerViewHolder.errorRelativeLayout.setVisibility(View.GONE);
            footerViewHolder.loadingFrameLayout.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected RecyclerView.ViewHolder createFooterViewHolder(ViewGroup parent) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progressbar, parent, false);
        return new FooterViewHolder(mView);
    }

    @Override
    protected void bindHeaderViewHolder(RecyclerView.ViewHolder viewHolder) {

    }

    @Override
    protected void bindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final PropertyViewHolder holder = (PropertyViewHolder) viewHolder;

        final Movie movie;
        movie = getItem(position);
        if (movie != null) {
            setViews(holder, movie);

        }
    }

    private void setViews(PropertyViewHolder holder, Movie movie) {
        holder.txtTitle.setText(movie.getName());
        Glide.with(mContext)
                .setDefaultRequestOptions(requestOptions)
                .load(setPosterImage(movie.getPosterImage()))
                .into(holder.imgPoster);
    }

    private int setPosterImage(String posterImage) {
        if (posterImage!=null) {
            switch (posterImage) {
                case POSTER_1:
                    return R.drawable.poster1;
                case POSTER_2:
                    return R.drawable.poster2;
                case POSTER_3:
                    return R.drawable.poster3;
                case POSTER_4:
                    return R.drawable.poster4;
                case POSTER_5:
                    return R.drawable.poster5;
                case POSTER_6:
                    return R.drawable.poster6;
                case POSTER_7:
                    return R.drawable.poster7;
                case POSTER_8:
                    return R.drawable.poster8;
                case POSTER_9:
                    return R.drawable.poster9;
                default:
                    return R.drawable.ic_image_placeholder;
            }
        }
        return ZERO;
    }

    @Override
    protected void bindFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        footerViewHolder = (FooterViewHolder) viewHolder;
        //holder.loadingImageView.setMaskOrientation(LoadingImageView.MaskOrientation.LeftToRight);
    }

    @Override
    protected void displayErrorFooter() {
        if (footerViewHolder != null) {
            footerViewHolder.loadingFrameLayout.setVisibility(View.GONE);
            footerViewHolder.errorRelativeLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void addFooter() {
        isFooterAdded = true;
//        add(new Movie());
    }

    public class PropertyViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        // region Views
        private TextView txtTitle;
        private ImageView imgPoster;

        // region Constructors
        PropertyViewHolder(View view) {
            super(view);
            txtTitle = itemView.findViewById(R.id.txt_title);
            imgPoster = itemView.findViewById(R.id.img_poster);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getPosition(), v);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        // region Views
        FrameLayout loadingFrameLayout;
        RelativeLayout errorRelativeLayout;

        FooterViewHolder(View view) {
            super(view);
            loadingFrameLayout = view.findViewById(R.id.loading_fl);
        }
    }
}