package com.example.habitmaster.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.habitmaster.R;
import com.example.habitmaster.utils.AvatarUtils;

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

        Integer resId = AvatarUtils.getAvatarResId(avatarNames[position]);
        if (resId != null) {
            imageView.setImageResource(resId);
        } else {
            imageView.setImageResource(R.drawable.default_avatar);
        }

        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(150, 150));
        return imageView;
    }
}
