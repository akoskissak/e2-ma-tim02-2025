package com.example.habitmaster.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class AvatarSpinnerAdapter extends BaseAdapter {
    private final Context context;
    private final String[] avatarNames;

    public AvatarSpinnerAdapter(Context context, String[] avatarNames) {
        this.context = context;
        this.avatarNames = avatarNames;
    }

    @Override
    public int getCount() {
        return avatarNames.length;
    }

    @Override
    public Object getItem(int position) {
        return avatarNames[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = convertView == null ? new ImageView(context) : (ImageView) convertView;

        // nemamo puno avatara pa .getIdentifier koji pretrazuje resurs po imenu nije tezak zadatak
        @SuppressLint("DiscouragedApi")
        int resId = context.getResources().getIdentifier(avatarNames[position], "drawable", context.getPackageName());
        imageView.setImageResource(resId);
        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(150, 150));
        return imageView;
    }
}
