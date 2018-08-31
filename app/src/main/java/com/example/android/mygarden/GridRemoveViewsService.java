package com.example.android.mygarden;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class GridRemoveViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new com.example.android.mygarden.ui.RemoteViewsFactory(this.getApplicationContext());
    }
}
