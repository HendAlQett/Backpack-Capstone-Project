package com.hend.backpack.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hend.backpack.R;
import com.hend.backpack.models.Landmark;
import com.hend.backpack.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Landmark detail screen.
 * This fragment is either contained in a {@link LandmarkListActivity}
 * in two-pane mode (on tablets) or a {@link LandmarkDetailActivity}
 * on handsets.
 */
public class LandmarkDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    @BindView(R.id.tvLandmarkDescription)
    TextView tvLandmarkDescription;
    @BindView(R.id.btnStreetView)
    AppCompatButton btnStreetView;
    static final String DETAIL_TRANSITION_ANIMATION = "DTA";

    /**
     * The dummy content this fragment is presenting.
     */
    private Landmark landmark;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LandmarkDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(Constants.LANDMARK)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            landmark = getArguments().getParcelable(Constants.LANDMARK);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(landmark.getName_en());
            }
        }
    }

    public interface Callback {
        public void onStreetViewClicked(Landmark landmark);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.landmark_detail, container, false);
        ButterKnife.bind(this, rootView);

        tvLandmarkDescription.setText(landmark.getDescription_en());
        tvLandmarkDescription.setContentDescription(landmark.getDescription_en());

        if (rootView.findViewById(R.id.ivLandmark) != null) {

            ImageView ivLandmark = (ImageView) rootView.findViewById(R.id.ivLandmark);
            Glide.with(this).load(landmark.getImage_url())
                    .centerCrop()
                    .error(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(ivLandmark);
            ivLandmark.setContentDescription(landmark.getDescription_en());
        }

        btnStreetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Callback) getActivity()).onStreetViewClicked(landmark);
            }
        });
        return rootView;
    }
}
