package com.example.tacademy.sampleokhttp1;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongja94 on 2016-02-05.
 */
public class MovieAdapter extends BaseAdapter {
    List<MovieItem> items = new ArrayList<MovieItem>();
    public void addAll(List<MovieItem> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemView view;
        if (convertView == null) {
            view = new ItemView(parent.getContext());
        } else {
            view = (ItemView)convertView;
        }
        view.setMovieItem(items.get(position));
        return view;
    }
}
