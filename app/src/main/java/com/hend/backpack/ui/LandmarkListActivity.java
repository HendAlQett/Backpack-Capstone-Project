package com.hend.backpack.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.hend.backpack.R;
import com.hend.backpack.adapters.LandmarkRecyclerViewAdapter;
import com.hend.backpack.apis.RestClient;
import com.hend.backpack.models.Landmark;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * An activity representing a list of Landmarks. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link LandmarkDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class LandmarkListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    List<Landmark> landmarksList;
    LandmarkRecyclerViewAdapter adapter;
    @NonNull RecyclerView recyclerView;
//    private GridLayoutManager gridLayout;
    final String LOG_TAG = LandmarkListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        recyclerView = (RecyclerView) findViewById(R.id.landmark_list);
        assert recyclerView != null;
        landmarksList = new ArrayList<>();
        setupRecyclerView();
        getLandmarks();


        if (findViewById(R.id.landmark_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView() {
//        gridLayout = new GridLayoutManager(this, 2);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(gridLayout);
        adapter = new LandmarkRecyclerViewAdapter(LandmarkListActivity.this, landmarksList);
        recyclerView.setAdapter(adapter);
    }

    private void getLandmarks() {
        RestClient.get().requestLandmarks(new Callback<List<Landmark>>() {
            @Override
            public void success(List<Landmark> landmarks, Response response) {
                landmarksList.clear();
                landmarksList = landmarks;
                adapter = new LandmarkRecyclerViewAdapter(LandmarkListActivity.this, landmarksList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                for (Landmark landmark : landmarks) {
                    Log.d(LOG_TAG, landmark.getName_en());
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

//    public class SimpleItemRecyclerViewAdapter
//            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
//
//        private final List<DummyContent.DummyItem> mValues;
//
//        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
//            mValues = items;
//        }
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.landmark_list_content, parent, false);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(final ViewHolder holder, int position) {
//            holder.mItem = mValues.get(position);
//            holder.mIdView.setText(mValues.get(position).id);
//            holder.mContentView.setText(mValues.get(position).content);
//
//            holder.mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mTwoPane) {
//                        Bundle arguments = new Bundle();
//                        arguments.putString(LandmarkDetailFragment.ARG_ITEM_ID, holder.mItem.id);
//                        LandmarkDetailFragment fragment = new LandmarkDetailFragment();
//                        fragment.setArguments(arguments);
//                        getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.landmark_detail_container, fragment)
//                                .commit();
//                    } else {
//                        Context context = v.getContext();
//                        Intent intent = new Intent(context, LandmarkDetailActivity.class);
//                        intent.putExtra(LandmarkDetailFragment.ARG_ITEM_ID, holder.mItem.id);
//
//                        context.startActivity(intent);
//                    }
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return mValues.size();
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            public final View mView;
//            public final TextView mIdView;
//            public final TextView mContentView;
//            public DummyContent.DummyItem mItem;
//
//            public ViewHolder(View view) {
//                super(view);
//                mView = view;
//                mIdView = (TextView) view.findViewById(R.id.id);
//                mContentView = (TextView) view.findViewById(R.id.content);
//            }
//
//            @Override
//            public String toString() {
//                return super.toString() + " '" + mContentView.getText() + "'";
//            }
//        }
//    }
}
