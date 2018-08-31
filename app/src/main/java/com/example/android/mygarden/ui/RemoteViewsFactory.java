package com.example.android.mygarden.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.mygarden.R;
import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.utils.PlantUtils;

public class RemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    Cursor mCursor;

    public RemoteViewsFactory(Context appContext) {
        mContext = appContext;
    }

    @Override
    public void onCreate() {
        // intentionally empty
    }

    @Override
    public void onDataSetChanged() {
        Uri PLANT_URI = PlantContract.BASE_CONTENT_URI.buildUpon().appendPath(PlantContract.PATH_PLANTS).build();
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = mContext.getContentResolver().query(
                PLANT_URI,
                null,
                null,
                null,
                PlantContract.PlantEntry.COLUMN_CREATION_TIME
        );
    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        return (mCursor == null) ? 0 : mCursor.getCount();
    }

    /**
     * Acts like the onBindViewHolder method in an Adapter
     *
     * @param position  The current position of the item in the GridView to be displayed
     * @return          The RemoteViews object to display in the given position
     */
    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0) {
            return null;
        }

        mCursor.moveToPosition(position);

        long plantId = mCursor.getLong(mCursor.getColumnIndex(PlantContract.PlantEntry._ID));
        int plantType = mCursor.getInt(mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE));
        long createdAt = mCursor.getLong(mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME));
        long wateredAt = mCursor.getLong(mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME));
        long timeNow = System.currentTimeMillis();

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.plant_widget);

        // update the plant widget
        int imgRes = PlantUtils.getPlantImageRes(mContext, timeNow - createdAt, timeNow - wateredAt, plantType);
        remoteViews.setImageViewResource(R.id.widget_plant_image, imgRes);
        remoteViews.setTextViewText(R.id.widget_plant_name, String.valueOf(plantId));

        // always hide the water drop in grid view
        remoteViews.setViewVisibility(R.id.widget_water_button, View.GONE);

        // fill in the onClick PendingIntent Template using the specific plant id for each item individually
        Bundle bundle = new Bundle();
        bundle.putLong(PlantDetailActivity.EXTRA_PLANT_ID, plantId);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(bundle);
        remoteViews.setOnClickFillInIntent(R.id.widget_plant_image, fillInIntent);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
