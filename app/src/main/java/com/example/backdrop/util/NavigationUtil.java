package com.example.backdrop.util;

import android.app.Activity;
import android.content.Intent;

import com.example.backdrop.ui.AddCategoryActivity;

public class NavigationUtil {

    public static void goToAddCat(Activity activity) {
        Intent intent = new Intent(activity, AddCategoryActivity.class);
        activity.startActivity(intent);
    }
}
