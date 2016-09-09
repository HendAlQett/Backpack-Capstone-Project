package com.hend.backpack.widget;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.hend.backpack.R;
import com.hend.backpack.data.LandmarkProvider;
import com.hend.backpack.ui.LandmarkListActivity;

/**
 * Created by hend on 9/3/16.
 */
public class AllLandmarksWidgetIntentService extends RemoteViewsService {

    static final int COL_ID = 0;

    static final int COL_LANDMARK_NAME_EN = 2;

    static final int COL_LANDMARK_DESCRIPTION_EN = 4;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;
            String landmarkName = getString(R.string.empty_text);
            String landmarkDescription = getString(R.string.empty_text);

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                Uri landmarkUri = LandmarkProvider.Landmarks.CONTENT_URI;
                data = getContentResolver().query(landmarkUri, null, null, null, null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_landmarks_list_item);

                landmarkName = data.getString(COL_LANDMARK_NAME_EN);
                landmarkDescription = data.getString(COL_LANDMARK_DESCRIPTION_EN);

                views.setTextViewText(R.id.tvLandmarkName, landmarkName);
                views.setTextViewText(R.id.tvLandmarkDescription, landmarkDescription);

                Intent launchIntent = new Intent(AllLandmarksWidgetIntentService.this, LandmarkListActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(AllLandmarksWidgetIntentService.this, 0, launchIntent, 0);
                views.setOnClickPendingIntent(R.id.widget_list_item, pendingIntent);
                return views;

            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_landmarks_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(COL_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
