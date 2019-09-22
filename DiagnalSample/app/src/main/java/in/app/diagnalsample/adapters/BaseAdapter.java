package in.app.diagnalsample.adapters;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // region Constants
    protected static final int HEADER = 0;
    protected static final int ITEM = 1;
    protected static final int FOOTER = 2;
    protected static final int OTHER_RESULT = 3;
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int INVALIED_POSITION = -1;
    // endregion

    // region Member Variables
    protected List<T> items;
    public OnItemClickListener onItemClickListener;
    private OnReloadClickListener onReloadClickListener;
    protected boolean isFooterAdded = false;
    // endregion

    // region Interfaces
    public interface OnItemClickListener {
        void onItemClick(int position, View view);
    }

    public interface OnReloadClickListener {
        void onReloadClick();
    }
    // endregion

    // region Constructors
    public BaseAdapter() {
        items = new ArrayList<>();
    }
    // endregion


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case HEADER:
                viewHolder = createHeaderViewHolder(parent);
                break;
            case ITEM:
                viewHolder = createItemViewHolder(parent);
                break;
            case FOOTER:
                viewHolder = createFooterViewHolder(parent);
                break;
            case OTHER_RESULT:
                viewHolder = createOtherResultViewHolder(parent);
                break;
            default:
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case HEADER:
                bindHeaderViewHolder(viewHolder);
                break;
            case ITEM:
                bindItemViewHolder(viewHolder, position);
                break;
            case FOOTER:
                bindFooterViewHolder(viewHolder);
                break;
            case OTHER_RESULT:
                bindOtherResultViewHolder(viewHolder);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // region Abstract Methods
    protected abstract RecyclerView.ViewHolder createHeaderViewHolder(ViewGroup parent);

    protected abstract RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent);

    protected abstract RecyclerView.ViewHolder createFooterViewHolder(ViewGroup parent);

    protected abstract void bindHeaderViewHolder(RecyclerView.ViewHolder viewHolder);

    protected abstract void bindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position);

    protected abstract void bindFooterViewHolder(RecyclerView.ViewHolder viewHolder);

    protected abstract RecyclerView.ViewHolder createOtherResultViewHolder(ViewGroup parent);

    protected abstract void bindOtherResultViewHolder(RecyclerView.ViewHolder viewHolder);

    protected abstract void displayLoadMoreFooter();

    protected abstract void displayErrorFooter();

    public abstract void addFooter();
    // endregion

    // region Helper Methods
    protected T getItem(int position) {
        return items.get(position);
    }

    private void add(T item) {
        items.add(item);
        notifyItemInserted(items.size() - ONE);
    }

    public void addAll(List<T> items) {
        for (T item : items) {
            add(item);
        }
    }

    public void remove(T item) {
        int position = items.indexOf(item);
        if (position > INVALIED_POSITION) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isFooterAdded = false;
        while (getItemCount() > ZERO) {
            remove(getItem(ZERO));
        }
    }

    boolean isLastPosition(int position) {
        return (position == items.size()-ONE);
    }

    public void removeFooter() {
        isFooterAdded = false;

        int position = items.size() - ONE;
        T item = getItem(position);

        if (item != null) {
            notifyItemRemoved(position);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnReloadClickListener(OnReloadClickListener onReloadClickListener) {
        this.onReloadClickListener = onReloadClickListener;
    }
    // endregion
}