package com.hend.backpack.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hend.backpack.R;
import com.hend.backpack.models.Landmark;

import java.util.List;

/**
 * Created by hend on 8/13/16.
 */
public class LandmarkRecyclerViewAdapter extends RecyclerView.Adapter<LandmarkRecyclerViewAdapter.LandmarkAdapterViewHolder> {

    Context mContext;
    List<Landmark> mLandmarks;

    public LandmarkRecyclerViewAdapter(Context mContext, List<Landmark> mLandmarks) {
        this.mContext = mContext;
        this.mLandmarks = mLandmarks;

    }

    @Override
    public LandmarkAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.landmark_list_item, parent, false);
        view.setFocusable(true);
        return new LandmarkAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LandmarkAdapterViewHolder holder, int position) {
        Landmark landmark= mLandmarks.get(position);
        holder.tvLandmark.setText(landmark.getName_en());
        Glide.with(mContext)
                .load(landmark.getImage_url())
                .centerCrop()
                .error(R.mipmap.ic_launcher)
                .crossFade()

                .into(holder.ivLandmark);

    }

    @Override
    public int getItemCount() {
        return mLandmarks.size();
    }

    public class LandmarkAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView ivLandmark;
        public final TextView tvLandmark;


        public LandmarkAdapterViewHolder(View view) {
            super(view);
            ivLandmark = (ImageView) view.findViewById(R.id.ivLandmark);
            tvLandmark = (TextView) view.findViewById(R.id.tvLandmark);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
//            mCursor.moveToPosition(adapterPosition);
//            int dateColumnIndex = mCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
//            mClickHandler.onClick(mCursor.getLong(dateColumnIndex), this);
//            mICM.onClick(this);
        }
    }

    public static interface LandmarkAdapterOnClickHandler {
        void onClick(Long date, LandmarkAdapterViewHolder vh);
    }

}
