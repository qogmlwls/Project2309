package com.example.project2309.common;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class TextViewStyleManager {


    public static void setErrorStyledText(TextView view, String message) {
        view.setText(message);
        view.setTextColor(Color.RED);
        view.setVisibility(View.VISIBLE);
    }

    public static void clearTextAndStyle(TextView view) {
        view.setText("");
        view.setTextColor(Color.TRANSPARENT);
        view.setVisibility(View.GONE);
    }


}
