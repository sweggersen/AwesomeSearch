package info.awesomedevelopment.awesomesearch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Sam Mathias Weggersen on 03/07/15.
 */
public abstract class AwesomeSearchResultAdapter<I, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private List<I> mResults;
    private OnItemClickListener<I> mListener;

    public AwesomeSearchResultAdapter() {
    }

    protected void setOnClickListener(OnItemClickListener<I> listener) {
        mListener = listener;
    }

    @Override
    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClicked(getItem(position), v, position);
            }
        });
        onBindViewHolder(holder, getItem(position), position);
    }

    public abstract void onBindViewHolder(final VH holder, final I item, final int position);

    public void updateResults(List<I> results) {
        mResults = results;
        notifyDataSetChanged();
    }

    public I getItem(int position) {
        return mResults.get(position);
    }

    @Override
    public int getItemCount() {
        return mResults != null ? mResults.size() : 0;
    }

    public interface OnItemClickListener<E> {
        void onItemClicked(E item, View v, int position);
    }
}

