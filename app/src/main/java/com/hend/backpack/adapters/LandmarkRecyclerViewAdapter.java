package com.hend.backpack.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hend.backpack.R;
import com.hend.backpack.data.LandmarkColumns;
import com.hend.backpack.data.LandmarkDatabase;
import com.hend.backpack.models.Landmark;

import java.util.List;

/**
 * Created by hend on 8/13/16.
 */
public class LandmarkRecyclerViewAdapter extends RecyclerView.Adapter<LandmarkRecyclerViewAdapter.LandmarkAdapterViewHolder> {

    Context mContext;
    List<Landmark> mLandmarks;
    final private LandmarkAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;
    private static final String[] LANDMARK_COLUMNS = {
            LandmarkDatabase.LANDMARKS + "." + LandmarkColumns.ID,
            LandmarkColumns.LANDMARK_ID,
            LandmarkColumns.LANDMARK_NAME_EN,
            LandmarkColumns.LANDMARK_NAME_AR,
            LandmarkColumns.LANDMARK_DESCRIPTION_EN,
            LandmarkColumns.LANDMARK_DESCRIPTION_AR,
            LandmarkColumns.LANDMARK_IMAGE_URL,
            LandmarkColumns.LATITUDE,
            LandmarkColumns.LONGITUDE,
            LandmarkColumns.LANDMARK_RADIUS,
            LandmarkColumns.FLAG_STREET_VIEW
    };

    static final int COL_ID = 0;
    static final int COL_LANDMARK_ID = 1;
    static final int COL_LANDMARK_NAME_EN = 2;
    static final int COL_LANDMARK_NAME_AR = 3;
    static final int COL_LANDMARK_DESCRIPTION_EN = 4;
    static final int COL_LANDMARK_DESCRIPTION_AR = 5;
    static final int COL_LANDMARK_IMAGE_URL = 6;
    static final int COL_LATITUDE = 7;
    static final int COL_LONGITUDE = 8;
    static final int COL_LANDMARK_RADIUS = 9;
    static final int COL_FLAG_STREET_VIEW = 10;


    public LandmarkRecyclerViewAdapter(Context mContext, LandmarkAdapterOnClickHandler dh, View emptyView, List<Landmark> mLandmarks) {
        this.mContext = mContext;
        this.mClickHandler = dh;
        mEmptyView = emptyView;
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
        Landmark landmark = mLandmarks.get(position);
        holder.tvLandmark.setText(landmark.getName_en());
        Glide.with(mContext)
                .load(landmark.getImage_url())
                .centerCrop()
                .error(R.mipmap.ic_launcher)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.ivLandmark);
        holder.ivLandmark.setContentDescription(landmark.getName_en());
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
            mClickHandler.onClick(mLandmarks.get(adapterPosition), this);

        }
    }

    public void swapCursor(Cursor cursor) {
        mLandmarks.clear();
        if (cursor != null && cursor.getCount() != 0) {
            int landmarkId, radius;
            boolean streetView;
            Double latitude, longitude;
            String nameEn, nameAr, descriptionEn, descriptionAr, imageUrl;

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                // The Cursor is now set to the right position
                landmarkId = cursor.getInt(COL_LANDMARK_ID);
                nameEn = cursor.getString(COL_LANDMARK_NAME_EN);
                nameAr = cursor.getString(COL_LANDMARK_NAME_AR);
                descriptionEn = cursor.getString(COL_LANDMARK_DESCRIPTION_EN);
                descriptionAr = cursor.getString(COL_LANDMARK_DESCRIPTION_AR);
                imageUrl = cursor.getString(COL_LANDMARK_IMAGE_URL);
                latitude = cursor.getDouble(COL_LATITUDE);
                longitude = cursor.getDouble(COL_LONGITUDE);
                radius = cursor.getInt(COL_LANDMARK_RADIUS);
                streetView = cursor.getInt(COL_FLAG_STREET_VIEW) > 0 ? true : false;
                mLandmarks.add(new Landmark(landmarkId, nameEn, nameAr, descriptionEn, descriptionAr, imageUrl, latitude, longitude, radius, streetView));
            }
        }

        notifyDataSetChanged();

        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);

    }

    public static interface LandmarkAdapterOnClickHandler {
        void onClick(Landmark landmark, LandmarkAdapterViewHolder vh);
    }

    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof LandmarkAdapterViewHolder) {
            LandmarkAdapterViewHolder vfh = (LandmarkAdapterViewHolder) viewHolder;
            vfh.onClick(vfh.itemView);
        }
    }

}
