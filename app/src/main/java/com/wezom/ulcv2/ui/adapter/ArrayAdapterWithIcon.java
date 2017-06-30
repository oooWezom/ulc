package com.wezom.ulcv2.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.mvp.model.Category;

import java.util.List;

/**
 * Created by sivolotskiy.v on 14.04.2016.
 */
public class ArrayAdapterWithIcon extends ArrayAdapter<String> {

    Context context;
    int layoutResourceId;
    private List<Category> categories;
    private List<String> names;

    public ArrayAdapterWithIcon(Context context, List<String> items, List<Category> categories) {
        super(context, R.layout.select_dialog_item, items);
        this.categories = categories;
        this.names = items;
        this.context = context;
        layoutResourceId = R.layout.select_dialog_item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CategoryHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new CategoryHolder();
            holder.imgIcon = (ImageView) row.findViewById(R.id.dialog_icons_image);
            holder.txtTitle = (TextView) row.findViewById(R.id.dialog_icons_text);

            row.setTag(holder);
        } else {
            holder = (CategoryHolder) row.getTag();
        }

        holder.txtTitle.setText(names.get(position));
        Picasso.with(getContext()).load(Utils.getCorrectCategoryIconSizeURL(getContext(), false, false) + categories.get(position).getIcon()).into(holder.imgIcon);

        return row;
    }

    static class CategoryHolder {
        ImageView imgIcon;
        TextView txtTitle;
    }
}