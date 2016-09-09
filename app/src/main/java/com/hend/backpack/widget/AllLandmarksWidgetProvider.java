package com.hend.backpack.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.hend.backpack.R;
import com.hend.backpack.ui.LandmarkListActivity;

/**
 * Created by hend on 9/3/16.
 */
public class AllLandmarksWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds)
        {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_all_landmarks);

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, LandmarkListActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Set up the collection
                setRemoteAdapter(context, views);



            //views.setOnClickPendingIntent(R.id.widget, pendingIntent);
            views.setEmptyView(R.id.widget_list, R.id.widget_empty);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (LandmarkListActivity.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        }
    }


    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        Log.d("AllLandmarksWidget", "Set Adapter");
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, AllLandmarksWidgetIntentService.class));
    }


}
