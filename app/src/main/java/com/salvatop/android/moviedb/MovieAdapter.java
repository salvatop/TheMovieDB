package com.salvatop.android.moviedb;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * {@link MovieAdapter} exposes a list of weather forecasts to a
 * {@link android.support.v7.widget.RecyclerView}
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private String[] movies;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final adapterOnClickHandler mClickHandler;

    public interface adapterOnClickHandler {
        void onClick(String movie);
    }

    /**
     * Creates a MovieAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public MovieAdapter(adapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a movies list item.
     */
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final TextView mWeatherTextView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mWeatherTextView = (TextView) view.findViewById(R.id.tv_weather_data);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String movie = movies[adapterPosition];
            mClickHandler.onClick(movie);
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new MovieAdapterViewHolder that holds the View for each list item
     */
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View itemView = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        int height = viewGroup.getMeasuredHeight() / 2;
        itemView.setMinimumHeight(height);
        return new MovieAdapterViewHolder(itemView);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param MovieAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder MovieAdapterViewHolder, int position) {
        String weatherForThisDay = movies[position];
        MovieAdapterViewHolder.mWeatherTextView.setText(weatherForThisDay);
    }

    @Override
    public int getItemCount() {
        if (movies == null) return 0;
        return movies.length;
    }

    public void setMovie(String[] movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }
}